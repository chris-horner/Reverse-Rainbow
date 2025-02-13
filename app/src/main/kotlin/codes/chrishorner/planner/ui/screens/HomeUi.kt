package codes.chrishorner.planner.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.zIndex
import codes.chrishorner.planner.Game
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.CategoryStatus
import codes.chrishorner.planner.ui.Shuffle
import codes.chrishorner.planner.ui.theme.plannerColors

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
    CategorySubmissions(
      categoryStatuses = model.categoryStatuses,
      onCategoryClick = { category ->
        game.select(category)
      }
    )
  }
}

@OptIn(ExperimentalSharedTransitionApi::class)
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
      for ((index, card) in cards.withIndex()) {
        key(card.initialPosition) {

          val cardBackgroundColor: Color
          val textColor: Color
          val selectionColor: Color

          when (card.category) {
            Category.YELLOW -> {
              cardBackgroundColor = MaterialTheme.plannerColors.yellowSurface
              textColor = MaterialTheme.plannerColors.onYellowSurface
              selectionColor = MaterialTheme.plannerColors.onYellowSurface
            }
            Category.GREEN -> {
              cardBackgroundColor = MaterialTheme.plannerColors.greenSurface
              textColor = MaterialTheme.plannerColors.onGreenSurface
              selectionColor = MaterialTheme.plannerColors.onGreenSurface
            }
            Category.BLUE -> {
              cardBackgroundColor = MaterialTheme.plannerColors.blueSurface
              textColor = MaterialTheme.plannerColors.onBlueSurface
              selectionColor = MaterialTheme.plannerColors.onBlueSurface
            }
            Category.PURPLE -> {
              cardBackgroundColor = MaterialTheme.plannerColors.purpleSurface
              textColor = MaterialTheme.plannerColors.onPurpleSurface
              selectionColor = MaterialTheme.plannerColors.onPurpleSurface
            }
            null -> {
              cardBackgroundColor = MaterialTheme.colorScheme.surfaceContainer
              textColor = MaterialTheme.colorScheme.onSurface
              selectionColor = MaterialTheme.colorScheme.primary
            }
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .animateBounds(this@LookaheadScope)
              .background(
                color = cardBackgroundColor,
                shape = RoundedCornerShape(6.dp)
              )
              .then(
                if (card.selected) Modifier.border(
                  4.dp, color = selectionColor, shape = RoundedCornerShape(6.dp)
                ) else Modifier
              )
              .clickable { onSelect(card) }
              .padding(8.dp)
              .zIndex(4f - index) // Makes sure cards animating to the top render over others.
          ) {
            BasicText(
              text = (card.content as Card.Content.Text).content,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = { textColor },
              maxLines = 1,
              overflow = TextOverflow.Visible,
              autoSize = TextAutoSize.StepBased(
                maxFontSize = MaterialTheme.typography.titleMedium.fontSize,
                minFontSize = 1.sp,
                stepSize = 1.sp
              )
            )
          }
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

@Composable
private fun CategorySubmissions(
  categoryStatuses: Map<Category, CategoryStatus>,
  onCategoryClick: (Category) -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxWidth(),
  ) {
    for ((category, status) in categoryStatuses) {

      val backgroundColor: Color
      val iconColor: Color

      when (category) {
        Category.YELLOW -> {
          backgroundColor = MaterialTheme.plannerColors.yellowSurface
          iconColor = MaterialTheme.plannerColors.onYellowSurface
        }
        Category.GREEN -> {
          backgroundColor = MaterialTheme.plannerColors.greenSurface
          iconColor = MaterialTheme.plannerColors.onGreenSurface
        }
        Category.BLUE -> {
          backgroundColor = MaterialTheme.plannerColors.blueSurface
          iconColor = MaterialTheme.plannerColors.onBlueSurface
        }
        Category.PURPLE -> {
          backgroundColor = MaterialTheme.plannerColors.purpleSurface
          iconColor = MaterialTheme.plannerColors.onPurpleSurface
        }
      }

      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(64.dp)
          .alpha(if (status == CategoryStatus.DISABLED) 0.5f else 1f)
          .background(
            shape = RoundedCornerShape(8.dp),
            color = backgroundColor,
          )
          .border(width = 2.dp, color = backgroundColor.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
          .clickable(
            enabled = status != CategoryStatus.DISABLED,
            onClick = { onCategoryClick(category) },
          )
      ) {

        if (status == CategoryStatus.CLEARABLE) {
          Icon(
            imageVector = Icons.Rounded.Clear,
            contentDescription = null,
            tint = iconColor,
          )
        } else if (status == CategoryStatus.SWAPPABLE) {
          Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = null,
            tint = iconColor,
          )
        }
      }
    }
  }
}