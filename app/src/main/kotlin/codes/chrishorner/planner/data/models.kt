package codes.chrishorner.planner.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
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
  val cards: List<Card>,
  val selectionCount: Int = 0,
  val categoryStatuses: Map<Category, CategoryState> = mapOf(
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
  val cards: List<Card>,
  val categoryStatuses: Map<Category, CategoryStatus>,
  val rainbowStatus: RainbowStatus,

  /**
   * If 3/4 or more of the categories have been fully assigned. Useful for showing the NYT app
   * button.
   */
  val mostlyComplete: Boolean,
)