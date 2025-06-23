package codes.chrishorner.planner

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedState
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import codes.chrishorner.planner.GameLoader.LoaderState
import codes.chrishorner.planner.data.Tile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDate

/**
 * Use as little of the ViewModel API as possible to persist the game's loaded state and survive
 * Activity restarts.
 */
class GameLoaderViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
  val gameLoader: GameLoader

  init {
    val savedTiles: List<Tile>? = savedStateHandle
      .get<Bundle>("tiles")
      ?.let { decodeFromSavedState(it) }

    val savedDate: LocalDate? = savedStateHandle
      .get<Bundle>("date")
      ?.let { decodeFromSavedState(it) }

    val initialLoaderState = if (savedTiles != null && savedDate != null) {
      LoaderState.Success(savedDate, Game(savedTiles.toImmutableList()))
    } else {
      LoaderState.Loading
    }

    gameLoader = GameLoader(
      scope = CoroutineScope(viewModelScope.coroutineContext),
      initialState = initialLoaderState,
    )

    savedStateHandle.setSavedStateProvider("tiles") {
      val currentTiles = gameLoader.currentTiles

      if (currentTiles != null) {
        encodeToSavedState(currentTiles.toList())
      } else {
        SavedState.EMPTY
      }
    }

    savedStateHandle.setSavedStateProvider("date") {
      val currentDate = gameLoader.currentDate

      if (currentDate != null) {
        encodeToSavedState(currentDate)
      } else {
        SavedState.EMPTY
      }
    }
  }

  private val GameLoader.currentTiles: ImmutableList<Tile>?
    get() = (state.value as? LoaderState.Success)?.game?.model?.value?.tiles

  private val GameLoader.currentDate: LocalDate?
    get() = (state.value as? LoaderState.Success)?.date
}