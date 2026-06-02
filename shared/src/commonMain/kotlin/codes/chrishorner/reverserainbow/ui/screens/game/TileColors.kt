package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.ui.theme.plannerColors

class TileColors(
  val background: Color,
  val foreground: Color,
  val dragTileBorder: Color,
  val dragSlotBorder: Color,
  val swapCurrentForeground: Color,
  val swapProposedForeground: Color,
  val hoverSlotBorder: Color,
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

  val hoveredTile = (dragStatus as? DragStatus.Dragged)?.hoveredTile
  val foreground = if (tile.selected && dragStatus !is DragStatus.Dragged) {
    primaryColor
  } else {
    secondaryColor
  }

  return TileColors(
    background = if (tile.selected && dragStatus !is DragStatus.Dragged) {
      secondaryColor
    } else {
      primaryColor
    },
    foreground = foreground,
    dragTileBorder = when {
      dragStatus is DragStatus.Dragged && tile.category != null -> foreground.copy(alpha = 0.3f)
      dragStatus is DragStatus.Dragged -> foreground.copy(alpha = 0.1f)
      else -> Color.Transparent
    },
    dragSlotBorder = if (dragStatus is DragStatus.Dragged) {
      getSlotBorderColorFor(tile.category)
    } else {
      Color.Transparent
    },
    swapCurrentForeground = getSwapTextColorFor(tile.category),
    swapProposedForeground = if (hoveredTile != null) {
      getSwapTextColorFor(hoveredTile.category)
    } else {
      Color.Transparent
    },
    hoverSlotBorder = if (dragStatus == DragStatus.Hovered) {
      getSlotBorderColorFor(tile.category)
    } else {
      Color.Transparent
    },
  )
}

@Composable
private fun getSlotBorderColorFor(category: Category?): Color = when (category) {
  Category.YELLOW -> MaterialTheme.plannerColors.yellowSurface
  Category.GREEN -> MaterialTheme.plannerColors.greenSurface
  Category.BLUE -> MaterialTheme.plannerColors.blueSurface
  Category.PURPLE -> MaterialTheme.plannerColors.purpleSurface
  null -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun getSwapTextColorFor(category: Category?): Color = when (category) {
  Category.YELLOW -> MaterialTheme.plannerColors.yellowSurface
  Category.GREEN -> MaterialTheme.plannerColors.greenSurface
  Category.BLUE -> MaterialTheme.plannerColors.blueSurface
  Category.PURPLE -> MaterialTheme.plannerColors.purpleSurface
  null -> MaterialTheme.colorScheme.onBackground
}