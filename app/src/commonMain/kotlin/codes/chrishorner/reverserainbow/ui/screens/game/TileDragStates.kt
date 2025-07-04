package codes.chrishorner.reverserainbow.ui.screens.game

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
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastForEach
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.ui.util.mutableLongStateFrom
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

/**
 * Keeps track of the drag position and hover state of each tile.
 */
class TileDragStates(
  private val tiles: ImmutableList<Tile>,
  private val onDragOver: (source: Tile, destination: Tile) -> Unit,
) {
  private val states = this@TileDragStates.tiles.map { TileDragState(it) }.toImmutableList()
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
    val state = states.find { it.bounds.contains(position) } ?: return
    state.status = DragStatus.Dragged(
      transformOrigin = TransformOrigin(
        pivotFractionX = (position.x - state.bounds.left) / state.bounds.width,
        pivotFractionY = (position.y - state.bounds.top) / state.bounds.height,
      )
    )

    dragPosition = position
  }

  private fun onDrag(dragAmount: Offset) {
    if (dragPosition.isUnspecified) return

    dragPosition += dragAmount

    // Avoid filtering and mapping the list since we don't want to create garbage collections every
    // frame the tile is dragged.
    val currentDragState = states.fastFirst { it.status is DragStatus.Dragged }
    val currentDragStatus = currentDragState.status as DragStatus.Dragged

    currentDragStatus.offset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())

    var hoveredTile: Tile? = null

    states.fastForEach { dragState ->
      val hovered = dragState.bounds.contains(dragPosition)

      when (dragState.status) {
        is DragStatus.Dragged -> Unit
        is DragStatus.Hovered -> if (!hovered) dragState.status = DragStatus.None
        DragStatus.None -> if (hovered) dragState.status = DragStatus.Hovered
      }

      if (hovered) {
        hoveredTile = dragState.tile
      }
    }

    currentDragStatus.hoveredTile = hoveredTile
  }

  private fun onDragFinish(cancelled: Boolean) {
    if (dragPosition.isSpecified && !cancelled) {
      var source: Tile? = null
      var destination: Tile? = null

      states.fastForEach { state ->
        when (state.status) {
          is DragStatus.Dragged -> source = state.tile
          is DragStatus.Hovered -> destination = state.tile
          DragStatus.None -> Unit
        }
      }

      if (source != null && destination != null) {
        onDragOver(source, destination)
      }
    }

    states.fastForEach { state ->
      state.status = DragStatus.None
    }

    dragPosition = Offset.Unspecified
  }
}

@Stable
class TileDragState(
  val tile: Tile,
) {
  var bounds by mutableStateOf(Rect.Zero)
  var status by mutableStateOf<DragStatus>(DragStatus.None)
}

@Stable
sealed interface DragStatus {
  val offset: IntOffset
    get() = IntOffset.Zero

  val transformOrigin: TransformOrigin
    get() = TransformOrigin.Center

  @Stable
  class Dragged(override val transformOrigin: TransformOrigin) : DragStatus {
    private var offsetState = mutableLongStateFrom(IntOffset.Zero)
    override var offset: IntOffset
      get() = IntOffset(offsetState.longValue)
      set(value) {
        offsetState.longValue = value.packedValue
      }

    var hoveredTile by mutableStateOf<Tile?>(null)
  }

  data object Hovered : DragStatus

  data object None : DragStatus
}
