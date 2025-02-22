package codes.chrishorner.planner

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.CategoryStatus
import codes.chrishorner.planner.data.GameModel

class Game(cards: List<Card>) {
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
      card.category != null -> when {
        // If the first card selected already has a category, select all cards in that category.
        selectionCount == 0 -> {
          cards.replaceAll { it.copy(selected = it.category == card.category) }
        }

        // If there are several cards selected of the same category, deselect them all except the
        // current card.
        cards.filter { it.category == card.category }.all { it.selected } && selectionCount > 1 -> {
          cards.replaceAll { if (it != card) it.copy(selected = false) else card }
        }

        // Otherwise just select the card like normal.
        else -> {
          cards[card.currentPosition] = card.copy(selected = !card.selected)
        }
      }

      // Otherwise just select the card like normal.
      else -> {
        cards[card.currentPosition] = card.copy(selected = !card.selected)
      }
    }

    publishModelUpdate()
  }

  fun select(category: Category) {
    val status = determineCategoryStatus(category)

    when (status) {
      CategoryStatus.DISABLED -> return
      CategoryStatus.ENABLED -> assignCards(category)
      CategoryStatus.CLEARABLE -> clearCards(category)
      CategoryStatus.SWAPPABLE -> swapSelectedToCategory(category)
    }

    cards.replaceAll { it.copy(selected = false) }
    sortGrid()
    publishModelUpdate()
  }

  private fun assignCards(selectedCategory: Category) {
    val selectedCards = cards.filter { it.selected }
    val categoryHasAssignedCards = cards.any { it.category == selectedCategory }

    val row = if (categoryHasAssignedCards) {
      cards.chunked(4).single { row -> row.any { it.category == selectedCategory } }
    } else {
      cards.chunked(4).first { row -> row.all { it.category == null } }
    }

    var swapPosition = if (categoryHasAssignedCards) {
      row.first { it.category == null }.currentPosition
    } else {
      row.first().currentPosition
    }

    for (card in selectedCards) {
      val currentCard = cards.single { it.initialPosition == card.initialPosition }
      val updatedCard = currentCard.copy(category = selectedCategory)
      cards[updatedCard.currentPosition] = updatedCard
      val cardToSwap = cards[swapPosition]
      swapCards(updatedCard, cardToSwap)
      swapPosition++
    }
  }

  private fun clearCards(selectedCategory: Category) {
    if (cards.any { it.selected }) {
      // If any cards are selected, only clear those cards.
      cards.replaceAll { card ->
        if (card.selected && card.category == selectedCategory) card.copy(category = null) else card
      }
    } else {
      // Otherwise clear everything in this category.
      cards.replaceAll { card ->
        card.copy(category = if (card.category == selectedCategory) null else card.category)
      }
    }
  }

  private fun swapSelectedToCategory(selectedCategory: Category) {
    val selectedCards = cards.filter { it.selected }
    val selectedCardsCategory = selectedCards.map { it.category }.distinct().single()
    val cardsInCategory = cards.filter { it.category == selectedCategory }

    for (card in selectedCards) {
      cards[card.currentPosition] = card.copy(category = selectedCategory)
    }

    for (card in cardsInCategory) {
      cards[card.currentPosition] = card.copy(category = selectedCardsCategory)
    }
  }

  private fun swapCards(card1: Card, card2: Card) {
    if (card1 == card2) return

    val card1Position = card1.currentPosition
    val card2Position = card2.currentPosition
    cards[card1Position] = card2.copy(currentPosition = card1Position)
    cards[card2Position] = card1.copy(currentPosition = card2Position)
  }

  /**
   * For each row, ensure that cards with an assigned category are positioned before cards without
   * a category.
   *
   * For all rows, ensure that those with assigned categories are positioned higher than those
   * without categories.
   */
  private fun sortGrid() {
    for (rowStartIndex in 0..12 step 4) {
      val row = cards.subList(rowStartIndex, rowStartIndex + 4)

      // If some cards in a row have a category and some do not, sort them to remove any gaps.
      if (row.any { it.category != null } && row.any { it.category == null }) {
        for ((rowIndex, card) in row.sortedByDescending { it.category }.withIndex()) {
          val cardIndex = rowStartIndex + rowIndex
          cards[cardIndex] = card.copy(currentPosition = cardIndex)
        }
      }

      // Check that there will actually be a next row before attempting further sorting.
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

  private fun publishModelUpdate() {
    _model.value = generateModel()
  }

  private fun generateModel(): GameModel {
    val categoryStatuses = Category.entries.associateWith { determineCategoryStatus(it) }
    return GameModel(cards.toList(), categoryStatuses)
  }

  /**
   * Look at the current state of the board - including currently assigned categories and selected
   * cards. Use this to work out what the current valid action is for a given category.
   */
  private fun determineCategoryStatus(category: Category): CategoryStatus {
    val selectedCards = cards.filter { it.selected }
    val selectionCount = selectedCards.count()
    val categorySelected = selectedCards.any { it.category == category }
    val cardsInCategoryCount = cards.count { it.category == category }
    val otherCategorySelectionCount = selectedCards
      .filter { it.category != null && it.category != category }
      .distinctBy { it.category }
      .count()
    val allOfOneOtherCategorySelected = selectedCards
      .all { it.category != category && it.category != null } && otherCategorySelectionCount == 1

    return when {
      selectionCount > 0 -> when {
        cardsInCategoryCount + selectionCount <= 4 && !categorySelected -> CategoryStatus.ENABLED
        cardsInCategoryCount > 0 && allOfOneOtherCategorySelected -> CategoryStatus.SWAPPABLE
        selectedCards.all { it.category == category } -> CategoryStatus.CLEARABLE
        else -> CategoryStatus.DISABLED
      }

      cards.any { it.category == category } -> CategoryStatus.CLEARABLE
      else -> CategoryStatus.DISABLED
    }
  }
}
