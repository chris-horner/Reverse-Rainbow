package codes.chrishorner.planner.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.ui.util.getValue
import codes.chrishorner.planner.ui.util.mutableLongStateFrom
import codes.chrishorner.planner.ui.util.setValue
import kotlin.math.roundToInt

@Composable
fun rememberTileDragState(cards: List<Card>): TileDragState {
  return remember { TileDragState(cards) }
}

@Stable
class TileDragState(private var cards: List<Card>) {
  private val offsetStates = Array(16) { mutableLongStateFrom(IntOffset.Zero) }
  private val draggingStates = Array(16) { mutableStateOf(false) }

  fun updateCards(cards: List<Card>) {
    this.cards = cards
  }

  fun getOffset(card: Card): IntOffset {
    val offset by offsetStates[card.initialPosition]
    return offset
  }

  fun getDragging(card: Card): Boolean {
    return draggingStates[card.initialPosition].value
  }

  fun onDrag(card: Card, dragAmount: Offset) {
    var offset by offsetStates[card.initialPosition]
    offset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
    draggingStates[card.initialPosition].value = true

    if (card.category == null) return

    cards.fastForEach { otherCard ->
      val isOtherCard = otherCard.initialPosition != card.initialPosition
      val isSameCategory = otherCard.category == card.category

      if (isOtherCard && isSameCategory) {
        var otherCardOffset by offsetStates[otherCard.initialPosition]
        otherCardOffset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
        draggingStates[otherCard.initialPosition].value = true
      }
    }
  }

  fun finishDrag() {
    offsetStates.forEach { offsetState ->
      offsetState.longValue = IntOffset.Zero.packedValue
    }

    draggingStates.forEach { draggingState ->
      draggingState.value = false
    }
  }
}