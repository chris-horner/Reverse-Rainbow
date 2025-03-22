package codes.chrishorner.planner.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.ui.util.mutableLongStateFrom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

@Composable
fun rememberTileDragStates(tiles: ImmutableList<Tile>): TileDragStates {
  return remember(tiles) { TileDragStates(tiles) }
}

class TileDragStates(private val tiles: ImmutableList<Tile>) {
  private val states = this@TileDragStates.tiles.map { TileDragState() }.toImmutableList()
  private var dragPosition = Offset.Unspecified

  operator fun get(index: Int): TileDragState {
    return states[index]
  }

  fun onDragStart(position: Offset) {
    val tile = tiles.find { states[it.currentPosition].bounds.contains(position) } ?: return
    val state = states[tile.currentPosition]

    state.dragging = true
    state.transformOrigin = TransformOrigin(
      pivotFractionX = (position.x - state.bounds.left) / state.bounds.width,
      pivotFractionY = (position.y - state.bounds.top) / state.bounds.height,
    )

    dragPosition = position
  }

  fun onDrag(dragAmount: Offset) {
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

  fun onDragFinish() {
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