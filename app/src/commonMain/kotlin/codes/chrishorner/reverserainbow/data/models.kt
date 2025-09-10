package codes.chrishorner.reverserainbow.data

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.serialization.Serializable

/**
 * Represents a single card on the Connections board.
 */
@Serializable
data class Tile(
  val initialPosition: Int,
  val content: Content,
  val currentPosition: Int = initialPosition,
  val selected: Boolean = false,
  val category: Category? = null,
) {
  @Serializable
  sealed interface Content {
    @Serializable
    data class Text(val body: String) : Content

    @Serializable
    data class Image(val url: String, val description: String?) : Content
  }
}

enum class Category {
  YELLOW,
  GREEN,
  BLUE,
  PURPLE,
}

/**
 * Based on the current selections and assigned categories on the Connections board,
 * `CategoryAction` represents the current valid action for a category.
 *
 * A couple of examples:
 *
 * If no tiles were currently assigned to YELLOW, and 4 unassigned tiles were selected, then the
 * `CategoryAction` for YELLOW would be `ASSIGN`.
 *
 * If 4 YELLOW tiles were selected and 4 other tiles on the board were assigned to GREEN, then the
 * `CategoryAction` for GREEN would be `SWAP`.
 */
enum class CategoryAction {
  DISABLED,
  ASSIGN,
  CLEAR,
  SWAP,
}

data class CategoryStatus(
  val complete: Boolean,
  val allSelected: Boolean,
  val bulkSelectable: Boolean,
  val action: CategoryAction,
)

data class GameModel(
  val tiles: ImmutableList<Tile>,
  val categoryStatuses: ImmutableMap<Category, CategoryStatus>,
  val allTilesAssigned: Boolean,

  /**
   * If 3/4 or more of the categories have been fully assigned. Useful for showing the NYT app
   * button.
   */
  val mostlyComplete: Boolean,
)