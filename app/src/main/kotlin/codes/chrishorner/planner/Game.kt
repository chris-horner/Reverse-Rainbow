package codes.chrishorner.planner

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.CategoryStatus
import codes.chrishorner.planner.data.GameModel
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

class Game2(cards: List<Card>) {
  private val cards = cards.toMutableList()

  private val _model: MutableState<GameModel>
  val model: State<GameModel>

  init {
    require(cards.size == 16) {
      "inputCards must be size 16, but was ${cards.size}"
    }

    val selectionCount = cards.count { it.selected }
    require(selectionCount in 0..4) {
      "Number of selected cards must be within 0 and 4, but was $selectionCount"
    }

    for (category in Category.entries) {
      val cardCountInCategory = cards.count { it.category == category }
      require(cardCountInCategory == 0 || cardCountInCategory == 4) {
        "Category $category must have 0 or assigned 4 cards, but had $cardCountInCategory"
      }
    }

    _model = mutableStateOf(generateModel())
    model = _model
  }

  fun select(card: Card) {
    val selectionCount = cards.count { it.selected }

    // Prevent selecting more than 4 cards.
    if (!card.selected && selectionCount >= 4) return

    // Prevent selecting cards in more than one category at once.
    if (cards.any { it.selected && it.category != null } && card.category != null) return

    // If the first card selected already has a category, select all cards in that category.
    if (selectionCount == 0 && card.category != null) {
      val category = card.category
      cards.replaceAll { it.copy(selected = it.category == category) }
      return
    } else {
      // Otherwise just select the card like normal.
      cards[card.currentPosition] = card.copy(selected = !card.selected)
    }

    publishModelUpdate()
  }

  fun select(selectedCategory: Category) {
    val status = determineCategoryStatus(selectedCategory)

    when (status) {
      CategoryStatus.DISABLED -> return

      CategoryStatus.ENABLED -> {
        val selectedCards = cards.filter { it.selected }

        for (card in selectedCards) {
          val updatedCard = card.copy(category = selectedCategory, selected = false)
          val firstUncategorizedCard = cards.first { it.category == null }
          swapCards(updatedCard, firstUncategorizedCard)
        }
      }

      // BUG:
      // If you clear the current lowest selected category, the one above will reorder itself.
      CategoryStatus.CLEARABLE -> {
        cards.replaceAll { card ->
          if (card.category == selectedCategory) card.copy(category = null) else card
        }

        for (card in cards) {
          if (card.category == null) continue

          val firstUncategorizedCard = cards.first { it.category == null }
          swapCards(card, firstUncategorizedCard)
        }
      }

      CategoryStatus.SWAPPABLE -> {

      }
    }

    publishModelUpdate()
  }

  private fun swapCards(card1: Card, card2: Card) {
    if (card1 == card2) return

    val card1Position = card1.currentPosition
    val card2Position = card2.currentPosition
    cards[card1Position] = card2.copy(currentPosition = card1Position)
    cards[card2Position] = card1.copy(currentPosition = card2Position)
  }

  private fun publishModelUpdate() {
    _model.value = generateModel()
  }

  private fun generateModel(): GameModel {
    val categoryStatuses = Category.entries.associateWith { determineCategoryStatus(it) }
    return GameModel(cards.toList(), categoryStatuses)
  }

  private fun determineCategoryStatus(category: Category): CategoryStatus {
    val selectionCount = cards.count { it.selected }

    return when {
      selectionCount == 4 -> when {
        cards.none { it.category == category } -> CategoryStatus.ENABLED
        cards.count { it.category != category } == 4 -> CategoryStatus.SWAPPABLE
        cards.count { it.category == category } < 4 -> CategoryStatus.SWAPPABLE
        else -> CategoryStatus.DISABLED
      }

      cards.any { it.category == category } -> CategoryStatus.CLEARABLE
      else -> CategoryStatus.DISABLED
    }
  }
}

private class RealGame(initialState: GameState) : Game {
  private var _state = mutableStateOf(initialState)
  override val state: State<GameState> = _state

  private val cards = initialState.cards.toMutableList()
  private var selectionCount: Int = initialState.selectionCount
  private val categoryStates = initialState.categoryStatuses.toMutableMap()

  override fun select(card: Card) {
    if (!card.selected && selectionCount >= 4) {
      return
    }

    if (card.category != null) return

    if (card.selected) {
      selectionCount--
    } else {
      selectionCount++
    }

    cards[card.currentPosition] = card.copy(selected = !card.selected)

    if (selectionCount == 4) {
      categoryStates.replaceAll { category, state ->
        state.copy(
          status = if (cards.any { it.category == category }) {
            CategoryStatus.ENABLED
          } else if (state.assigned) {
            CategoryStatus.CLEARABLE
          } else {
            CategoryStatus.ENABLED
          }
        )
      }
    }

    publishStateUpdate()
  }

  override fun submit(selectedCategory: Category) {
    check(selectionCount == 4) {
      "Attempting to submit a category with selectionCount: $selectionCount"
    }

    val selectedCards = cards.filter { it.selected }
    val firstUncategorizedPosition = cards.first { it.category == null }.currentPosition

    for ((index, proposedCard) in selectedCards.withIndex()) {
      val position = firstUncategorizedPosition + index
      val currentCard = cards[position]
      val proposedCardPosition = proposedCard.currentPosition

      cards[position] =
        proposedCard.copy(currentPosition = position, category = selectedCategory, selected = false)

      if (currentCard != proposedCard) {
        cards[proposedCardPosition] = currentCard.copy(currentPosition = proposedCardPosition)
      }
    }

    selectionCount = 0
    categoryStates.replaceAll { category, state ->
      state.copy(
        assigned = state.assigned || category == selectedCategory,
        status = if (state.assigned || category == selectedCategory) {
          CategoryStatus.CLEARABLE
        } else {
          CategoryStatus.DISABLED
        }
      )
    }

    publishStateUpdate()
  }

  private fun publishStateUpdate() {
    _state.value = GameState(
      cards = cards.toList(),
      selectionCount = selectionCount,
      //categoryAssignments = categoryAssignments.toMap(),
    )
  }
}