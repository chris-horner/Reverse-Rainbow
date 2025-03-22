package codes.chrishorner.planner

import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.data.TileFetchResult
import codes.chrishorner.planner.data.fetchTiles
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

@Stable
class GameLoader private constructor(
  private val scope: CoroutineScope,
  private val initialState: LoaderState = LoaderState.Loading,
  private val fetchTiles: suspend () -> TileFetchResult = ::fetchTiles,
  private val clock: Clock = Clock.systemDefaultZone(),
) {

  sealed interface LoaderState {
    data object Loading : LoaderState
    class Success(val date: LocalDate, val game: Game) : LoaderState
    class Failure(val type: FailureType) : LoaderState
  }

  enum class FailureType {
    NETWORK,
    HTTP,
    PARSING,
  }

  private var _state = mutableStateOf(initialState)
  val state: State<LoaderState> = _state

  fun refresh() = scope.launch {
    _state.value = LoaderState.Loading

    val result = fetchTiles()

    _state.value = when (result) {
      is TileFetchResult.Success -> LoaderState.Success(LocalDate.now(clock), Game(result.tiles))
      is TileFetchResult.Failure -> LoaderState.Failure(
        type = when (result) {
          TileFetchResult.HttpFailure -> FailureType.HTTP
          TileFetchResult.NetworkFailure -> FailureType.NETWORK
          TileFetchResult.ParsingFailure -> FailureType.PARSING
        }
      )
    }
  }

  fun refreshIfNecessary() {
    val currentState = _state.value

    if (currentState !is LoaderState.Success || currentState.date != LocalDate.now(clock)) {
      refresh()
    }
  }

  /**
   * Use as little of the ViewModel API as possible to persist the game's loaded state and survive
   * Activity restarts.
   */
  @Suppress("DEPRECATION") // Alternatives only available API 33 and up.
  class ViewModelWrapper(savedStateHandle: SavedStateHandle) : ViewModel() {
    val gameLoader: GameLoader

    init {
      val previousBundle = savedStateHandle.get<Bundle>("wrapper_state")
      val previousTiles = previousBundle?.getParcelableArrayList<Tile>("tiles")
      val previousDate = previousBundle?.getString("date")?.let { LocalDate.parse(it) }
      val initialLoaderState = if (previousTiles != null && previousDate != null) {
        LoaderState.Success(previousDate, Game(previousTiles.toImmutableList()))
      } else {
        LoaderState.Loading
      }

      gameLoader = GameLoader(
        scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main),
        initialState = initialLoaderState,
      )

      savedStateHandle.setSavedStateProvider("wrapper_state") {
        Bundle().apply {
          val loaderState = gameLoader.state.value

          if (loaderState is LoaderState.Success) {
            putParcelableArrayList("tiles", ArrayList(loaderState.game.model.value.tiles))
            putString("date", loaderState.date.toString())
          }
        }
      }
    }
  }
}
