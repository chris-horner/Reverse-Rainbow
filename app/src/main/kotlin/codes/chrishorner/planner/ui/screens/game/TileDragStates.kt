package codes.chrishorner.planner.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import codes.chrishorner.planner.data.Card
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

  operator fun get(index: Int): TileDragState {
    return states[index]
  }

  fun onDragStart(position: Offset) {
    val card = cards.find { states[it.currentPosition].bounds.contains(position) } ?: return
    states[card.currentPosition].dragging = true

    if (card.category == null) return

    cards.fastForEach { otherCard ->
      val isOtherCard = otherCard.currentPosition != card.currentPosition
      val isSameCategory = otherCard.category == card.category

      if (isOtherCard && isSameCategory) {
        states[otherCard.currentPosition].dragging = true
      }
    }
  }

  fun onDrag(dragAmount: Offset) {
    states.fastForEach { state ->
      if (state.dragging) {
        state.offset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
      }
    }
  }

  fun onDragFinish() {
    states.fastForEach { state ->
      state.offset = IntOffset.Zero
      state.dragging = false
    }
  }
}

@Stable
class TileDragState {
  private var offsetState = mutableLongStateFrom(IntOffset.Zero)
  private var draggingState = mutableStateOf(false)
  private var boundsStates = mutableStateOf(Rect.Zero)

  var offset: IntOffset
    get() = IntOffset(offsetState.longValue)
    set(value) {
      offsetState.longValue = value.packedValue
    }

  var dragging: Boolean by draggingState

  var bounds by boundsStates
}