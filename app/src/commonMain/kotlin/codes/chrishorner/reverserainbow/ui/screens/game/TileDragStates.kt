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
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.ui.util.mutableLongStateFrom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

@Composable
fun rememberTileDragStates(
  tiles: ImmutableList<Tile>,
  onDragOver: (source: Tile, destination: Tile) -> Unit,
  onRowDragOver: (source: Tile, destinationRowTile: Tile) -> Unit,
): TileDragStates {
  return remember(tiles, onDragOver, onRowDragOver) { TileDragStates(tiles, onDragOver, onRowDragOver) }
}

private sealed interface DragMode {
  data object Single : DragMode
  data class Row(
    val sourceRowStart: Int,
    val sourceCategory: Category,
    var hoveredRowStart: Int? = null,
  ) : DragMode
}

/**
 * Keeps track of the drag position and hover state of each tile.
 */
class TileDragStates(
  private val tiles: ImmutableList<Tile>,
  private val onDragOver: (source: Tile, destination: Tile) -> Unit,
  private val onRowDragOver: (source: Tile, destinationRowTile: Tile) -> Unit,
) {
  private val states = this@TileDragStates.tiles.map { TileDragState(it) }.toImmutableList()
  private var dragPosition = Offset.Unspecified
  private var dragMode: DragMode? = null
  private var sourceBounds = Rect.Zero

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
    val tile = state.tile

    state.status = DragStatus.Dragged(
      transformOrigin = TransformOrigin(
        pivotFractionX = (position.x - state.bounds.left) / state.bounds.width,
        pivotFractionY = (position.y - state.bounds.top) / state.bounds.height,
      )
    )

    dragPosition = position
    sourceBounds = state.bounds

    if (tile.category != null && !tile.selected) {
      val sourceRowStart = (tile.currentPosition / 4) * 4
      for (i in 0..3) {
        val pos = sourceRowStart + i
        if (pos != tile.currentPosition) {
          states[pos].status = DragStatus.Companion
        }
      }
      dragMode = DragMode.Row(
        sourceRowStart = sourceRowStart,
        sourceCategory = tile.category,
      )
    } else {
      dragMode = DragMode.Single
    }
  }

  private fun onDrag(dragAmount: Offset) {
    if (dragPosition.isUnspecified) return

    dragPosition += dragAmount

    // Avoid filtering and mapping the list since we don't want to create garbage collections every
    // frame the tile is dragged.
    val currentDragState = states.fastFirst { it.status is DragStatus.Dragged }
    val currentDragStatus = currentDragState.status as DragStatus.Dragged

    val dragDelta = IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
    currentDragStatus.offset += dragDelta

    when (val mode = dragMode) {
      is DragMode.Single -> onDragSingle()
      is DragMode.Row -> {
        // Companions follow the drag when not hovering another row.
        if (mode.hoveredRowStart == null) {
          for (i in 0..3) {
            val state = states[mode.sourceRowStart + i]
            if (state.status is DragStatus.Companion) {
              state.previewOffset.longValue =
                (IntOffset(state.previewOffset.longValue) + dragDelta).packedValue
            }
          }
        }
        onDragRow(currentDragState, mode)
      }
      null -> Unit
    }
  }

  private fun onDragSingle() {
    states.fastForEach { dragState ->
      val hovered = dragState.bounds.contains(dragPosition)

      when (dragState.status) {
        is DragStatus.Dragged -> Unit
        is DragStatus.Hovered -> if (!hovered) {
          dragState.status = DragStatus.None
          dragState.previewOffset.longValue = IntOffset.Zero.packedValue
        }
        DragStatus.None -> if (hovered) {
          dragState.status = DragStatus.Hovered
          dragState.previewOffset.longValue = IntOffset(
            (sourceBounds.left - dragState.bounds.left).roundToInt(),
            (sourceBounds.top - dragState.bounds.top).roundToInt(),
          ).packedValue
        }
        else -> Unit
      }
    }
  }

  private fun onDragRow(currentDragState: TileDragState, mode: DragMode.Row) {
    val hoveredState = states.find { it.bounds.contains(dragPosition) }
    val hoveredTile = hoveredState?.tile

    val hoveredRowStart = if (hoveredTile != null) {
      (hoveredTile.currentPosition / 4) * 4
    } else {
      null
    }

    val effectiveHoveredRowStart = if (hoveredRowStart == mode.sourceRowStart) null else hoveredRowStart

    if (effectiveHoveredRowStart == mode.hoveredRowStart) return

    val currentDragOffset = (currentDragState.status as DragStatus.Dragged).offset

    // Clear previous hovered row.
    val previousHoveredRowStart = mode.hoveredRowStart
    if (previousHoveredRowStart != null) {
      for (i in 0..3) {
        val state = states[previousHoveredRowStart + i]
        if (state.status is DragStatus.RowHovered) {
          state.status = DragStatus.None
          state.previewOffset.longValue = IntOffset.Zero.packedValue
        }
      }
    }

    mode.hoveredRowStart = effectiveHoveredRowStart

    if (effectiveHoveredRowStart != null) {
      // Hovering a new row: companions preview to dest positions, dest tiles preview to source.
      for (i in 0..3) {
        val sourceState = states[mode.sourceRowStart + i]
        val destState = states[effectiveHoveredRowStart + i]

        // Companion tiles get preview offset to destination position (skip the Dragged tile).
        if (sourceState.status is DragStatus.Companion) {
          sourceState.previewOffset.longValue = IntOffset(
            (destState.bounds.left - sourceState.bounds.left).roundToInt(),
            (destState.bounds.top - sourceState.bounds.top).roundToInt(),
          ).packedValue
        }

        // Destination tiles get RowHovered status and preview offset to source position.
        destState.status = DragStatus.RowHovered
        destState.previewOffset.longValue = IntOffset(
          (sourceState.bounds.left - destState.bounds.left).roundToInt(),
          (sourceState.bounds.top - destState.bounds.top).roundToInt(),
        ).packedValue
      }
    } else {
      // Left hover: resume companion drag following.
      for (i in 0..3) {
        val state = states[mode.sourceRowStart + i]
        if (state.status is DragStatus.Companion) {
          state.previewOffset.longValue = currentDragOffset.packedValue
        }
      }
    }
  }

  private fun onDragFinish(cancelled: Boolean) {
    if (dragPosition.isSpecified && !cancelled) {
      when (val mode = dragMode) {
        is DragMode.Single -> {
          var source: Tile? = null
          var destination: Tile? = null

          states.fastForEach { state ->
            when (state.status) {
              is DragStatus.Dragged -> source = state.tile
              is DragStatus.Hovered -> destination = state.tile
              else -> Unit
            }
          }

          if (source != null && destination != null) {
            onDragOver(source, destination)
          }
        }
        is DragMode.Row -> {
          if (mode.hoveredRowStart != null) {
            val source = states.fastFirst { it.status is DragStatus.Dragged }.tile
            val destTile = states[mode.hoveredRowStart!!].tile
            onRowDragOver(source, destTile)
          }
        }
        null -> Unit
      }
    }

    states.fastForEach { state ->
      state.status = DragStatus.None
      state.previewOffset.longValue = IntOffset.Zero.packedValue
    }

    dragPosition = Offset.Unspecified
    dragMode = null
  }
}

@Stable
class TileDragState(
  val tile: Tile,
) {
  var bounds by mutableStateOf(Rect.Zero)
  var status by mutableStateOf<DragStatus>(DragStatus.None)
  val previewOffset = mutableLongStateFrom(IntOffset.Zero)
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
  }

  data object Hovered : DragStatus

  data object Companion : DragStatus

  data object RowHovered : DragStatus

  data object None : DragStatus
}
