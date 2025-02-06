package codes.chrishorner.planner

import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.CardFetchResult
import codes.chrishorner.planner.data.fetchCards
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.ArrayList

@Stable
class GameLoader private constructor(
  private val scope: CoroutineScope,
  private val initialState: LoaderState = LoaderState.Idle,
  private val fetchCards: suspend () -> CardFetchResult = ::fetchCards,
) {

  sealed interface LoaderState {
    object Idle : LoaderState
    class Success(val game: Game2) : LoaderState
    class Failure(val type: FailureType) : LoaderState
  }

  enum class FailureType {
    NETWORK,
    HTTP,
    PARSING,
  }

  private var _state = mutableStateOf<LoaderState>(initialState)
  val state: State<LoaderState> = _state

  fun refresh() = scope.launch {
    val result = fetchCards()

    _state.value = when (result) {
      is CardFetchResult.Success -> LoaderState.Success(Game2(result.cards))
      is CardFetchResult.Failure -> LoaderState.Failure(
        type = when (result) {
          CardFetchResult.HttpFailure -> FailureType.HTTP
          CardFetchResult.NetworkFailure -> FailureType.NETWORK
          CardFetchResult.ParsingFailure -> FailureType.PARSING
        }
      )
    }
  }

  class ViewModelWrapper(savedStateHandle: SavedStateHandle) : ViewModel() {
    val gameLoader: GameLoader

    init {
      val previousBundle = savedStateHandle.get<Bundle>("wrapper_state")
      @Suppress("DEPRECATION") // Alternative only available API 33 and up.
      val previousCards = previousBundle?.getParcelableArrayList<Card>("cards")
      val initialLoaderState = if (previousCards != null) {
        LoaderState.Success(Game2(previousCards))
      } else {
        LoaderState.Idle
      }

      gameLoader = GameLoader(
        scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main),
        initialState = initialLoaderState,
      )

      savedStateHandle.setSavedStateProvider("wrapper_state") {
        Bundle().apply {
          val loaderState = gameLoader.state.value

          if (loaderState is LoaderState.Success) {
            putParcelableArrayList("cards", ArrayList(loaderState.game.model.value.cards))
          }
        }
      }
    }
  }
}
