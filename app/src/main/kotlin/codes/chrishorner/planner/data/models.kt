package codes.chrishorner.planner.data

data class Card(
  val content: Content,
  val selected: Boolean = false,
  val category: Category? = null,
) {
  sealed interface Content {
    data class Text(val content: String) : Content
    data class Image(val url: String, val description: String?) : Content
  }
}

enum class Category {
  YELLOW,
  GREEN,
  BLUE,
  PURPLE,
}

data class GameState(
  val cardRows: List<List<Card>>,
  val selectionCount: Int = 0,
  val categoryAssignments: Map<Category, Boolean> = mapOf(
    Category.YELLOW to false,
    Category.GREEN to false,
    Category.BLUE to false,
    Category.PURPLE to false,
  ),
)
