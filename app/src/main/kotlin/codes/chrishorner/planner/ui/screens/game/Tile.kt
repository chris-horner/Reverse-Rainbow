package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.ui.Icons
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.theme.plannerColors
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun Tile(
  tile: Tile,
  dragState: TileDragState,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier,
) = with(LocalSharedTransitionScope.current) {
  val tileColors = getColors(tile)
  val backgroundColor by animateColorAsState(
    targetValue = tileColors.background,
    animationSpec = spring(stiffness = Spring.StiffnessHigh),
  )
  val foregroundColor by animateColorAsState(
    targetValue = tileColors.text,
    animationSpec = spring(stiffness = Spring.StiffnessHigh),
  )

  // When a tile is being dragged, make sure it renders over the others with a grace period,
  // allowing it to continue being on top while it animates back into position.
  val dragZOffset by animateFloatAsState(
    targetValue = if (dragState.dragging) 100f else 0f,
    animationSpec = SnapSpec(delay = 100),
  )

  val scale by animateFloatAsState(
    targetValue = if (dragState.highlight) 0.92f else if (dragState.dragging) 0.7f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow
    ),
  )

  val highlightBorderColor by animateColorAsState(
    targetValue = if (dragState.highlight) MaterialTheme.colorScheme.primary else Color.Transparent,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )

  val dragBorderColor = when {
    dragState.dragging && tile.category != null -> foregroundColor.copy(alpha = 0.3f)
    dragState.dragging -> foregroundColor.copy(alpha = 0.1f)
    else -> Color.Transparent
  }

  val swapBorderColor by animateColorAsState(
    targetValue = if (dragState.dragging) MaterialTheme.colorScheme.secondary else Color.Transparent,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      // Make sure tiles animating to the top, or being dragged render over others.
      .zIndex(4f - tile.currentPosition + dragZOffset)
  ) {
    val proposedSwapTile = dragState.hoveredTile

    if (proposedSwapTile != null) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
      ) {
        // TODO: Deal with image tiles.
        TileText(
          text = (tile.content as Tile.Content.Text).body,
          color = MaterialTheme.colorScheme.onBackground,
          textStyle = MaterialTheme.typography.labelSmall,
        )

        Icon(
          imageVector = Icons.Shuffle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(20.dp),
        )

        TileText(
          text = (proposedSwapTile.content as Tile.Content.Text).body,
          color = MaterialTheme.colorScheme.onBackground,
          textStyle = MaterialTheme.typography.labelSmall,
        )
      }
    }

    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .matchParentSize()
        .padding(1.dp)
        .dashedBorder(color = { swapBorderColor })
        .offset { dragState.offset }
        .animateBounds(
          lookaheadScope = this@with,
          boundsTransform = { _, _ ->
            if (!dragState.dragging) {
              spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = Rect.VisibilityThreshold,
              )
            } else {
              SnapSpec()
            }
          }
        )
        .dashedBorder(color = { highlightBorderColor })
        .padding(3.dp)
        .graphicsLayer {
          this.transformOrigin = transformOrigin
          scaleX = scale
          scaleY = scale
        }
        .background(
          color = backgroundColor,
          shape = TileShape,
        )
        .border(width = 5.dp, color = dragBorderColor, shape = TileShape)
        .clip(TileShape)
        .combinedClickable(
          onClick = onClick,
          onLongClick = onLongClick,
        )
        .padding(8.dp)
    ) {

      when (tile.content) {
        is Tile.Content.Image -> {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(tile.content.url)
              .crossfade(true)
              .build(),
            contentDescription = tile.content.description,
            colorFilter = ColorFilter.tint(foregroundColor),
          )
        }

        is Tile.Content.Text -> {
          TileText(text = tile.content.body, color = foregroundColor)
        }
      }
    }
  }
}

private val TileShape = RoundedCornerShape(6.dp)

private data class TileColors(
  val background: Color,
  val text: Color,
)

@Composable
private fun getColors(tile: Tile): TileColors {
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

  val backgroundColor = if (tile.selected) secondaryColor else primaryColor
  val textColor = if (tile.selected) primaryColor else secondaryColor

  return TileColors(backgroundColor, textColor)
}

private fun Modifier.dashedBorder(
  color: ColorProducer,
) = drawBehind {
  val inset = 3.dp.toPx()
  val outline = TileShape.createOutline(
    size = size.copy(size.width - inset, size.height - inset),
    layoutDirection = layoutDirection,
    density = this
  )
  val dashedStroke = Stroke(
    cap = StrokeCap.Round,
    width = 3.dp.toPx(),
    pathEffect = PathEffect.dashPathEffect(
      intervals = floatArrayOf(4.dp.toPx(), 8.dp.toPx())
    )
  )

  inset(inset / 2f) {
    drawOutline(
      outline = outline,
      style = dashedStroke,
      color = color(),
    )
  }
}