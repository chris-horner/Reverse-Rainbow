package codes.chrishorner.planner

import assertk.assertFailure
import assertk.assertions.hasMessage
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.Tile
import kotlinx.collections.immutable.toImmutableList
import kotlin.test.Test

class GameTest {
  @Test
  fun `initialize with valid tiles`() {
    Game(validTiles)
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

  private companion object TestData {
    val validTiles = (0..15)
      .map { index ->
        Tile(
          initialPosition = index,
          content = Tile.Content.Text(index.toString()),
        )
      }
      .toImmutableList()
  }
}