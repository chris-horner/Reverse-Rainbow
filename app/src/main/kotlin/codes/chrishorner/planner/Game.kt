package codes.chrishorner.planner

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.CategoryAction
import codes.chrishorner.planner.data.CategoryStatus
import codes.chrishorner.planner.data.GameModel
import codes.chrishorner.planner.data.RainbowStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

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

  fun select(category: Category) {
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

  fun rainbowSort() {
    require(tiles.all { it.category != null }) {
      "Can't sort rainbows if not all tiles have a category"
    }

    val yellow = tiles.filter { it.category == Category.YELLOW }
    val green = tiles.filter { it.category == Category.GREEN }
    val blue = tiles.filter { it.category == Category.BLUE }
    val purple = tiles.filter { it.category == Category.PURPLE }

    val orderedCategories = if (isCurrentlyInRainbowOrder()) {
      listOf(purple, blue, green, yellow)
    } else {
      listOf(yellow, green, blue, purple)
    }

    orderedCategories.flatten().forEachIndexed { index, tile ->
      tiles[index] = tile.copy(currentPosition = index)
    }

    publishModelUpdate()
  }

  private fun assignTiles(selectedCategory: Category) {
    val selectedTiles = tiles.filter { it.selected }
    val categoryHasAssignedTiles = tiles.any { it.category == selectedCategory }

    val row = if (categoryHasAssignedTiles) {
      tiles.chunked(4).single { row -> row.any { it.category == selectedCategory } }
    } else {
      tiles.chunked(4).first { row -> row.all { it.category == null } }
    }

    var swapPosition = if (categoryHasAssignedTiles) {
      row.first { it.category == null }.currentPosition
    } else {
      row.first().currentPosition
    }

    for (tile in selectedTiles) {
      val currentTile = tiles.single { it.initialPosition == tile.initialPosition }
      val updatedTile = currentTile.copy(category = selectedCategory)
      tiles[updatedTile.currentPosition] = updatedTile
      val tileToSwap = tiles[swapPosition]
      swapTiles(updatedTile, tileToSwap)
      swapPosition++
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
      val tilesInCategory = tiles.filter { it.category == category }

      require(selectedTiles.count() == tilesInCategory.count()) {
        "The number of selected tiles should equal the number of tiles in a category being swapped to, but had ${selectedTiles.count()} selected ad ${tilesInCategory.count()} in category"
      }

      selectedTiles.zip(tilesInCategory) { tile1, tile2 ->
        swapTiles(tile1.copy(category = tile2.category), tile2.copy(category = tile1.category))
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
   * For each row, ensure that tiles with an assigned category are positioned before tiles without
   * a category.
   *
   * For all rows, ensure that those with assigned categories are positioned higher than those
   * without categories.
   */
  private fun sortGrid() {
    for (rowStartIndex in 0..12 step 4) {
      val row = tiles.subList(rowStartIndex, rowStartIndex + 4)

      // If some tiles in a row have a category and some do not, sort them to remove any gaps.
      if (row.any { it.category != null } && row.any { it.category == null }) {
        for ((rowIndex, tile) in row.sortedByDescending { it.category }.withIndex()) {
          val tileIndex = rowStartIndex + rowIndex
          tiles[tileIndex] = tile.copy(currentPosition = tileIndex)
        }
      }

      // Check that there will actually be a next row before attempting further sorting.
      if (rowStartIndex > 8) continue

      // If this entire row has no category, and the next row _does_ have a category, swap all
      // tiles from the next row that do have an assigned category.
      val nextRow = tiles.subList(rowStartIndex + 4, rowStartIndex + 8)
      if (row.all { it.category == null } && nextRow.any { it.category != null }) {
        row.zip(nextRow)
          .filter { (_, tile2) -> tile2.category != null }
          .forEach { (tile1, tile2) ->
            swapTiles(tile1, tile2)
          }
      }
    }
  }

  private fun publishModelUpdate() {
    _model.value = generateModel()
  }

  private fun generateModel(): GameModel {
    val categoryStatuses = Category.entries.associateWith { determineCategoryStatus(it) }
    val rainbowStatus = when {
      isCurrentlyInRainbowOrder() -> RainbowStatus.REVERSIBLE
      tiles.all { it.category != null } -> RainbowStatus.SETTABLE
      else -> RainbowStatus.DISABLED
    }
    val completedCategoryCount = categoryStatuses.count { it.value.complete }

    return GameModel(
      tiles = tiles.toImmutableList(),
      categoryStatuses = categoryStatuses.toImmutableMap(),
      rainbowStatus = rainbowStatus,
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

    val otherCategorySelectionCount = selectedTiles
      .filter { it.category != category }
      .distinctBy { it.category }
      .count()

    val allOfOneOtherCategorySelectedWithMatchingCount = otherCategorySelectionCount == 1 &&
      selectedTiles.all { it.category != null } &&
      selectedTiles.all { it.category != category } &&
      selectedTiles.count() == tilesInThisCategoryCount

    val equalNumberFromOtherCategorySelected = otherCategorySelectionCount == 1 &&
      selectedTiles.count { it.category == category } == selectedTiles.count { it.category != category }

    val action = when {
      selectionCount > 0 -> when {
        tilesInThisCategoryCount + selectionCount <= 4 && !thisCategorySelected -> CategoryAction.ASSIGN
        allOfOneOtherCategorySelectedWithMatchingCount -> CategoryAction.SWAP
        equalNumberFromOtherCategorySelected -> CategoryAction.SWAP
        selectedTiles.all { it.category == category } -> CategoryAction.CLEAR
        else -> CategoryAction.DISABLED
      }

      tiles.any { it.category == category } -> CategoryAction.CLEAR
      else -> CategoryAction.DISABLED
    }

    return CategoryStatus(
      complete = tilesInThisCategoryCount == 4,
      action = action,
    )
  }

  private fun isCurrentlyInRainbowOrder(): Boolean {
    return tiles.slice(0..3).all { it.category == Category.YELLOW } &&
      tiles.slice(4..7).all { it.category == Category.GREEN } &&
      tiles.slice(8..11).all { it.category == Category.BLUE } &&
      tiles.slice(12..15).all { it.category == Category.PURPLE }
  }
}
