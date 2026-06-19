package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.ui.theme.backgroundColor
import codes.chrishorner.reverserainbow.ui.theme.foregroundColor

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
  val primaryColor = tile.category.backgroundColor
  val secondaryColor = tile.category.foregroundColor

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
private fun getSlotBorderColorFor(category: Category?): Color {
  return category?.backgroundColor ?: MaterialTheme.colorScheme.secondary
}

@Composable
private fun getSwapTextColorFor(category: Category?): Color {
  return category?.backgroundColor ?: MaterialTheme.colorScheme.onBackground
}