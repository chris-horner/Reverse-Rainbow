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
import androidx.compose.ui.util.fastForEachIndexed
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.ui.util.mutableLongStateFrom
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

@Composable
fun rememberTileDragStates(cards: ImmutableList<Card>): TileDragStates {
  return remember(cards) { TileDragStates(cards) }
}

class TileDragStates(private val cards: ImmutableList<Card>) {
  private val states = cards.map { TileDragState() }.toImmutableList()
  private var dragPosition = Offset.Unspecified

  operator fun get(index: Int): TileDragState {
    return states[index]
  }

  fun onDragStart(position: Offset) {
    val card = cards.find { states[it.currentPosition].bounds.contains(position) } ?: return
    val state = states[card.currentPosition]
    state.dragging = true
    states[card.currentPosition].transformOrigin = TransformOrigin(
      pivotFractionX = (position.x - state.bounds.left) / state.bounds.width,
      pivotFractionY = (position.y - state.bounds.top) / state.bounds.height,
    )

    dragPosition = position

    if (card.category == null) return

    cards.fastForEach { otherCard ->
      val isOtherCard = otherCard.currentPosition != card.currentPosition
      val isSameCategory = otherCard.category == card.category

      if (isOtherCard && isSameCategory) {
        val state = states[otherCard.currentPosition]
        state.dragging = true
        state.transformOrigin = TransformOrigin(
          pivotFractionX = (position.x - state.bounds.left) / state.bounds.width,
          pivotFractionY = (position.y - state.bounds.top) / state.bounds.height,
        )
      }
    }
  }

  fun onDrag(dragAmount: Offset) {
    if (dragPosition.isUnspecified) return

    dragPosition += dragAmount
    var highlightCategory: Category? = null

    states.fastForEachIndexed { index, dragState ->
      when {
        // If the drag state of this entry is dragging, all we need to do is move it.
        dragState.dragging -> {
          dragState.offset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
        }

        // If we're not being dragging, then check if the drag position is currently hovering over
        // this entry. If it is, then highlight it, and all entries in the same category.
        dragState.bounds.contains(dragPosition) -> {
          dragState.highlight = true
          val hoveredCard = cards[index]

          if (hoveredCard.category != null) {
            highlightCategory = hoveredCard.category

            cards.fastForEachIndexed { highlightIndex, card ->
              if (card.category == hoveredCard.category) {
                states[highlightIndex].highlight = true
              }
            }
          }
        }

        // If the entry isn't being hovered over and its category doesn't match the current
        // highlightCategory, then unhighlight it.
        cards[index].category != highlightCategory || cards[index].category == null -> {
          dragState.highlight = false
        }
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