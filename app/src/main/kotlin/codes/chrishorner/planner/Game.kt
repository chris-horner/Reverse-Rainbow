package codes.chrishorner.planner

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.GameState

interface Game {
  val state: State<GameState>
  fun select(card: Card)
  fun submit(category: Category)

  companion object {
    fun from(cards: List<Card>): Game {
      val state = GameState(cards = cards.sortedBy { it.initialPosition })
      return RealGame(state)
    }

    fun from(state: GameState): Game {
      return RealGame(state)
    }
  }
}

private class RealGame(initialState: GameState) : Game {
  private var _state = mutableStateOf(initialState)
  override val state: State<GameState> = _state

  private val cards = initialState.cards.toMutableList()
  private var selectionCount: Int = initialState.selectionCount
  private val categoryAssignments = initialState.categoryAssignments.toMutableMap()

  override fun select(card: Card) {
    if (card.selected == false && selectionCount >= 4) {
      return
    }

    if (card.category != null) return

    if (card.selected) {
      selectionCount--
    } else {
      selectionCount++
    }

    cards[card.currentPosition] = card.copy(selected = !card.selected)

    publishStateUpdate()
  }

  override fun submit(category: Category) {
    val selectedCards = cards.filter { it.selected }
    val firstUncategorizedPosition = cards.first { it.category == null }.currentPosition

    for ((index, proposedCard) in selectedCards.withIndex()) {
      val position = firstUncategorizedPosition + index
      val currentCard = cards[position]
      val proposedCardPosition = proposedCard.currentPosition

      cards[position] = proposedCard.copy(currentPosition = position, category = category, selected = false)

      if (currentCard != proposedCard) {
        cards[proposedCardPosition] = currentCard.copy(currentPosition = proposedCardPosition)
      }
    }

    selectionCount = 0
    categoryAssignments[category] = !categoryAssignments.getValue(category)

    publishStateUpdate()
  }

  private fun publishStateUpdate() {
    _state.value = GameState(
      cards = cards.toList(),
      selectionCount = selectionCount,
      categoryAssignments = categoryAssignments.toMap(),
    )
  }
}