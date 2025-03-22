package codes.chrishorner.planner.data

import android.os.Parcelable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tile(
  val initialPosition: Int,
  val content: Content,
  val currentPosition: Int = initialPosition,
  val selected: Boolean = false,
  val category: Category? = null,
) : Parcelable {
  @Parcelize
  sealed interface Content : Parcelable {
    @Parcelize
    data class Text(val body: String) : Content

    @Parcelize
    data class Image(val url: String, val description: String?) : Content
  }
}

enum class Category {
  YELLOW,
  GREEN,
  BLUE,
  PURPLE,
}

@Parcelize
data class GameState(
  val tiles: ImmutableList<Tile>,
  val selectionCount: Int = 0,
  val categoryStatuses: ImmutableMap<Category, CategoryState> = persistentMapOf(
    Category.YELLOW to CategoryState(),
    Category.GREEN to CategoryState(),
    Category.BLUE to CategoryState(),
    Category.PURPLE to CategoryState(),
  ),
) : Parcelable

@Parcelize
data class CategoryState(
  val assigned: Boolean = false,
  val status: CategoryStatus = CategoryStatus.DISABLED,
) : Parcelable

enum class CategoryStatus {
  DISABLED,
  ENABLED,
  CLEARABLE,
  SWAPPABLE,
}

enum class RainbowStatus {
  DISABLED,
  SETTABLE,
  REVERSIBLE,
}

class GameModel(
  val tiles: ImmutableList<Tile>,
  val categoryStatuses: ImmutableMap<Category, CategoryStatus>,
  val rainbowStatus: RainbowStatus,

  /**
   * If 3/4 or more of the categories have been fully assigned. Useful for showing the NYT app
   * button.
   */
  val mostlyComplete: Boolean,
)