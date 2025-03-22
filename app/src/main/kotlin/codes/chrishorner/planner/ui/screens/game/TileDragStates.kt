package codes.chrishorner.planner.ui.screens.game

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastZip
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.ui.util.mutableLongStateFrom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

@Composable
fun rememberTileDragStates(
  tiles: ImmutableList<Tile>,
  onDragOver: (source: Tile, destination: Tile) -> Unit,
): TileDragStates {
  return remember(tiles, onDragOver) { TileDragStates(tiles, onDragOver) }
}

class TileDragStates(
  private val tiles: ImmutableList<Tile>,
  private val onDragOver: (source: Tile, destination: Tile) -> Unit,
) {
  private val states = this@TileDragStates.tiles.map { TileDragState() }.toImmutableList()
  private var dragPosition = Offset.Unspecified

  operator fun get(index: Int): TileDragState {
    return states[index]
  }

  suspend fun detectDragGestures(scope: PointerInputScope) {
    scope.detectDragGestures(
      onDragStart = { position -> onDragStart(position) },
      onDrag = { _, dragAmount -> onDrag(dragAmount) },
      onDragEnd = { onDragFinish(cancelled = false) },
      onDragCancel = { onDragFinish(cancelled = true) },
    )
  }

  private fun onDragStart(position: Offset) {
    val tile = tiles.find { states[it.currentPosition].bounds.contains(position) } ?: return
    val state = states[tile.currentPosition]

    state.dragging = true
    state.transformOrigin = TransformOrigin(
      pivotFractionX = (position.x - state.bounds.left) / state.bounds.width,
      pivotFractionY = (position.y - state.bounds.top) / state.bounds.height,
    )

    dragPosition = position
  }

  private fun onDrag(dragAmount: Offset) {
    if (dragPosition.isUnspecified) return

    dragPosition += dragAmount

    states.fastForEach { dragState ->
      if (dragState.dragging) {
        dragState.offset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
      } else {
        dragState.highlight = dragState.bounds.contains(dragPosition)
      }
    }
  }

  private fun onDragFinish(cancelled: Boolean) {
    if (dragPosition.isSpecified && !cancelled) {
      var source: Tile? = null
      var destination: Tile? = null

      states.fastZip(tiles) { state, tile ->
        if (state.dragging) source = tile
        if (state.highlight) destination = tile
      }

      if (source != null && destination != null) {
        onDragOver(source, destination)
      }
    }

    states.fastForEach { state ->
      state.offset = IntOffset.Zero
      state.dragging = false
      state.highlight = false
      state.transformOrigin = TransformOrigin.Center
    }

    dragPosition = Offset.Unspecified
  }
}

@Stable
class TileDragState {
  private var offsetState = mutableLongStateFrom(IntOffset.Zero)

  var dragging: Boolean by mutableStateOf(false)
  var bounds by mutableStateOf(Rect.Zero)
  var highlight by mutableStateOf(false)
  var transformOrigin by mutableStateOf(TransformOrigin.Center)
  var offset: IntOffset
    get() = IntOffset(offsetState.longValue)
    set(value) {
      offsetState.longValue = value.packedValue
    }
}