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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Fetches today's Connections game from The New York Times API.
 */
@Stable
class GameLoader(
  private val scope: CoroutineScope,
  private val initialState: LoaderState = LoaderState.Loading,
  private val fetchTiles: suspend () -> TileFetchResult = ::fetchTiles,
  private val clock: Clock = Clock.System,
  private val timeZoneProvider: () -> TimeZone = { TimeZone.currentSystemDefault() },
) {

  sealed interface LoaderState {
    data object Loading : LoaderState
    class Success(val date: LocalDate, val game: Game) : LoaderState
    data class Failure(val type: FailureType) : LoaderState
  }

  enum class FailureType {
    NETWORK,
    HTTP,
    PARSING,
  }

  private var _state = mutableStateOf(initialState)
  val state: State<LoaderState> = _state

  fun refresh() = scope.launch(start = CoroutineStart.UNDISPATCHED) {
    _state.value = LoaderState.Loading

    val result = fetchTiles()
    val currentLocalDate = clock.now().toLocalDateTime(timeZoneProvider()).date

    _state.value = when (result) {
      is TileFetchResult.Success -> LoaderState.Success(currentLocalDate, Game(result.tiles))
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
    val currentLocalDate = clock.now().toLocalDateTime(timeZoneProvider()).date

    if (currentState !is LoaderState.Success || currentState.date != currentLocalDate) {
      refresh()
    }
  }

  /**
   * Use as little of the ViewModel API as possible to persist the game's loaded state and survive
   * Activity restarts.
   */
  class ViewModelWrapper(savedStateHandle: SavedStateHandle) : ViewModel() {
    val gameLoader: GameLoader

    init {
      val previousBundle = savedStateHandle.get<Bundle>("wrapper_state")?.apply {
        classLoader = Tile::class.java.classLoader
      }
      @Suppress("DEPRECATION") // Alternative only available API 33 and up.
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
