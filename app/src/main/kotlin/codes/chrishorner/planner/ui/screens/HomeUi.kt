package codes.chrishorner.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.data.Card

@Composable
fun HomeUi(
  loaderState: GameLoader.LoaderState,
  onRefresh: () -> Unit,
) {
  LaunchedEffect(Unit) { onRefresh() }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    when (loaderState) {
      GameLoader.LoaderState.Idle -> Loading()
      is GameLoader.LoaderState.Failure -> {}
      is GameLoader.LoaderState.Success -> Loaded(loaderState.game.state.cardRows)
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
private fun Loaded(cardRows: List<List<Card>>) {
  Column {
    Spacer(modifier = Modifier.height(32.dp))
    Grid(cardRows)
    Spacer(modifier = Modifier.height(32.dp))
  }
}

@Composable
private fun Grid(cardRows: List<List<Card>>) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .fillMaxSize()
      .widthIn(max = 400.dp)
      .padding(8.dp)
  ) {
    for (row in cardRows) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        for (card in row) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .background(Color.Gray, RoundedCornerShape(4.dp))
              .weight(1f)
              .aspectRatio(1f)
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