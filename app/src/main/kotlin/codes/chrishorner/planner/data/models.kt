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
    data class Text(val content: String) : Content
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
  val categoryAssignments: Map<Category, Boolean> = mapOf(
    Category.YELLOW to false,
    Category.GREEN to false,
    Category.BLUE to false,
    Category.PURPLE to false,
  ),
) : Parcelable