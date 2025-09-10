package codes.chrishorner.reverserainbow

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.data.CategoryStatus
import codes.chrishorner.reverserainbow.data.GameModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

/**
 * The current state of the Connections board and logic to manipulate it.
 */
@Stable
class Game(tiles: ImmutableList<Tile>) {
  private val tiles = tiles.toMutableList()

  private val _model: MutableState<GameModel>
  val model: State<GameModel>

  init {
    require(tiles.size == 16) {
      "tiles must be size 16, but was ${tiles.size}"
    }

    val selectionCount = tiles.count { it.selected }
    require(selectionCount in 0..4) {
      "Number of selected tiles must be within 0 and 4, but was $selectionCount"
    }

    for (category in Category.entries) {
      val tileCountInCategory = tiles.count { it.category == category }
      require(tileCountInCategory in 0..4) {
        "Category $category must have between 0 to 4 tiles, but had $tileCountInCategory"
      }
    }

    _model = mutableStateOf(generateModel())
    model = _model
  }

  fun select(tile: Tile) {
    val selectionCount = tiles.count { it.selected }

    // Prevent selecting more than 4 tiles.
    if (!tile.selected && selectionCount >= 4) return

    tiles[tile.currentPosition] = tile.copy(selected = !tile.selected)
    publishModelUpdate()
  }

  fun longSelect(tile: Tile) {
    if (tile.category != null) {
      // Select (or deselect) all tiles in the long-selected tile's category.
      tiles.replaceAll { it.copy(selected = !tile.selected && it.category == tile.category) }
      publishModelUpdate()
    }
  }

  fun selectAll(category: Category) {
    val categoryStatus = determineCategoryStatus(category)

    if (categoryStatus.allSelected) {
      // If all tiles in the category are already selected, deselect them.
      tiles.replaceAll { if (it.category == category) it.copy(selected = false) else it }
    } else {
      // Otherwise select _only_ these tiles.
      tiles.replaceAll { it.copy(selected = it.category == category) }
    }

    publishModelUpdate()
  }

  fun applyCategoryAction(category: Category) {
    val action = determineCategoryStatus(category).action

    when (action) {
      CategoryAction.DISABLED -> return
      CategoryAction.ASSIGN -> assignTiles(category)
      CategoryAction.CLEAR -> clearTiles(category)
      CategoryAction.SWAP -> swapSelectedToCategory(category)
    }

    tiles.replaceAll { it.copy(selected = false) }
    sortGrid()
    publishModelUpdate()
  }

  fun onDragOver(source: Tile, destination: Tile) {
    val sourceCategory = source.category
    swapTiles(
      source.copy(category = destination.category),
      destination.copy(category = sourceCategory),
    )

    tiles.replaceAll { it.copy(selected = false) }
    sortGrid()
    publishModelUpdate()
  }

  fun reset() {
    tiles.replaceAll { tile ->
      tile.copy(
        currentPosition = tile.initialPosition,
        selected = false,
        category = null,
      )
    }

    tiles.sortBy { it.currentPosition }
    publishModelUpdate()
  }

  fun shuffle() {
    val newPositions = tiles
      .filter { it.category == null }
      .map { it.currentPosition }
      .shuffled()
      .toMutableList()

    tiles.replaceAll { tile ->
      if (tile.category == null) {
        tile.copy(currentPosition = newPositions.removeAt(0))
      } else {
        tile
      }
    }

    tiles.sortBy { it.currentPosition }
    publishModelUpdate()
  }

  private fun assignTiles(selectedCategory: Category) {
    tiles
      .filter { it.selected }
      .forEach { tile ->
        tiles[tile.currentPosition] = tile.copy(category = selectedCategory)
      }
  }

  private fun clearTiles(selectedCategory: Category) {
    if (tiles.any { it.selected }) {
      // If any tiles are selected, only clear those tiles.
      tiles.replaceAll { tile ->
        if (tile.selected && tile.category == selectedCategory) tile.copy(category = null) else tile
      }
    } else {
      // Otherwise clear everything in this category.
      tiles.replaceAll { tile ->
        tile.copy(category = if (tile.category == selectedCategory) null else tile.category)
      }
    }
  }

  private fun swapSelectedToCategory(category: Category) {
    val selectedTiles = tiles.filter { it.selected }
    val selectedCategories = selectedTiles.distinctBy { it.category }.map { it.category }

    if (selectedCategories.size == 1) {
      val selectedTilesCategory = selectedCategories.single()
      val originalTilesInCategory = tiles.filter { it.category == category }

      for (tile in selectedTiles) {
        tiles[tile.currentPosition] = tile.copy(category = category)
      }

      for (tile in originalTilesInCategory) {
        tiles[tile.currentPosition] = tile.copy(category = selectedTilesCategory)
      }
    } else {
      // Selected tiles should span across two categories, and there should be an even number
      // between those categories.
      require(selectedCategories.size == 2) {
        "There can be only one or two selected categories when swapping, but had ${selectedCategories.size}"
      }

      val (category1, category2) = selectedCategories
      val category1Tiles = selectedTiles.filter { it.category == category1 }
      val category2Tiles = selectedTiles.filter { it.category == category2 }

      require(category1Tiles.size == category2Tiles.size) {
        "There should be an equal number of tiles in each category when swapping, but had ${category1Tiles.size} and ${category2Tiles.size}"
      }

      category1Tiles.zip(category2Tiles) { tile1, tile2 ->
        swapTiles(tile1.copy(category = tile2.category), tile2.copy(category = tile1.category))
      }
    }
  }

  private fun swapTiles(tile1: Tile, tile2: Tile) {
    if (tile1 == tile2) return

    val tile1Position = tile1.currentPosition
    val tile2Position = tile2.currentPosition
    tiles[tile1Position] = tile2.copy(currentPosition = tile1Position)
    tiles[tile2Position] = tile1.copy(currentPosition = tile2Position)
  }

  /**
   * For each category, ensure that all assigned tiles are positioned in the appropriate
   * reverse-rainbow sorted row, with any missing tiles positioned on the right.
   */
  private fun sortGrid() {
    for (category in Category.entries) {
      val tilesToSort = tiles.filter { it.category == category }.toMutableList()
      val rowStartIndex = when (category) {
        Category.YELLOW -> 12
        Category.GREEN -> 8
        Category.BLUE -> 4
        Category.PURPLE -> 0
      }

      for (position in rowStartIndex..(rowStartIndex + 3)) {
        val tile = tiles[position]

        if (tile.category == category) {
          tilesToSort.remove(tile)
        } else {
          val replacement = tilesToSort.removeFirstOrNull() ?: break
          swapTiles(tile, replacement)
        }
      }
    }
  }

  private fun publishModelUpdate() {
    _model.value = generateModel()
  }

  private fun generateModel(): GameModel {
    val categoryStatuses = Category.entries.associateWith { determineCategoryStatus(it) }
    val completedCategoryCount = categoryStatuses.count { it.value.complete }

    return GameModel(
      tiles = tiles.toImmutableList(),
      categoryStatuses = categoryStatuses.toImmutableMap(),
      allTilesAssigned = tiles.all { it.category != null },
      mostlyComplete = completedCategoryCount >= 3,
    )
  }

  /**
   * Look at the current state of the board - including currently assigned categories and selected
   * tiles. Use this to work out the current status of a given category
   */
  private fun determineCategoryStatus(category: Category): CategoryStatus {
    val selectedTiles = tiles.filter { it.selected }
    val selectionCount = selectedTiles.count()
    val thisCategorySelected = selectedTiles.any { it.category == category }
    val tilesInThisCategoryCount = tiles.count { it.category == category }

    val otherCategoriesSelected = selectedTiles
      .filter { it.category != category }
      .distinctBy { it.category }
      .map { it.category }

    val otherCategorySelectionCount = otherCategoriesSelected.count()

    val allAndOnlyThisCategorySelected = tilesInThisCategoryCount > 0 &&
      otherCategorySelectionCount == 0 &&
      tiles.filter { it.category == category }.all { it.selected }

    val allOfOneOtherCategorySelected = otherCategorySelectionCount == 1 && tiles
      .filter { it.category == otherCategoriesSelected.single() }
      .all { it.selected }

    val equalNumberFromOtherCategorySelected = otherCategorySelectionCount == 1 &&
      selectedTiles.count { it.category == category } == selectedTiles.count { it.category == otherCategoriesSelected.single() }

    val action = when {
      selectionCount > 0 -> when {
        tilesInThisCategoryCount + selectionCount <= 4 && !thisCategorySelected -> CategoryAction.ASSIGN
        allOfOneOtherCategorySelected -> CategoryAction.SWAP
        equalNumberFromOtherCategorySelected -> CategoryAction.SWAP
        selectedTiles.all { it.category == category } -> CategoryAction.CLEAR
        else -> CategoryAction.DISABLED
      }

      tiles.any { it.category == category } -> CategoryAction.CLEAR
      else -> CategoryAction.DISABLED
    }

    return CategoryStatus(
      complete = tilesInThisCategoryCount == 4,
      allSelected = allAndOnlyThisCategorySelected,
      bulkSelectable = tilesInThisCategoryCount > 0,
      action = action,
    )
  }

  private inline fun <T> MutableList<T>.replaceAll(operator: (T) -> T) {
    for (index in indices) {
      set(index, operator(get(index)))
    }
  }
}
