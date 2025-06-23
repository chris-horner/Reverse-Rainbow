package codes.chrishorner.planner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedState
import androidx.savedstate.serialization.encodeToSavedState
import codes.chrishorner.planner.GameLoader.LoaderState
import codes.chrishorner.planner.data.Tile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDate

/**
 * Use as little of the ViewModel API as possible to persist the game's loaded state and survive
 * Activity restarts.
 */
class GameLoaderViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
  val gameLoader: GameLoader

  init {
    val previousTiles = savedStateHandle.get<ImmutableList<Tile>?>("tiles")
    val previousDate = savedStateHandle.get<LocalDate>("date")

    val initialLoaderState = if (previousTiles != null && previousDate != null) {
      LoaderState.Success(previousDate, Game(previousTiles))
    } else {
      LoaderState.Loading
    }

    gameLoader = GameLoader(
      scope = CoroutineScope(viewModelScope.coroutineContext),
      initialState = initialLoaderState,
    )

    savedStateHandle.setSavedStateProvider("tiles") {
      val loaderState = gameLoader.state.value

      if (loaderState is LoaderState.Success) {
        encodeToSavedState(loaderState.game.model.value.tiles)
      } else {
        SavedState.EMPTY
      }
    }

    savedStateHandle.setSavedStateProvider("date") {
      val loaderState = gameLoader.state.value

      if (loaderState is LoaderState.Success) {
        encodeToSavedState(loaderState.date)
      } else {
        SavedState.EMPTY
      }
    }
  }
}