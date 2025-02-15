package codes.chrishorner.planner.ui.screens.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.Game

@Composable
fun GameUi(game: Game) {
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
