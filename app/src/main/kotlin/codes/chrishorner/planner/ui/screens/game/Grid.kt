package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import codes.chrishorner.planner.data.Card
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun Grid(
  cards: ImmutableList<Card>,
  onSelect: (Card) -> Unit,
  onLongSelect: (Card) -> Unit,
) {
  val density = LocalDensity.current
  val alphaAnimation = remember { Animatable(0f) }
  val offsetAnimations = remember(density) {
    with(density) {
      List(cards.size) { index ->
        Animatable(IntOffset(0, ((-8).dp * (index + 1)).roundToPx()), IntOffset.VectorConverter)
      }.toImmutableList()
    }
  }

  val tileDragState = rememberTileDragStates(cards)

  LaunchedEffect(Unit) {
    launch { alphaAnimation.animateTo(1f) }
    for (offsetAnimation in offsetAnimations) {
      launch {
        offsetAnimation.animateTo(
          targetValue = IntOffset.Zero,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
          ),
        )
      }
    }
  }

  ConnectionsLayout(
    modifier = Modifier
      .padding(8.dp)
      .alpha(alphaAnimation.value)
      .pointerInput(cards) {
        detectDragGestures(
          onDragStart = { position -> tileDragState.onDragStart(position) },
          onDrag = { _, dragAmount -> tileDragState.onDrag(dragAmount) },
          onDragEnd = { tileDragState.onDragFinish() },
          onDragCancel = { tileDragState.onDragFinish() },
        )
      }
  ) {
    cards.fastForEachIndexed { index, card ->
      key(card.initialPosition) {
        Tile(
          card = card,
          onClick = { onSelect(card) },
          onLongClick = { onLongSelect(card) },
          dragOffsetProvider = { tileDragState[index].offset },
          dragging = tileDragState[index].dragging,
          modifier = Modifier
            .offset { offsetAnimations[index].value }
            .onPlaced { coordinates ->
              tileDragState[index].bounds = coordinates.boundsInParent()
            }
        )
      }
    }
  }
}

@Composable
private fun ConnectionsLayout(
  modifier: Modifier,
  itemSpacing: Dp = 8.dp,
  content: @Composable () -> Unit,
) {
  // We could use `LazyGrid`, but this gives us more control over the animations as items move. Not
  // to mention it's much simpler.
  Layout(
    content = content,
    modifier = modifier,
  ) { measurables, constraints ->
    require(measurables.size == 16) {
      "ConnectionsLayout layout requires 16 children exactly."
    }

    val widthAndHeight = min(constraints.maxWidth, constraints.maxHeight)
    val itemSpacingPx = itemSpacing.roundToPx()
    val itemSize = (widthAndHeight - (itemSpacingPx * 3)) / 4
    val itemConstraints = Constraints.fixed(width = itemSize, height = itemSize)
    val placeables = measurables.fastMap { it.measure(itemConstraints) }

    layout(widthAndHeight, widthAndHeight) {
      placeables.fastForEachIndexed { index, placeable ->
        val horizontalIndex = index % 4
        val horizontalOffset = (itemSize + itemSpacingPx) * horizontalIndex
        val verticalIndex = index / 4
        val verticalOffset = (itemSize + itemSpacingPx) * verticalIndex
        placeable.place(x = horizontalOffset, y = verticalOffset)
      }
    }
  }
}