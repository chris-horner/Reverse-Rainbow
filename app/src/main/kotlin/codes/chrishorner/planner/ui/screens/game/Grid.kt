package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import codes.chrishorner.planner.data.Tile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun Grid(
  tiles: ImmutableList<Tile>,
  onSelect: (Tile) -> Unit,
  onLongSelect: (Tile) -> Unit,
  onDragOver: (source: Tile, destination: Tile) -> Unit,
) {
  val density = LocalDensity.current
  val alphaAnimation = remember { Animatable(0f) }
  val offsetAnimations = remember(density) {
    with(density) {
      List(tiles.size) { index ->
        Animatable(IntOffset(0, ((-8).dp * (index + 1)).roundToPx()), IntOffset.VectorConverter)
      }.toImmutableList()
    }
  }

  val tileDragStates = rememberTileDragStates(tiles, onDragOver)

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
      .padding(4.dp)
      .alpha(alphaAnimation.value)
      .pointerInput(tiles) { tileDragStates.detectDragGestures(this) }
  ) {
    tiles.fastForEachIndexed { index, tile ->
      key(tile.initialPosition) {
        val dragState = tileDragStates[index]

        Tile(
          tile = tile,
          onClick = { onSelect(tile) },
          onLongClick = { onLongSelect(tile) },
          dragOffsetProvider = { dragState.offset },
          dragging = dragState.dragging,
          transformOrigin = dragState.transformOrigin,
          highlight = dragState.highlight,
          modifier = Modifier
            .offset { offsetAnimations[index].value }
            .onPlaced { coordinates -> dragState.bounds = coordinates.boundsInParent() }
        )
      }
    }
  }
}

@Composable
private fun ConnectionsLayout(
  modifier: Modifier,
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
    val itemSize = widthAndHeight / 4
    val itemConstraints = Constraints.fixed(width = itemSize, height = itemSize)
    val placeables = measurables.fastMap { it.measure(itemConstraints) }

    layout(widthAndHeight, widthAndHeight) {
      placeables.fastForEachIndexed { index, placeable ->
        val horizontalIndex = index % 4
        val horizontalOffset = itemSize * horizontalIndex
        val verticalIndex = index / 4
        val verticalOffset = itemSize * verticalIndex
        placeable.place(x = horizontalOffset, y = verticalOffset)
      }
    }
  }
}