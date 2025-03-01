package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.Game
import codes.chrishorner.planner.R
import codes.chrishorner.planner.data.RainbowStatus
import codes.chrishorner.planner.ui.BetterDropdownMenu
import codes.chrishorner.planner.ui.CappedWidthContainer
import codes.chrishorner.planner.ui.Icons
import codes.chrishorner.planner.ui.LayoutOrientation
import codes.chrishorner.planner.ui.LocalLayoutOrientation
import codes.chrishorner.planner.ui.LocalUiMode
import codes.chrishorner.planner.ui.UiMode

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

        Grid(model.cards, game::select)

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
      LandscapeSideBar(
        showNytButton = model.mostlyComplete,
        rainbowStatus = model.rainbowStatus,
        onAboutClick = onClickAbout,
        onRainbowClick = { game.rainbowSort() },
        onOpenNytClick = onOpenNyt,
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      )

      Grid(model.cards, game::select)

      Spacer(modifier = Modifier.size(16.dp))

      CategoryActions(
        categoryStatuses = model.categoryStatuses,
        onCategoryClick = { category -> game.select(category) }
      )

      Spacer(modifier = Modifier.size(16.dp))
    }
  }
}

@Composable
private fun LandscapeSideBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onAboutClick: () -> Unit,
  onRainbowClick: () -> Unit,
  onOpenNytClick: () -> Unit,
  modifier: Modifier,
) {
  Column(modifier = modifier) {
    Menu(onAboutClick)

    Spacer(modifier = Modifier.size(32.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      OpenNytButton(showNytButton, onOpenNytClick, Modifier.fillMaxWidth())
    }

    Spacer(modifier = Modifier.size(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      RainbowButton(rainbowStatus, onRainbowClick, Modifier.fillMaxWidth())
    }
  }
}

@Composable
private fun BottomBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onAboutClick: () -> Unit,
  onRainbowClick: () -> Unit,
  onOpenNytClick: () -> Unit,
) {
  BottomAppBar(
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    actions = {
      Spacer(modifier = Modifier.size(16.dp))

      OpenNytButton(showNytButton, onOpenNytClick)

      Spacer(modifier = Modifier.size(16.dp))

      RainbowButton(rainbowStatus, onRainbowClick)

      Spacer(modifier = Modifier.weight(1f))

      Menu(onAboutClick)
    },
  )
}

@Composable
private fun Menu(
  onAboutClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier) {
    IconButton(onClick = { expanded = true }) {
      Icon(
        imageVector = Icons.MoreVert,
        contentDescription = stringResource(R.string.menu_description),
      )
    }

    BetterDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      DropdownMenuItem(
        leadingIcon = {
          Icon(
            Icons.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
          )
        },
        text = { Text(stringResource(R.string.about)) },
        onClick = {
          expanded = false
          onAboutClick()
        },
      )
    }
  }
}

@Composable
private fun OpenNytButton(
  show: Boolean, onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = show,
    enter = ButtonEnterSpec,
    exit = ButtonExitSpec,
  ) {
    OutlinedButton(
      onClick = onClick,
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
      ),
      modifier = modifier,
    ) {
      Text(stringResource(R.string.open_nyt))
    }
  }
}

@Composable
private fun RainbowButton(
  status: RainbowStatus,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val rainbowRotation by animateFloatAsState(
    targetValue = if (status == RainbowStatus.REVERSIBLE) 180f else 0f
  )

  val text = if (status == RainbowStatus.REVERSIBLE) {
    stringResource(R.string.reverse_rainbow_button)
  } else {
    stringResource(R.string.make_rainbow_button)
  }

  AnimatedVisibility(
    visible = status != RainbowStatus.DISABLED,
    enter = ButtonEnterSpec,
    exit = ButtonExitSpec,
  ) {
    OutlinedButton(
      onClick = onClick,
      modifier = modifier.animateContentSize(),
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
      ),
    ) {
      Text("🌈", modifier = Modifier.rotate(rainbowRotation))
      Spacer(modifier = Modifier.size(6.dp))
      AnimatedContent(
        targetState = text,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
      ) { text ->
        Text(text)
      }
    }
  }
}

private val ButtonEnterSpec = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
  slideInVertically(
    animationSpec = spring(
      stiffness = Spring.StiffnessMedium,
      dampingRatio = Spring.DampingRatioMediumBouncy,
    ),
    initialOffsetY = { height -> height },
  )

private val ButtonExitSpec = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
  slideOutVertically(
    animationSpec = spring(
      stiffness = Spring.StiffnessMedium,
      dampingRatio = Spring.DampingRatioNoBouncy,
    ),
    targetOffsetY = { height -> height },
  )