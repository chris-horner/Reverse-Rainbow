package codes.chrishorner.planner

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.CategoryAction
import codes.chrishorner.planner.data.RainbowStatus
import codes.chrishorner.planner.data.Tile
import kotlinx.collections.immutable.toImmutableList
import kotlin.test.Test

class GameTest {
  @Test
  fun `initialize with valid tiles`() {
    val game = Game(validTiles)
    assertThat(game.tiles).isEqualTo(validTiles)
  }

  @Test
  fun `initialize with not enough tiles`() {
    assertFailure {
      val fifteenTiles = validTiles.take(15).toImmutableList()
      Game(fifteenTiles)
    }.hasMessage("tiles must be size 16, but was 15")
  }

  @Test
  fun `initialize with too many selected tiles`() {
    assertFailure {
      val firstFiveSelected = validTiles
        .mapIndexed { index, tile -> tile.copy(selected = index < 5) }
        .toImmutableList()
      Game(firstFiveSelected)
    }.hasMessage("Number of selected tiles must be within 0 and 4, but was 5")
  }

  @Test
  fun `initialize with invalid category assignment`() {
    assertFailure {
      val tooMuchPurple = validTiles
        .mapIndexed { index, tile -> tile.copy(category = Category.PURPLE.takeIf { index < 5 }) }
        .toImmutableList()
      Game(tooMuchPurple)
    }.hasMessage("Category PURPLE must have between 0 to 4 tiles, but had 5")
  }

  @Test
  fun `selecting a tile updates it to selected`() {
    val game = Game(validTiles)
    game.select(validTiles[8])

    game.tiles.forEachIndexed { index, tile ->
      assertThat(tile.selected).isEqualTo(index == 8)
    }
  }

  @Test
  fun `cannot select more than four tiles`() {
    val game = Game(validTiles)

    repeat(4) { index -> game.select(validTiles[index]) }
    assertThat(game.tiles.count { it.selected }).isEqualTo(4)

    game.select(validTiles[4])
    assertThat(game.tiles.count { it.selected }).isEqualTo(4)
  }

  @Test
  fun `long selecting unselected, categorized tile selects all in category`() {
    val game = Game(tilesInRainbowOrder)
    game.longSelect(tilesInRainbowOrder[5])

    game.tiles.filter { it.category == Category.GREEN }.forEach { tile ->
      assertThat(tile.selected).isTrue()
    }

    game.tiles.filter { it.category != Category.GREEN }.forEach { tile ->
      assertThat(tile.selected).isFalse()
    }
  }

  @Test
  fun `long selecting selected, categorized tile deselects all in category`() {
    val allBlueSelectedTiles = tilesInRainbowOrder
      .mapIndexed { index,
        tile ->
        tile.copy(selected = index in 8..11)
      }
      .toImmutableList()

    val game = Game(allBlueSelectedTiles)
    game.longSelect(allBlueSelectedTiles[9])

    assertThat(game.tiles.none { it.selected }).isTrue()
  }

  @Test
  fun `category selection assigns selected, uncategorized tiles`() {
    val game = Game(validTiles)

    game.select(validTiles[0])
    game.select(validTiles[5])
    game.select(validTiles[7])
    game.select(validTiles[11])
    game.select(Category.YELLOW)

    with(game.tiles[0]) {
      assertThat(initialPosition).isEqualTo(0)
      assertThat(currentPosition).isEqualTo(0)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[1]) {
      assertThat(initialPosition).isEqualTo(5)
      assertThat(currentPosition).isEqualTo(1)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[2]) {
      assertThat(initialPosition).isEqualTo(7)
      assertThat(currentPosition).isEqualTo(2)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[3]) {
      assertThat(initialPosition).isEqualTo(11)
      assertThat(currentPosition).isEqualTo(3)
      assertThat(category).isEqualTo(Category.YELLOW)
    }
  }

  @Test
  fun `assigning additional tiles to category appends them to the row`() {
    val game = Game(validTiles)
    game.select(validTiles[0])
    game.select(validTiles[1])
    game.select(Category.YELLOW)

    assertThat(game.tiles[2].category).isNull()
    assertThat(game.tiles[3].category).isNull()

    game.select(validTiles[4])
    game.select(validTiles[5])
    game.select(Category.YELLOW)

    with(game.tiles[2]) {
      assertThat(initialPosition).isEqualTo(4)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[3]) {
      assertThat(initialPosition).isEqualTo(5)
      assertThat(category).isEqualTo(Category.YELLOW)
    }
  }

  @Test
  fun `assigning tile to a category swaps it with first unassigned tile in row`() {
    val game = Game(validTiles)
    game.select(validTiles[0])
    game.select(validTiles[1])
    game.select(Category.YELLOW)

    game.select(validTiles[7])
    game.select(Category.YELLOW)

    assertThat(game.tiles[2].initialPosition).isEqualTo(7)
    assertThat(game.tiles[7].initialPosition).isEqualTo(2)
  }

  @Test
  fun `category selection clears categorized tiles`() {
    val game = Game(tilesInRainbowOrder)
    game.select(Category.GREEN)

    val rows = game.tiles.chunked(4)

    assertThat(rows[0].all { it.category == Category.YELLOW }).isTrue()
    assertThat(rows[1].all { it.category == Category.BLUE }).isTrue()
    assertThat(rows[2].all { it.category == Category.PURPLE }).isTrue()
    assertThat(rows[3].all { it.category == null }).isTrue()
  }

  @Test
  fun `category selection swaps even number of selected tiles between categories`() {
    val game = Game(tilesInRainbowOrder)
    game.select(tilesInRainbowOrder[0])
    game.select(tilesInRainbowOrder[4])

    game.select(Category.GREEN)

    with(game.tiles[0]) {
      assertThat(initialPosition).isEqualTo(4)
      assertThat(category).isEqualTo(Category.YELLOW)
    }

    with(game.tiles[4]) {
      assertThat(initialPosition).isEqualTo(0)
      assertThat(category).isEqualTo(Category.GREEN)
    }
  }

  @Test
  fun `category selection with no selected tiles or categories does nothing`() {
    val game = Game(validTiles)
    game.select(Category.YELLOW)
    assertThat(game.tiles).isEqualTo(validTiles)
  }

  @Test
  fun `dragging one tile over another swaps their positions and categories`() {
    val game = Game(tilesInRainbowOrder)

    game.onDragOver(
      source = tilesInRainbowOrder[4],
      destination = tilesInRainbowOrder[15],
    )

    with(game.tiles[4]) {
      assertThat(initialPosition).isEqualTo(15)
      assertThat(category).isEqualTo(Category.GREEN)
    }

    with(game.tiles[15]) {
      assertThat(initialPosition).isEqualTo(4)
      assertThat(category).isEqualTo(Category.PURPLE)
    }
  }

  @Test
  fun `rainbow sorting incomplete grid fails`() {
    val game = Game(validTiles)
    assertFailure {
      game.rainbowSort()
    }.hasMessage("Can't sort rainbows if not all tiles have a category")
  }

  @Test
  fun `rainbow sorting non-rainbow sorted tiles`() {
    val game = Game(tilesOutOfRainbowOrder)
    game.rainbowSort()

    val (firstRow, secondRow, thirdRow, forthRow) = game.tiles.chunked(4)
    assertThat(firstRow.all { it.category == Category.YELLOW }).isTrue()
    assertThat(secondRow.all { it.category == Category.GREEN }).isTrue()
    assertThat(thirdRow.all { it.category == Category.BLUE }).isTrue()
    assertThat(forthRow.all { it.category == Category.PURPLE }).isTrue()
  }

  @Test
  fun `rainbow sorting already rainbow tiles reverses the rainbow`() {
    val game = Game(tilesInRainbowOrder)
    game.rainbowSort()

    val (firstRow, secondRow, thirdRow, forthRow) = game.tiles.chunked(4)
    assertThat(firstRow.all { it.category == Category.PURPLE }).isTrue()
    assertThat(secondRow.all { it.category == Category.BLUE }).isTrue()
    assertThat(thirdRow.all { it.category == Category.GREEN }).isTrue()
    assertThat(forthRow.all { it.category == Category.YELLOW }).isTrue()
  }

  @Test
  fun `reset moves tiles to their original positions and clears categories`() {
    val game = Game(validTiles)

    game.select(validTiles[2])
    game.select(Category.YELLOW)

    game.select(validTiles[6])
    game.select(Category.GREEN)

    game.select(validTiles[7])
    game.select(validTiles[8])
    game.select(Category.PURPLE)

    game.reset()
    assertThat(game.tiles).isEqualTo(validTiles)
  }

  @Test
  fun `shuffle keeps categorized tiles in their current positions`() {
    val game = Game(validTiles)

    game.select(validTiles[0])
    game.select(Category.YELLOW)

    game.select(validTiles[4])
    game.select(Category.GREEN)

    game.shuffle()

    assertThat(game.tiles[0].category).isEqualTo(Category.YELLOW)
    assertThat(game.tiles[4].category).isEqualTo(Category.GREEN)
  }

  @Test
  fun `game marked as mostly complete when three categories have been fully assigned`() {
    val threeCategoriesAssignedTiles = validTiles
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
    val twoCategoriesAssignedTiles = validTiles
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
  fun `rainbow status disabled when not all categories assigned`() {
    val game = Game(validTiles)
    assertThat(game.model.value.rainbowStatus).isEqualTo(RainbowStatus.DISABLED)
  }

  @Test
  fun `rainbow status settable when all categories assigned out of rainbow order`() {
    val game = Game(tilesOutOfRainbowOrder)
    assertThat(game.model.value.rainbowStatus).isEqualTo(RainbowStatus.SETTABLE)
  }

  @Test
  fun `rainbow status reversable when all categories assigned in rainbow order`() {
    val game = Game(tilesInRainbowOrder)
    assertThat(game.model.value.rainbowStatus).isEqualTo(RainbowStatus.REVERSIBLE)
  }

  @Test
  fun `category status marked as complete when category fully assigned`() {
    val game = Game(validTiles)
    assertThat(game.model.value.categoryStatuses[Category.YELLOW]!!.complete).isFalse()

    repeat(4) { index -> game.select(validTiles[index]) }
    game.select(Category.YELLOW)

    assertThat(game.model.value.categoryStatuses[Category.YELLOW]!!.complete).isTrue()
  }

  @Test
  fun `category action disabled when nothing selected or assigned`() {
    val game = Game(validTiles)
    val categoryStatuses = game.model.value.categoryStatuses

    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action assign when unassigned tiles selected`() {
    val game = Game(validTiles)
    game.select(validTiles[0])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.ASSIGN)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.ASSIGN)
  }

  @Test
  fun `category action clear when assigned tiles selected`() {
    val game = Game(tilesInRainbowOrder)
    game.select(tilesInRainbowOrder[0])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action clear for assigned categories when nothing selected`() {
    val game = Game(tilesInRainbowOrder)

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.CLEAR)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.CLEAR)
  }

  @Test
  fun `category action swap when even number of tiles across categories selected`() {
    val game = Game(tilesInRainbowOrder)
    game.select(tilesInRainbowOrder[0])
    game.select(tilesInRainbowOrder[1])
    game.select(tilesInRainbowOrder[4])
    game.select(tilesInRainbowOrder[5])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.SWAP)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.SWAP)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action disabled when uneven number of tiles across categories selected`() {
    val game = Game(tilesInRainbowOrder)
    game.select(tilesInRainbowOrder[0])
    game.select(tilesInRainbowOrder[1])
    game.select(tilesInRainbowOrder[4])

    val categoryStatuses = game.model.value.categoryStatuses
    assertThat(categoryStatuses[Category.YELLOW]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.GREEN]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.BLUE]!!.action).isEqualTo(CategoryAction.DISABLED)
    assertThat(categoryStatuses[Category.PURPLE]!!.action).isEqualTo(CategoryAction.DISABLED)
  }

  @Test
  fun `category action swap when even number of categorized and uncategorized tiles selected`() {
    val firstTwoTilesYellow = validTiles
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
    val firstTwoTilesYellow = validTiles
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

  private val Game.tiles
    get() = model.value.tiles

  private companion object TestData {
    val validTiles = (0..15)
      .map { index ->
        Tile(
          initialPosition = index,
          content = Tile.Content.Text(index.toString()),
        )
      }
      .toImmutableList()

    val tilesInRainbowOrder = (0..15)
      .map { index ->
        Tile(
          initialPosition = index,
          content = Tile.Content.Text(index.toString()),
          category = when {
            index < 4 -> Category.YELLOW
            index < 8 -> Category.GREEN
            index < 12 -> Category.BLUE
            else -> Category.PURPLE
          },
        )
      }
      .toImmutableList()

    val tilesOutOfRainbowOrder = validTiles
      .mapIndexed { index, tile ->
        tile.copy(
          category = when (index) {
            in 0..3 -> Category.BLUE
            in 4..7 -> Category.YELLOW
            in 8..11 -> Category.PURPLE
            else -> Category.GREEN
          },
        )
      }
      .toImmutableList()
  }
}