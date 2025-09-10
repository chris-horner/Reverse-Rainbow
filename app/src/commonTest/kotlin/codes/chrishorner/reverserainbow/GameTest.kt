package codes.chrishorner.reverserainbow

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.data.Tile
import kotlinx.collections.immutable.toImmutableList
import kotlin.test.Test

class GameTest {
  @Test
  fun `initialize with valid tiles`() {
    val game = Game(unassignedTiles)
    assertThat(game.tiles).isEqualTo(unassignedTiles)
  }

  @Test
  fun `initialize with not enough tiles`() {
    assertFailure {
      val fifteenTiles = unassignedTiles.take(15).toImmutableList()
      Game(fifteenTiles)
    }.hasMessage("tiles must be size 16, but was 15")
  }

  @Test
  fun `initialize with too many selected tiles`() {
    assertFailure {
      val firstFiveSelected = unassignedTiles
        .mapIndexed { index, tile -> tile.copy(selected = index < 5) }
        .toImmutableList()
      Game(firstFiveSelected)
    }.hasMessage("Number of selected tiles must be within 0 and 4, but was 5")
  }

  @Test
  fun `initialize with invalid category assignment`() {
    assertFailure {
      val tooMuchPurple = unassignedTiles
        .mapIndexed { index, tile -> tile.copy(category = Category.PURPLE.takeIf { index < 5 }) }
        .toImmutableList()
      Game(tooMuchPurple)
    }.hasMessage("Category PURPLE must have between 0 to 4 tiles, but had 5")
  }

  @Test
  fun `selecting a tile updates it to selected`() {
    val game = Game(unassignedTiles)
    game.select(unassignedTiles[8])

    game.tiles.forEachIndexed { index, tile ->
      assertThat(tile.selected).isEqualTo(index == 8)
    }
  }

  @Test
  fun `cannot select more than four tiles`() {
    val game = Game(unassignedTiles)

    repeat(4) { index -> game.select(unassignedTiles[index]) }
    assertThat(game.tiles.count { it.selected }).isEqualTo(4)

    game.select(unassignedTiles[4])
    assertThat(game.tiles.count { it.selected }).isEqualTo(4)
  }

  @Test
  fun `long selecting unselected, categorized tile selects all in category`() {
    val game = Game(assignedTiles)
    game.longSelect(assignedTiles[5])

    game.tiles.filter { it.category == Category.BLUE }.forEach { tile ->
      assertThat(tile.selected).isTrue()
    }

    game.tiles.filter { it.category != Category.BLUE }.forEach { tile ->
      assertThat(tile.selected).isFalse()
    }
  }

  @Test
  fun `long selecting selected, categorized tile deselects all in category`() {
    val allBlueSelectedTiles = assignedTiles
      .mapIndexed { index, tile ->
        tile.copy(selected = index in 8..11)
      }
      .toImmutableList()

    val game = Game(allBlueSelectedTiles)
    game.longSelect(allBlueSelectedTiles[9])

    assertThat(game.tiles.none { it.selected }).isTrue()
  }

  @Test
  fun `selecting all in category replaces selection with only that category`() {
    val oneYellowTileSelected = assignedTiles
      .mapIndexed { index, tile ->
        tile.copy(selected = index == 0)
      }
      .toImmutableList()

    val game = Game(oneYellowTileSelected)
    game.selectAll(Category.GREEN)

    val selectedTiles = game.tiles.filter { it.selected }
    assertThat(selectedTiles).hasSize(4)
    assertThat(selectedTiles.all { it.category == Category.GREEN }).isTrue()
  }

  @Test
  fun `selecting all in already selected category deselects them`() {
    val allGreenSelectedTiles = assignedTiles
      .mapIndexed { index, tile ->
        tile.copy(selected = index in 4..7)
      }
      .toImmutableList()

    val game = Game(allGreenSelectedTiles)
    game.selectAll(Category.BLUE)

    assertThat(game.tiles.none { it.selected }).isTrue()
  }

  @Test
  fun `applying category assigns and sorts selected, uncategorized tiles`() {
    val game = Game(unassignedTiles)

    game.select(unassignedTiles[0])
    game.select(unassignedTiles[5])
    game.select(unassignedTiles[7])
    game.select(unassignedTiles[11])
    game.applyCategoryAction(Category.YELLOW)

    with(game.tiles[12]) {
      assertThat(initialPosition).isEqualTo(0)
      assertThat(currentPosition).isEqualTo(12)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[13]) {
      assertThat(initialPosition).isEqualTo(5)
      assertThat(currentPosition).isEqualTo(13)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[14]) {
      assertThat(initialPosition).isEqualTo(7)
      assertThat(currentPosition).isEqualTo(14)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[15]) {
      assertThat(initialPosition).isEqualTo(11)
      assertThat(currentPosition).isEqualTo(15)
      assertThat(category).isEqualTo(Category.YELLOW)
    }
  }

  @Test
  fun `assigning additional tiles to category appends them to the row`() {
    val game = Game(unassignedTiles)
    game.select(unassignedTiles[0])
    game.select(unassignedTiles[1])
    game.applyCategoryAction(Category.PURPLE)

    assertThat(game.tiles[0].category).isEqualTo(Category.PURPLE)
    assertThat(game.tiles[1].category).isEqualTo(Category.PURPLE)
    assertThat(game.tiles[2].category).isNull()

    game.select(game.tiles[7])
    game.applyCategoryAction(Category.PURPLE)

    with(game.tiles[2]) {
      assertThat(category).isEqualTo(Category.PURPLE)
      assertThat(initialPosition).isEqualTo(7)
    }

    assertThat(game.tiles[0].initialPosition).isEqualTo(0)
    assertThat(game.tiles[1].initialPosition).isEqualTo(1)
  }

  @Test
  fun `assigning tile to a category swaps it with first unassigned tile in category row`() {
    val game = Game(unassignedTiles)
    game.select(unassignedTiles[7])
    game.applyCategoryAction(Category.YELLOW)

    assertThat(game.tiles[12].initialPosition).isEqualTo(7)
    assertThat(game.tiles[7].initialPosition).isEqualTo(12)
  }

  @Test
  fun `assigning a tile to a category that's already in position remains in place`() {
    val game = Game(unassignedTiles)
    game.select(unassignedTiles[12])
    game.applyCategoryAction(Category.YELLOW)

    with(game.tiles[12]) {
      assertThat(category).isEqualTo(Category.YELLOW)
      assertThat(initialPosition).isEqualTo(12)
    }
  }

  @Test
  fun `clearing a category clears categorized tiles`() {
    val game = Game(assignedTiles)
    assertThat(game.model.value.categoryStatuses[Category.GREEN]!!.action)
      .isEqualTo(CategoryAction.CLEAR)

    game.applyCategoryAction(Category.GREEN)

    val rows = game.tiles.chunked(4)

    assertThat(rows[0].all { it.category == Category.PURPLE }).isTrue()
    assertThat(rows[1].all { it.category == Category.BLUE }).isTrue()
    assertThat(rows[2].all { it.category == null }).isTrue()
    assertThat(rows[3].all { it.category == Category.YELLOW }).isTrue()
  }

  @Test
  fun `category swap swaps even number of selected tiles between categories`() {
    val game = Game(assignedTiles)
    game.select(assignedTiles[0])
    game.select(assignedTiles[4])
    assertThat(game.model.value.categoryStatuses[Category.PURPLE]!!.action)
      .isEqualTo(CategoryAction.SWAP)
    assertThat(game.model.value.categoryStatuses[Category.BLUE]!!.action)
      .isEqualTo(CategoryAction.SWAP)

    game.applyCategoryAction(Category.PURPLE)

    with(game.tiles[0]) {
      assertThat(initialPosition).isEqualTo(4)
      assertThat(category).isEqualTo(Category.PURPLE)
    }

    with(game.tiles[4]) {
      assertThat(initialPosition).isEqualTo(0)
      assertThat(category).isEqualTo(Category.BLUE)
    }
  }

  @Test
  fun `category swap swaps completely selected category`() {
    val game = Game(assignedTiles)
    game.selectAll(Category.YELLOW)
    assertThat(game.model.value.categoryStatuses[Category.GREEN]!!.action)
      .isEqualTo(CategoryAction.SWAP)

    game.applyCategoryAction(Category.GREEN)

    assertThat(game.tiles[8].initialPosition).isEqualTo(12)
    assertThat(game.tiles[9].initialPosition).isEqualTo(13)
    assertThat(game.tiles[10].initialPosition).isEqualTo(14)
    assertThat(game.tiles[11].initialPosition).isEqualTo(15)
  }

  @Test
  fun `applying category action with no selected tiles or categories does nothing`() {
    val game = Game(unassignedTiles)
    game.applyCategoryAction(Category.YELLOW)
    assertThat(game.tiles).isEqualTo(unassignedTiles)
  }

  @Test
  fun `dragging one tile over another swaps their positions and categories`() {
    val game = Game(assignedTiles)

    game.onDragOver(
      source = assignedTiles[4],
      destination = assignedTiles[15],
    )

    with(game.tiles[4]) {
      assertThat(initialPosition).isEqualTo(15)
      assertThat(category).isEqualTo(Category.BLUE)
    }

    with(game.tiles[15]) {
      assertThat(initialPosition).isEqualTo(4)
      assertThat(category).isEqualTo(Category.YELLOW)
    }
  }

  @Test
  fun `reset moves tiles to their original positions and clears categories`() {
    val game = Game(unassignedTiles)

    game.select(unassignedTiles[2])
    game.applyCategoryAction(Category.PURPLE)

    game.select(unassignedTiles[6])
    game.applyCategoryAction(Category.BLUE)

    game.select(unassignedTiles[7])
    game.select(unassignedTiles[8])
    game.applyCategoryAction(Category.YELLOW)

    game.reset()
    assertThat(game.tiles).isEqualTo(unassignedTiles)
  }

  @Test
  fun `shuffle keeps categorized tiles in their current positions`() {
    val game = Game(unassignedTiles)

    game.select(unassignedTiles[0])
    game.applyCategoryAction(Category.PURPLE)

    game.select(unassignedTiles[4])
    game.applyCategoryAction(Category.BLUE)

    game.shuffle()

    assertThat(game.tiles[0].category).isEqualTo(Category.PURPLE)
    assertThat(game.tiles[4].category).isEqualTo(Category.BLUE)
  }

  @Test
  fun `game marked as mostly complete when three categories have been fully assigned`() {
    val threeCategoriesAssignedTiles = unassignedTiles
      .mapIndexed { index, tile ->
        tile.copy(
          category = when (index) {
            in 0..3 -> Category.BLUE
            in 4..7 -> Category.YELLOW
            in 8..11 -> Category.PURPLE
            else -> null
          },
        )
      }
      .toImmutableList()

    val game = Game(threeCategoriesAssignedTiles)
    assertThat(game.model.value.mostlyComplete).isTrue()
  }

  @Test
  fun `game not marked as mostly complete when fewer than three categories have been assigned`() {
    val twoCategoriesAssignedTiles = unassignedTiles
      .mapIndexed { index, tile ->
        tile.copy(
          category = when (index) {
            in 0..3 -> Category.BLUE
            in 4..7 -> Category.YELLOW
            in 8..10 -> Category.PURPLE // Only assign 3 out of 4 purple tiles.
            else -> null
          },
        )
      }
      .toImmutableList()

    val game = Game(twoCategoriesAssignedTiles)
    assertThat(game.model.value.mostlyComplete).isFalse()
  }

  @Test
  fun `allTilesAssigned false when all not all tiles are assigned`() {
    val game = Game(unassignedTiles)
    assertThat(game.model.value.allTilesAssigned).isFalse()
  }

  @Test
  fun `allTilesAssigned true when all tiles are assigned`() {
    val game = Game(assignedTiles)
    assertThat(game.model.value.allTilesAssigned).isTrue()
  }

  @Test
  fun `category status marked as complete when category fully assigned`() {
    val game = Game(unassignedTiles)
    assertThat(game.model.value.categoryStatuses[Category.YELLOW]!!.complete).isFalse()

    repeat(4) { index -> game.select(unassignedTiles[index]) }
    game.applyCategoryAction(Category.YELLOW)

    assertThat(game.model.value.categoryStatuses[Category.YELLOW]!!.complete).isTrue()
  }

  @Test
  fun `category action disabled when nothing selected or assigned`() {
    val game = Game(unassignedTiles)
    val categoryStatuses = game.model.value.categoryStatuses

    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action assign when unassigned tiles selected`() {
    val game = Game(unassignedTiles)
    game.select(unassignedTiles[0])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.ASSIGN)
  }

  @Test
  fun `category action clear when assigned tiles selected`() {
    val game = Game(assignedTiles)
    game.select(assignedTiles[12])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action clear for assigned categories when nothing selected`() {
    val game = Game(assignedTiles)

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.CLEAR)
  }

  @Test
  fun `category action swap when even number of tiles across categories selected`() {
    val game = Game(assignedTiles)
    game.select(assignedTiles[0])
    game.select(assignedTiles[1])
    game.select(assignedTiles[4])
    game.select(assignedTiles[5])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.SWAP)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.SWAP)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action disabled when uneven number of tiles across categories selected`() {
    val game = Game(assignedTiles)
    game.select(assignedTiles[0])
    game.select(assignedTiles[1])
    game.select(assignedTiles[4])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action swap when even number of categorized and uncategorized tiles selected`() {
    val firstTwoTilesYellow = unassignedTiles
      .mapIndexed { index, tile ->
        tile.copy(category = if (index < 2) Category.YELLOW else null)
      }
      .toImmutableList()

    val game = Game(firstTwoTilesYellow)
    game.select(firstTwoTilesYellow[0])
    game.select(firstTwoTilesYellow[1])
    game.select(firstTwoTilesYellow[4])
    game.select(firstTwoTilesYellow[5])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.SWAP)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.ASSIGN)
  }

  @Test
  fun `category action disabled when selected count higher than available slots in category`() {
    val firstTwoTilesYellow = unassignedTiles
      .mapIndexed { index, tile ->
        tile.copy(category = if (index < 2) Category.YELLOW else null)
      }
      .toImmutableList()

    val game = Game(firstTwoTilesYellow)
    game.select(firstTwoTilesYellow[4])
    game.select(firstTwoTilesYellow[5])
    game.select(firstTwoTilesYellow[6])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.ASSIGN)
  }

  @Test
  fun `selecting all and only tiles in category marks it as selected`() {
    val game = Game(assignedTiles)
    game.select(assignedTiles[0])
    game.select(assignedTiles[1])
    game.select(assignedTiles[2])
    assertThat(game.model.value.categoryStatuses[Category.PURPLE]!!.allSelected).isFalse()

    game.select(assignedTiles[3])
    assertThat(game.model.value.categoryStatuses[Category.PURPLE]!!.allSelected).isTrue()
  }

  @Test
  fun `selecting all of a category and one other does not mark it as selected`() {
    val tiles = unassignedTiles
      .mapIndexed { index, tile ->
        tile.copy(
          category = when (index) {
            0, 1 -> Category.YELLOW
            4 -> Category.GREEN
            else -> null
          },
          selected = index == 0 || index == 1 || index == 4,
        )
      }
      .toImmutableList()

    val game = Game(tiles)
    // Even though all yellow tiles are selected, a green tile is too.
    assertThat(game.model.value.categoryStatuses[Category.YELLOW]!!.allSelected).isFalse()
  }

  @Test
  fun `category is bulk selectable when there is more than one tile in category`() {
    val tiles = unassignedTiles
      .mapIndexed { index, tile ->
        tile.copy(
          category = when (index) {
            0, 1 -> Category.YELLOW
            4 -> Category.GREEN
            else -> null
          },
        )
      }
      .toImmutableList()

    val game = Game(tiles)
    assertThat(game.model.value.categoryStatuses[Category.YELLOW]!!.bulkSelectable).isTrue()
    assertThat(game.model.value.categoryStatuses[Category.GREEN]!!.bulkSelectable).isFalse()
  }

  private val Game.tiles
    get() = model.value.tiles

  private companion object TestData {
    val unassignedTiles = (0..15)
      .map { index ->
        Tile(
          initialPosition = index,
          content = Tile.Content.Text(index.toString()),
        )
      }
      .toImmutableList()

    val assignedTiles = (0..15)
      .map { index ->
        Tile(
          initialPosition = index,
          content = Tile.Content.Text(index.toString()),
          category = when {
            index < 4 -> Category.PURPLE
            index < 8 -> Category.BLUE
            index < 12 -> Category.GREEN
            else -> Category.YELLOW
          },
        )
      }
      .toImmutableList()
  }
}