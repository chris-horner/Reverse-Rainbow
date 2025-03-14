package codes.chrishorner.planner.ui.screens.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.Game
import codes.chrishorner.planner.ui.LayoutOrientation
import codes.chrishorner.planner.ui.LocalLayoutOrientation
import codes.chrishorner.planner.ui.LocalUiMode
import codes.chrishorner.planner.ui.UiMode
import codes.chrishorner.planner.ui.util.CappedWidthContainer

@Composable
fun GameUi(
  game: Game,
  onOpenNyt: () -> Unit,
  onClickAbout: () -> Unit,
) {
  val orientation = LocalLayoutOrientation.current

  when (orientation) {
    LayoutOrientation.Portrait -> PortraitGameUi(game, onOpenNyt, onClickAbout)
    LayoutOrientation.Landscape -> LandscapeGameUi(game, onOpenNyt, onClickAbout)
  }
}

@Composable
private fun PortraitGameUi(game: Game, onOpenNyt: () -> Unit, onClickAbout: () -> Unit) {
  val model = game.model.value

  Scaffold(
    bottomBar = {
      BottomBar(
        showNytButton = model.mostlyComplete,
        rainbowStatus = model.rainbowStatus,
        onAboutClick = onClickAbout,
        onRainbowClick = { game.rainbowSort() },
        onOpenNytClick = onOpenNyt,
      )
    },
  ) { paddingValues ->
    CappedWidthContainer {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .padding(paddingValues)
      ) {
        when (LocalUiMode.current) {
          UiMode.Small -> Spacer(modifier = Modifier.weight(1f))
          UiMode.Large -> Spacer(modifier = Modifier.weight(3f))
        }

        Grid(model.cards, game::select, game::longSelect)

        Spacer(modifier = Modifier.height(32.dp))

        CategoryActions(
          categoryStatuses = model.categoryStatuses,
          onCategoryClick = { category -> game.select(category) }
        )

        Spacer(modifier = Modifier.weight(3f))
      }
    }
  }
}

@Composable
private fun LandscapeGameUi(game: Game, onOpenNyt: () -> Unit, onClickAbout: () -> Unit) {
  val model = game.model.value

  Scaffold { paddingValues ->
    Row(modifier = Modifier.padding(paddingValues)) {
      SideBar(
        showNytButton = model.mostlyComplete,
        rainbowStatus = model.rainbowStatus,
        onAboutClick = onClickAbout,
        onRainbowClick = { game.rainbowSort() },
        onOpenNytClick = onOpenNyt,
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      )

      Grid(model.cards, game::select, game::longSelect)

      Spacer(modifier = Modifier.size(16.dp))

      CategoryActions(
        categoryStatuses = model.categoryStatuses,
        onCategoryClick = { category -> game.select(category) }
      )

      Spacer(modifier = Modifier.size(16.dp))
    }
  }
}