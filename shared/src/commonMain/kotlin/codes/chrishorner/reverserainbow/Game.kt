package codes.chrishorner.reverserainbow

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.data.GameModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

/**
 * The current state of the Connections board and logic to manipulate it.
 */
@Stable
class Game(tiles: ImmutableList<Tile>) {
  private val tiles = tiles.toMutableStateList()
  private var expandedCategory by mutableStateOf<Category?>(null)

  val model: State<GameModel> = derivedStateOf { generateModel() }

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
  }

  fun select(tile: Tile) {
    val selectionCount = tiles.count { it.selected }
    val index = currentIndexOf(tile)
    val target = tiles[index]

    // Prevent selecting more than 4 tiles.
    if (!target.selected && selectionCount >= 4) return

    tiles[index] = target.copy(selected = !target.selected)
    expandedCategory = null
  }

  fun longSelect(tile: Tile) {
    val target = tiles[currentIndexOf(tile)]

    if (target.category != null) {
      // Select (or deselect) all tiles in the long-selected tile's category.
      tiles.replaceAll { it.copy(selected = !target.selected && it.category == target.category) }
      expandedCategory = null
    }
  }

  fun clearAll(category: Category) {
    applyCategoryAction(category, CategoryAction.CLEAR)
  }

  fun collapseCategories() {
    expandedCategory = null
  }

  fun applyCategoryAction(
    category: Category,
    action: CategoryAction = determineCategoryAction(category),
  ) {
    when (action) {
      CategoryAction.DISABLED -> return
      CategoryAction.ASSIGN -> assignTiles(category)
      CategoryAction.CLEAR -> clearTiles(category)
      CategoryAction.SWAP_EXPANDED -> swapExpandedToCategory(category)
      CategoryAction.SWAP_SELECTED -> swapSelectedToCategory(category)
      CategoryAction.FINISH -> assignAllUnassigned(category)
      CategoryAction.EXPAND -> { expandedCategory = category }
      CategoryAction.COLLAPSE -> { expandedCategory = null }
    }

    if (action != CategoryAction.EXPAND) {
      expandedCategory = null
    }

    tiles.replaceAll { it.copy(selected = false) }
    sortGrid()
  }

  fun onDragOver(source: Tile, destination: Tile) {
    val sourceCategory = source.category
    swapTiles(
      source.copy(category = destination.category),
      destination.copy(category = sourceCategory),
    )

    tiles.replaceAll { it.copy(selected = false) }
    sortGrid()
  }

  fun reset() {
    tiles.replaceAll { it.copy(selected = false, category = null) }
    tiles.sortBy { it.initialPosition }
  }

  fun shuffle() {
    val unassignedIndices = tiles.indices.filter { index -> tiles[index].category == null }
    val shuffledTiles = unassignedIndices.map { index -> tiles[index] }.shuffled()
    unassignedIndices.forEachIndexed { i, index -> tiles[index] = shuffledTiles[i] }
  }

  private fun assignTiles(selectedCategory: Category) {
    tiles.replaceAll { if (it.selected) it.copy(category = selectedCategory) else it }
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
      val selectedIds = selectedTiles.map { it.id }
      val originalIds = tiles.filter { it.category == category }.map { it.id }

      tiles.replaceAll { tile ->
        when (tile.id) {
          in selectedIds -> tile.copy(category = category)
          in originalIds -> tile.copy(category = selectedTilesCategory)
          else -> tile
        }
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

  private fun swapExpandedToCategory(category: Category) {
    val expandedCategory = checkNotNull(expandedCategory) {
      "Trying to swap $category with expanded category, but there is no expanded category."
    }

    tiles.replaceAll { tile ->
      when (tile.category) {
        expandedCategory -> tile.copy(category = category)
        category -> tile.copy(category = expandedCategory)
        else -> tile
      }
    }
  }

  private fun assignAllUnassigned(category: Category) {
    tiles.replaceAll { if (it.category == null) it.copy(category = category) else it }
  }

  /**
   * Swaps the board positions of [tile1] and [tile2], applying any other field changes carried by
   * the passed copies. Tiles are located by their stable [Tile.id].
   */
  private fun swapTiles(tile1: Tile, tile2: Tile) {
    if (tile1 == tile2) return

    val index1 = currentIndexOf(tile1)
    val index2 = currentIndexOf(tile2)
    tiles[index1] = tile2
    tiles[index2] = tile1
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

  private fun currentIndexOf(tile: Tile): Int {
    val index =  tiles.indexOfFirst { it.id == tile.id }
    require(index >= 0) { "Tile not found in tiles collection." }
    return index
  }

  private fun generateModel(): GameModel {
    val categoryActions = Category.entries.associateWith { determineCategoryAction(it) }

    val completedCategoryCount = Category.entries.count { category ->
      tiles.count { it.category == category } == 4
    }

    return GameModel(
      tiles = tiles.mapIndexed { index, tile -> tile.copy(currentPosition = index) }.toImmutableList(),
      categoryActions = categoryActions.toImmutableMap(),
      allTilesAssigned = tiles.all { it.category != null },
      mostlyComplete = completedCategoryCount >= 3,
      expandedCategory = expandedCategory,
    )
  }

  /**
   * Look at the current state of the board - including currently assigned categories and selected
   * tiles. Use this to work out the current valid action for a category.
   */
  private fun determineCategoryAction(category: Category): CategoryAction {
    val selectedTiles = tiles.filter { it.selected }
    val selectionCount = selectedTiles.count()
    val thisCategorySelected = selectedTiles.any { it.category == category }
    val tilesInThisCategoryCount = tiles.count { it.category == category }

    val otherCategoriesSelected = selectedTiles
      .filter { it.category != category }
      .distinctBy { it.category }
      .map { it.category }

    val otherCategorySelectionCount = otherCategoriesSelected.count()

    val allOfOneOtherCategorySelected = otherCategorySelectionCount == 1 && tiles
      .filter { it.category == otherCategoriesSelected.single() }
      .all { it.selected }

    val equalNumberFromOtherCategorySelected = otherCategorySelectionCount == 1 &&
      selectedTiles.count { it.category == category } == selectedTiles.count { it.category == otherCategoriesSelected.single() }

    val allOtherCategoriesCompletelyAssigned = (Category.entries - category)
      .map { otherCategory -> tiles.count { it.category == otherCategory } }
      .all { tileCount -> tileCount == 4 }

    return when {
      expandedCategory == category -> CategoryAction.COLLAPSE

      expandedCategory != null -> CategoryAction.SWAP_EXPANDED

      selectionCount > 0 -> when {
        tilesInThisCategoryCount + selectionCount <= 4 && !thisCategorySelected -> CategoryAction.ASSIGN
        allOfOneOtherCategorySelected -> CategoryAction.SWAP_SELECTED
        equalNumberFromOtherCategorySelected -> CategoryAction.SWAP_SELECTED
        selectedTiles.all { it.category == category } -> CategoryAction.CLEAR
        else -> CategoryAction.DISABLED
      }

      tilesInThisCategoryCount < 4 && allOtherCategoriesCompletelyAssigned -> CategoryAction.FINISH

      tiles.any { it.category == category } -> CategoryAction.EXPAND

      else -> CategoryAction.DISABLED
    }
  }

  /**
   * Tiles don't actually have a unique identifier, but their initial position we get from the
   * server is totally good enough.
   */
  private val Tile.id: Int
    get() = initialPosition

  private inline fun <T> MutableList<T>.replaceAll(operator: (T) -> T) {
    for (index in indices) {
      set(index, operator(get(index)))
    }
  }
}
