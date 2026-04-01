package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.zIndex
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.TileShape
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

  // Track whether this tile was showing a preview and its position just changed (committed swap).
  // When true, the tile is already at its destination — snap instead of animating.
  val previewOffsetValue = IntOffset(dragState.previewOffset.longValue)
  val prevPosition = remember { mutableIntStateOf(tile.currentPosition) }
  val prevHadPreview = remember { mutableStateOf(false) }
  val positionChanged = tile.currentPosition != prevPosition.intValue
  val shouldSnap = positionChanged && prevHadPreview.value

  SideEffect {
    prevPosition.intValue = tile.currentPosition
    prevHadPreview.value = previewOffsetValue != IntOffset.Zero
  }

  val animatedPreviewOffset by animateIntOffsetAsState(
    targetValue = previewOffsetValue,
    animationSpec = when {
      shouldSnap -> SnapSpec()
      dragState.status is DragStatus.Companion -> SnapSpec()
      else -> spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow,
      )
    },
  )

  // When a tile is being dragged or is a companion, make sure it renders over the others with a
  // grace period, allowing it to continue being on top while it animates back into position.
  val dragZOffset by animateFloatAsState(
    targetValue = when (dragState.status) {
      is DragStatus.Dragged -> 100f
      is DragStatus.Companion -> 50f
      else -> 0f
    },
    animationSpec = SnapSpec(delay = 100),
  )

  val scale by animateFloatAsState(
    targetValue = when (dragState.status) {
      is DragStatus.Dragged -> 0.7f
      is DragStatus.Hovered -> 0.92f
      is DragStatus.Companion -> 0.92f
      is DragStatus.RowHovered -> 0.92f
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
            when {
              dragState.status is DragStatus.Dragged -> SnapSpec()
              shouldSnap -> SnapSpec()
              else -> spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = Rect.VisibilityThreshold,
              )
            }
          }
        )
        .offset { animatedPreviewOffset }
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