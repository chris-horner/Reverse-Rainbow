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
import androidx.compose.ui.zIndex
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
        onAction = { action ->
          when (action) {
            BottomBarAction.AboutClick -> onClickAbout()
            BottomBarAction.ResetClick -> game.reset()
            BottomBarAction.ShuffleClick -> game.shuffle()
            BottomBarAction.RainbowClick -> game.rainbowSort()
            BottomBarAction.OpenNytClick -> onOpenNyt()
          }
        },
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

        Grid(
          tiles = model.tiles,
          onSelect = game::select,
          onLongSelect = game::longSelect,
          onDragOver = game::onDragOver,
          modifier = Modifier.zIndex(2f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        CategoryActions(
          categoryStatuses = model.categoryStatuses,
          rainbowStatus = model.rainbowStatus,
          onCategoryClick = { category -> game.select(category) },
          modifier = Modifier.zIndex(1f),
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
        onAction = { action ->
          when (action) {
            BottomBarAction.AboutClick -> onClickAbout()
            BottomBarAction.ResetClick -> game.reset()
            BottomBarAction.ShuffleClick -> game.shuffle()
            BottomBarAction.RainbowClick -> game.rainbowSort()
            BottomBarAction.OpenNytClick -> onOpenNyt()
          }
        },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      )

      Grid(
        tiles = model.tiles,
        onSelect = game::select,
        onLongSelect = game::longSelect,
        onDragOver = game::onDragOver,
        modifier = Modifier.zIndex(2f),
      )

      Spacer(modifier = Modifier.size(16.dp))

      CategoryActions(
        categoryStatuses = model.categoryStatuses,
        rainbowStatus = model.rainbowStatus,
        onCategoryClick = { category -> game.select(category) },
        modifier = Modifier.zIndex(1f),
      )

      Spacer(modifier = Modifier.size(16.dp))
    }
  }
}