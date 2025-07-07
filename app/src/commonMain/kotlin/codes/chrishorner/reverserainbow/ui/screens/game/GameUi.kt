package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import codes.chrishorner.reverserainbow.Game
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.util.CappedWidthContainer

/**
 * Shows the Connections grid, the category assignment buttons, as well as the action bar. Uses
 * [LocalLayoutOrientation] to conditionally show the game UI in portrait or landscape.
 */
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
    CappedWidthContainer(maxWidth = 656.dp) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .padding(paddingValues)
      ) {
        Spacer(modifier = Modifier.weight(1f))

        Grid(
          tiles = model.tiles,
          onSelect = game::select,
          onLongSelect = game::longSelect,
          onDragOver = game::onDragOver,
          modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        CategoryActions(
          categoryStatuses = model.categoryStatuses,
          rainbowStatus = model.rainbowStatus,
          onCategoryClick = { category -> game.applyCategoryAction(category) },
          onCategorySelect = { category -> game.selectAll(category) },
          modifier = Modifier.zIndex(1f),
        )

        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun LandscapeGameUi(game: Game, onOpenNyt: () -> Unit, onClickAbout: () -> Unit) {
  val model = game.model.value

  Scaffold { paddingValues ->
    Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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

      Spacer(modifier = Modifier.size(16.dp))

      Grid(
        tiles = model.tiles,
        onSelect = game::select,
        onLongSelect = game::longSelect,
        onDragOver = game::onDragOver,
        modifier = Modifier
          .fillMaxHeight()
          .wrapContentWidth()
          .zIndex(2f),
      )


      Spacer(modifier = Modifier.size(16.dp))

      CategoryActions(
        categoryStatuses = model.categoryStatuses,
        rainbowStatus = model.rainbowStatus,
        onCategoryClick = { category -> game.applyCategoryAction(category) },
        onCategorySelect = { category -> game.selectAll(category) },
        modifier = Modifier.zIndex(1f),
      )

      Spacer(modifier = Modifier.size(16.dp))

      Spacer(modifier = Modifier.weight(1f))
    }
  }
}