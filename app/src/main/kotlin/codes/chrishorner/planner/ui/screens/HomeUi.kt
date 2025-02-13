package codes.chrishorner.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import codes.chrishorner.planner.Game
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.ui.screens.game.CategoryActions
import codes.chrishorner.planner.ui.screens.game.Tile

@Composable
fun HomeUi(
  loaderState: GameLoader.LoaderState,
  onRefresh: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.systemBars)
  ) {
    when (loaderState) {
      GameLoader.LoaderState.Idle -> Loading()
      is GameLoader.LoaderState.Failure -> {}
      is GameLoader.LoaderState.Success -> Loaded(loaderState.game)
    }
  }
}

@Composable
private fun Loading() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize(),
  ) {
    Text("Loading...")
  }
}

@Composable
private fun Loaded(game: Game) {
  val model = game.model.value
  Column {
    Spacer(modifier = Modifier.height(32.dp))
    Grid(model.cards, game::select)
    Spacer(modifier = Modifier.height(32.dp))
    CategoryActions(
      categoryStatuses = model.categoryStatuses,
      onCategoryClick = { category -> game.select(category) }
    )
  }
}

@Composable
private fun Grid(
  cards: List<Card>,
  onSelect: (Card) -> Unit,
) {
  LookaheadScope {
    ConnectionsLayout(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 400.dp)
        .padding(8.dp)
    ) {
      for (card in cards) {
        key(card.initialPosition) {
          Tile(card, onClick = { onSelect(card) })
        }
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

    val width = constraints.maxWidth
    val height = width // Square up.
    val itemSpacingPx = itemSpacing.roundToPx()
    val itemSize = (width / 4)- itemSpacingPx
    val itemConstraints = Constraints.fixed(width = itemSize, height = itemSize)
    val placeables = measurables.fastMap { it.measure(itemConstraints) }

    layout(width, height) {
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