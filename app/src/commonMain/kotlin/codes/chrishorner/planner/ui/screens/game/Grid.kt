package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalUiMode
import codes.chrishorner.planner.ui.OvershootEasing
import codes.chrishorner.planner.ui.UiMode
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.min

@Composable
fun Grid(
  tiles: ImmutableList<Tile>,
  onSelect: (Tile) -> Unit,
  onLongSelect: (Tile) -> Unit,
  onDragOver: (source: Tile, destination: Tile) -> Unit,
  modifier: Modifier,
) = with(LocalAnimatedContentScope.current) {
  val tileDragStates = rememberTileDragStates(tiles, onDragOver)

  ConnectionsLayout(
    modifier = modifier
      .padding(4.dp)
      .pointerInput(tiles) { tileDragStates.detectDragGestures(this) }
  ) {
    tiles.fastForEachIndexed { index, tile ->
      key(tile.initialPosition) {
        val dragState = tileDragStates[index]

        Tile(
          tile = tile,
          dragState = dragState,
          onClick = { onSelect(tile) },
          onLongClick = { onLongSelect(tile) },
          modifier = Modifier
            .animateEnterExit(
              enter = getEnterTransitionFor(index),
              exit = fadeOut(),
            )
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
  val uiMode = LocalUiMode.current

  when (uiMode) {
    UiMode.Small -> SquareConnectionsLayout(modifier, content)
    UiMode.Large -> WideConnectionsLayout(modifier, content)
  }
}

@Composable
private fun SquareConnectionsLayout(
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
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

@Composable
private fun WideConnectionsLayout(
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  Layout(
    content = content,
    modifier = modifier,
  ) { measurables, constraints ->
    require(measurables.size == 16) {
      "WideConnectionsLayout layout requires 16 children exactly."
    }

    val width = constraints.maxWidth
    val itemWidth = width / 4
    val itemHeight = 96.dp.roundToPx()
    val height = itemHeight * 4

    val itemConstraints = Constraints.fixed(width = itemWidth, height = itemHeight)
    val placeables = measurables.fastMap { it.measure(itemConstraints) }

    layout(width, height) {
      placeables.fastForEachIndexed { index, placeable ->
        val horizontalIndex = index % 4
        val horizontalOffset = itemWidth * horizontalIndex
        val verticalIndex = index / 4
        val verticalOffset = itemHeight * verticalIndex
        placeable.place(x = horizontalOffset, y = verticalOffset)
      }
    }
  }
}

/**
 * Slides and fades an individual tiles when the `Grid` enters the screen.
 */
private fun getEnterTransitionFor(index: Int): EnterTransition {
  val duration = 200
  // Animate tiles lower down in the grid earlier than those higher up to create a nice effect.
  val delay = 200 - ((index / 4) * 50)

  return slideInVertically(
    animationSpec = tween(
      durationMillis = duration,
      delayMillis = delay,
      easing = OvershootEasing(1f),
    ),
  ) + fadeIn(
    animationSpec = tween(
      durationMillis = duration,
      delayMillis = delay,
    )
  )
}