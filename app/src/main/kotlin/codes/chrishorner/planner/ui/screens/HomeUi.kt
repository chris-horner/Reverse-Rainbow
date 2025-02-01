package codes.chrishorner.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.Game
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category

@Composable
fun HomeUi(
  loaderState: GameLoader.LoaderState,
  onRefresh: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
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
  val gameState = game.state.value
  Column {
    Spacer(modifier = Modifier.height(32.dp))
    Grid(gameState.cards, game::select)
    Spacer(modifier = Modifier.height(32.dp))
    CategorySubmissions(gameState.selectionCount, gameState.categoryAssignments)
  }
}

@Composable
private fun Grid(cards: Map<Int, Card>, onSelect: (Card) -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .widthIn(max = 400.dp)
      .padding(8.dp)
  ) {
    repeat(4) { row ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        repeat(4) { column ->
          val card = cards.getValue((row * 4) + column)

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .background(if (card.selected) Color.Magenta else Color.Gray, RoundedCornerShape(4.dp))
              .weight(1f)
              .aspectRatio(1f)
              .clickable { onSelect(card) }
          ) {
            Text(
              text = (card.content as Card.Content.Text).content,
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.inverseOnSurface,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CategorySubmissions(
  selectionCount: Int,
  categoryAssignments: Map<Category, Boolean>,
) {
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxWidth()
  ) {
    for ((category, assigned) in categoryAssignments) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .alpha(if (assigned || selectionCount < 4) 0.5f else 1f)
          .background(
            shape = RoundedCornerShape(8.dp),
            color = when (category) {
              Category.YELLOW -> Color.Yellow
              Category.GREEN -> Color.Green
              Category.BLUE -> Color.Blue
              Category.PURPLE -> Color(0xFFAE81FF)
            }
          )
          .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
      )
    }
  }
}