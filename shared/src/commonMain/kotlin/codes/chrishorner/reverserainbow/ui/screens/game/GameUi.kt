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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import codes.chrishorner.reverserainbow.Game
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.data.Tile.Content
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.util.CappedWidthContainer
import codes.chrishorner.reverserainbow.ui.util.PreviewLandscapeSmall
import codes.chrishorner.reverserainbow.ui.util.PreviewLightDarkPortraitSmall
import codes.chrishorner.reverserainbow.ui.util.PreviewUi
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

/**
 * Shows the Connections grid, the category assignment buttons, as well as the action bar. Uses
 * [LocalLayoutOrientation] to conditionally show the game UI in portrait or landscape.
 */
@Composable
fun GameUi(
  game: Game,
  date: LocalDate,
  onOpenNyt: () -> Unit,
  onClickAbout: () -> Unit,
) {
  val orientation = LocalLayoutOrientation.current

  when (orientation) {
    LayoutOrientation.Portrait -> PortraitGameUi(game, date, onOpenNyt, onClickAbout)
    LayoutOrientation.Landscape -> LandscapeGameUi(game, onOpenNyt, onClickAbout)
  }
}

@Composable
private fun PortraitGameUi(
  game: Game,
  date: LocalDate,
  onOpenNyt: () -> Unit,
  onClickAbout: () -> Unit,
) {
  val model = game.model.value

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            date.format(
              LocalDate.Format {
                monthName(MonthNames.ENGLISH_FULL); char(' '); day(Padding.NONE)
              }
            )
          )
        },
        actions = {
          HorizontalAppBarActions(
            showNytButton = model.mostlyComplete,
            onResetClick = { game.reset() },
            onShuffleClick = { game.shuffle() },
            onAboutClick = onClickAbout,
            onOpenNytClick = onOpenNyt,
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
          titleContentColor = MaterialTheme.colorScheme.onBackground,
          actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        )
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

        CategoryActionsBar(
          categoryActions = model.categoryActions,
          expandedCategory = model.expandedCategory,
          boardComplete = model.allTilesAssigned,
          onCategoryClick = { category -> game.applyCategoryAction(category) },
          onCategoryClear = { category -> game.clearAll(category) },
          onCollapseCategories = { game.collapseCategories() },
          modifier = Modifier.zIndex(1f),
        )

        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun LandscapeGameUi(
  game: Game,
  onOpenNyt: () -> Unit,
  onClickAbout: () -> Unit
) {
  val model = game.model.value

  Scaffold { paddingValues ->
    Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

      Spacer(modifier = Modifier.weight(2f))

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

      CategoryActionsBar(
        categoryActions = model.categoryActions,
        expandedCategory = model.expandedCategory,
        boardComplete = model.allTilesAssigned,
        onCategoryClick = { category -> game.applyCategoryAction(category) },
        onCategoryClear = { category -> game.clearAll(category) },
        onCollapseCategories = { game.collapseCategories() },
        modifier = Modifier.zIndex(1f),
      )

      Spacer(modifier = Modifier.weight(1f))

      VerticalToolbar(
        showNytButton = model.mostlyComplete,
        onResetClick = { game.reset() },
        onShuffleClick = { game.shuffle() },
        onAboutClick = onClickAbout,
        onOpenNytClick = onOpenNyt,
        modifier = Modifier.padding(top = 4.dp, end = 4.dp)
      )

      Spacer(modifier = Modifier.size(8.dp))
    }
  }
}

@Composable
@PreviewLightDarkPortraitSmall
internal fun GameUiEmptyPreview() = PreviewUi {
  GameUi(
    game = PreviewData.game,
    date = LocalDate(2025, 6, 14),
    onOpenNyt = {},
    onClickAbout = {},
  )
}

@Composable
@PreviewLandscapeSmall
internal fun GameUiEmptyLandscapePreview() = PreviewUi {
  CompositionLocalProvider(LocalLayoutOrientation provides LayoutOrientation.Landscape) {
    GameUi(
      game = PreviewData.game,
      date = LocalDate(2025, 6, 14),
      onOpenNyt = {},
      onClickAbout = {},
    )
  }
}

private object PreviewData {
  // Tiles from the NYT Connections puzzle dated 2025-06-14, ordered by their
  // original position on the board (same data as VALID_TEXT_JSON in FetchTilesTest).
  val game: Game
    get() = Game(
      tiles = persistentListOf(
        Tile(content = Content.Text("MOM"), initialPosition = 0),
        Tile(content = Content.Text("QUEEN"), initialPosition = 1),
        Tile(content = Content.Text("RIBBON"), initialPosition = 2),
        Tile(content = Content.Text("BORDER"), initialPosition = 3),
        Tile(content = Content.Text("BLUE"), initialPosition = 4),
        Tile(content = Content.Text("BOSTON"), initialPosition = 5),
        Tile(content = Content.Text("HEART"), initialPosition = 6),
        Tile(content = Content.Text("LEGEND"), initialPosition = 7),
        Tile(content = Content.Text("TOTO"), initialPosition = 8),
        Tile(content = Content.Text("ARROW"), initialPosition = 9),
        Tile(content = Content.Text("ICON"), initialPosition = 10),
        Tile(content = Content.Text("HOOCH"), initialPosition = 11),
        Tile(content = Content.Text("RAT"), initialPosition = 12),
        Tile(content = Content.Text("BULL"), initialPosition = 13),
        Tile(content = Content.Text("ASTRO"), initialPosition = 14),
        Tile(content = Content.Text("DIVA"), initialPosition = 15),
      )
    )
}