package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import codes.chrishorner.planner.data.Card
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun Grid(
  cards: List<Card>,
  onSelect: (Card) -> Unit,
) {
  val density = LocalDensity.current
  val alphaAnimation = remember { Animatable(0f) }
  val offsetAnimations = remember {
    with(density) {
      List(cards.size) { index ->
        Animatable(IntOffset(0, ((-8).dp * index).roundToPx()), IntOffset.VectorConverter)
      }
    }
  }

  LaunchedEffect(Unit) {
    launch { alphaAnimation.animateTo(1f) }
    for (offsetAnimation in offsetAnimations) {
      launch {
        offsetAnimation.animateTo(
          IntOffset.Zero, animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow
        )
        )
      }
    }
  }

  ConnectionsLayout(
    modifier = Modifier
      .widthIn(max = 400.dp)
      .padding(8.dp)
      .alpha(alphaAnimation.value)
  ) {
    cards.fastForEachIndexed { index, card ->
      key(card.initialPosition) {
        Tile(
          card = card,
          onClick = { onSelect(card) },
          modifier = Modifier.offset { offsetAnimations[index].value }
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