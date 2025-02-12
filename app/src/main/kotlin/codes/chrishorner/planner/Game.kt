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

    when {
      // If the first card selected already has a category, select all cards in that category.
      card.category != null && selectionCount == 0 -> {
        cards.replaceAll { it.copy(selected = it.category == card.category) }
      }

      // If there are several cards selected of the same category, deselect them all except the
      // current card.
      card.category != null && cards.count { it.selected && it.category == card.category } > 1 -> {
        cards.replaceAll { if (it != card) it.copy(selected = false) else card }
      }

      // Otherwise just select the card like normal.
      else -> {
        cards[card.currentPosition] = card.copy(selected = !card.selected)
      }
    }

    publishModelUpdate()
  }

  fun select(selectedCategory: Category) {
    val status = determineCategoryStatus(selectedCategory)

    when (status) {
      CategoryStatus.DISABLED -> return

      CategoryStatus.ENABLED -> {
        val selectedCards = cards.filter { it.selected }
        val firstUncategorizedRow = cards
          .chunked(4)
          .first { row -> row.all { it.category == null } }
        var swapPosition = firstUncategorizedRow.first().currentPosition

        for (card in selectedCards) {
          val updatedCard = card.copy(category = selectedCategory, selected = false)
          cards[updatedCard.currentPosition] = updatedCard
          val cardToSwap = cards[swapPosition]
          swapCards(updatedCard, cardToSwap)
          swapPosition++
        }
      }

      CategoryStatus.CLEARABLE -> {
        cards.replaceAll { card ->
          card.copy(
            category = if (card.category == selectedCategory) null else card.category
          )
        }

        for (rowStartIndex in 0..12 step 4) {
          val row = cards.subList(rowStartIndex, rowStartIndex + 4)
          // If some cards in a row have a category and some do not, sort them to ensure no gaps.
          if (row.any { it.category != null } && row.any { it.category == null }) {
            for ((rowIndex, card) in row.sortedBy { it.category }.withIndex()) {
              val cardIndex = rowStartIndex + rowIndex
              cards[cardIndex] = card.copy(currentPosition = cardIndex)
            }
          }

          // Check that there will actually be a next row.
          if (rowStartIndex > 8) continue

          // If this entire row has no category, and the next row _does_ have a category, swap all
          // cards from the next row that do have an assigned category.
          val nextRow = cards.subList(rowStartIndex + 4, rowStartIndex + 8)
          if (row.all { it.category == null } && nextRow.any { it.category != null }) {
            row.zip(nextRow)
              .filter { (_, card2) -> card2.category != null }
              .forEach { (card1, card2) ->
                swapCards(card1, card2)
              }
          }
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
      selectionCount > 0 -> when {
        cards.filter { it.selected }.all { it.category == category } -> CategoryStatus.CLEARABLE
        cards.filter { it.selected }
          .any { it.category != category && it.category != null } -> CategoryStatus.SWAPPABLE
        cards.none { it.category == category } -> CategoryStatus.ENABLED
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