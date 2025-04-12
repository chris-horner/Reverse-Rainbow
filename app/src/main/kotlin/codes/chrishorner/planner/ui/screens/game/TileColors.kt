package codes.chrishorner.planner.ui.screens.game

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.ui.theme.plannerColors

class TileColors(
  val background: Color,
  val foreground: Color,
  val dragBorder: Color,
  val swapBorder: Color,
  val swapForeground: Color,
  val hoverBorder: Color,
)

@Composable
fun getColorsFor(tile: Tile, dragState: TileDragState): TileColors {
  val dragStatus = dragState.status
  val primaryColor: Color
  val secondaryColor: Color

  when (tile.category) {
    Category.YELLOW -> {
      primaryColor = MaterialTheme.plannerColors.yellowSurface
      secondaryColor = MaterialTheme.plannerColors.onYellowSurface
    }

    Category.GREEN -> {
      primaryColor = MaterialTheme.plannerColors.greenSurface
      secondaryColor = MaterialTheme.plannerColors.onGreenSurface
    }

    Category.BLUE -> {
      primaryColor = MaterialTheme.plannerColors.blueSurface
      secondaryColor = MaterialTheme.plannerColors.onBlueSurface
    }

    Category.PURPLE -> {
      primaryColor = MaterialTheme.plannerColors.purpleSurface
      secondaryColor = MaterialTheme.plannerColors.onPurpleSurface
    }

    null -> {
      primaryColor = MaterialTheme.colorScheme.surfaceContainer
      secondaryColor = MaterialTheme.colorScheme.onSurface
    }
  }

  val background = if (tile.selected) secondaryColor else primaryColor
  val foreground = if (tile.selected) primaryColor else secondaryColor

  val dragBorder = when {
    dragStatus is DragStatus.Dragged && tile.category != null -> foreground.copy(alpha = 0.3f)
    dragStatus is DragStatus.Dragged -> foreground.copy(alpha = 0.1f)
    else -> Color.Transparent
  }

  val swapBorder = if (dragStatus is DragStatus.Dragged) {
    getHoverBorderColorFor(tile.category)
  } else {
    Color.Transparent
  }

  val swapForeground = if (dragStatus is DragStatus.Dragged) {
    if (tile.category != null) getHoverBorderColorFor(tile.category) else foreground
  } else {
    Color.Transparent
  }

  val hoverBorder = if (dragStatus == DragStatus.Hovered) {
    getHoverBorderColorFor(tile.category)
  } else {
    Color.Transparent
  }

  return TileColors(background, foreground, dragBorder, swapBorder, swapForeground, hoverBorder)
}

@Composable
private fun getHoverBorderColorFor(category: Category?): Color = when(category) {
  Category.YELLOW -> MaterialTheme.plannerColors.yellowSurface
  Category.GREEN -> MaterialTheme.plannerColors.greenSurface
  Category.BLUE -> MaterialTheme.plannerColors.blueSurface
  Category.PURPLE -> MaterialTheme.plannerColors.purpleSurface
  null -> MaterialTheme.colorScheme.secondary
}