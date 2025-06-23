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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.zIndex
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.ui.Icons
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.theme.TileShape
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
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
  val tileColors = getColorsFor(tile, dragState)

  val backgroundColor by animateColorAsState(
    targetValue = tileColors.background,
    animationSpec = spring(stiffness = Spring.StiffnessHigh),
  )
  val foregroundColor by animateColorAsState(
    targetValue = tileColors.foreground,
    animationSpec = spring(stiffness = Spring.StiffnessHigh),
  )
  val dragBorderColor by animateColorAsState(
    targetValue = tileColors.dragTileBorder,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )
  val hoverSlotBorderColor by animateColorAsState(
    targetValue = tileColors.hoverSlotBorder,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )
  val dragSlotBorderColor by animateColorAsState(
    targetValue = tileColors.dragSlotBorder,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )
  val swapCurrentForegroundColor by animateColorAsState(
    targetValue = tileColors.swapCurrentForeground,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )
  val swapProposedForegroundColor by animateColorAsState(
    targetValue = tileColors.swapProposedForeground,
    animationSpec = spring(stiffness = Spring.StiffnessHigh)
  )

  // When a tile is being dragged, make sure it renders over the others with a grace period,
  // allowing it to continue being on top while it animates back into position.
  val dragZOffset by animateFloatAsState(
    targetValue = if (dragState.status is DragStatus.Dragged) 100f else 0f,
    animationSpec = SnapSpec(delay = 100),
  )

  val scale by animateFloatAsState(
    targetValue = when (dragState.status) {
      is DragStatus.Dragged -> 0.7f
      is DragStatus.Hovered -> 0.92f
      DragStatus.None -> 1f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow
    ),
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      // Make sure tiles animating to the top, or being dragged render over others.
      .zIndex(4f - tile.currentPosition + dragZOffset)
  ) {
    val proposedSwapTile = (dragState.status as? DragStatus.Dragged)?.hoveredTile

    if (proposedSwapTile != null && proposedSwapTile.currentPosition != tile.currentPosition) {
      SwapContent(
        current = tile.content,
        proposed = proposedSwapTile.content,
        currentColor = swapCurrentForegroundColor,
        proposedColor = swapProposedForegroundColor,
      )
    }

    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .matchParentSize()
        .padding(1.dp)
        .dashedBorder(color = { dragSlotBorderColor })
        .offset { dragState.status.offset }
        .animateBounds(
          lookaheadScope = this@with,
          boundsTransform = { _, _ ->
            if (dragState.status !is DragStatus.Dragged) {
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
        .dashedBorder(color = { hoverSlotBorderColor })
        .padding(3.dp)
        .graphicsLayer {
          transformOrigin = dragState.status.transformOrigin
          scaleX = scale
          scaleY = scale
        }
        .background(
          color = backgroundColor,
          shape = TileShape,
        )
        .border(width = 6.dp, color = dragBorderColor, shape = TileShape)
        .clip(TileShape)
        .combinedClickable(
          onClick = onClick,
          onLongClick = onLongClick,
        )
        .padding(4.dp)
    ) {
      TileContent(tile.content, color = foregroundColor)
    }
  }
}

@Composable
private fun TileContent(
  content: Tile.Content,
  color: Color,
) {
  when (content) {
    is Tile.Content.Image -> TileImage(content, color)
    is Tile.Content.Text -> TileText(text = content.body, color = color)
  }
}

/**
 * Shown "under" the original position of a tile as it's being dragged. Displays a preview of the
 * swap that will occur if the dragged tile is dropped.
 */
@Composable
private fun SwapContent(
  current: Tile.Content,
  proposed: Tile.Content,
  currentColor: Color,
  proposedColor: Color,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier.padding(12.dp),
  ) {
    SwapEntry(current, currentColor)
    Icon(Icons.Shuffle, contentDescription = null, modifier = Modifier.size(16.dp))
    SwapEntry(proposed, proposedColor)
  }
}

@Composable
private fun SwapEntry(content: Tile.Content, color: Color) {
  when (content) {
    is Tile.Content.Image -> {
      TileImage(content, color, size = 24.dp)
    }

    is Tile.Content.Text -> {
      Text(
        text = content.body,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = color,
      )
    }
  }
}

@Composable
private fun TileImage(
  content: Tile.Content.Image,
  color: Color,
  size: Dp = Dp.Unspecified,
) {
  AsyncImage(
    model = ImageRequest.Builder(LocalPlatformContext.current)
      .data(content.url)
      .crossfade(true)
      .build(),
    contentDescription = content.description,
    colorFilter = ColorFilter.tint(color),
    modifier = if (size.isSpecified) Modifier.size(size) else Modifier,
  )
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