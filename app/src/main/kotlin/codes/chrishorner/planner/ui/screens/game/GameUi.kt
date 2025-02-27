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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
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

@Composable
fun GameUi(game: Game) {
  val model = game.model.value

  Scaffold(
    bottomBar = {
      BottomBar(
        showNytButton = model.mostlyComplete,
        rainbowStatus = model.rainbowStatus,
        onRainbowClick = { game.rainbowSort() },
      )
    },
  ) { paddingValues ->
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(paddingValues)
    ) {
      Spacer(modifier = Modifier.weight(4f))

      Grid(model.cards, game::select)

      Spacer(modifier = Modifier.height(32.dp))

      CategoryActions(
        categoryStatuses = model.categoryStatuses,
        onCategoryClick = { category -> game.select(category) }
      )

      Spacer(modifier = Modifier.weight(1f))
    }
  }
}

@Composable
private fun BottomBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onRainbowClick: () -> Unit,
) {
  BottomAppBar(
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    actions = {
      Spacer(modifier = Modifier.size(16.dp))

      AnimatedVisibility(
        visible = showNytButton,
        enter = ButtonEnterSpec,
        exit = ButtonExitSpec,
      ) {
        OutlinedButton(onClick = {}) {
          Text(stringResource(R.string.open_nyt))
        }
      }

      Spacer(modifier = Modifier.size(16.dp))

      RainbowButton(rainbowStatus, onRainbowClick)

      Spacer(modifier = Modifier.weight(1f))

      Menu()
    },
  )
}

@Composable
private fun Menu() {
  var expanded by remember { mutableStateOf(false) }

  Box {
    IconButton(
      onClick = { expanded = true },
      modifier = Modifier
        .sizeIn(minWidth = 52.dp, minHeight = 52.dp)
    ) {
      Icon(
        imageVector = Icons.Rounded.MoreVert,
        contentDescription = stringResource(R.string.menu_description),
      )
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      DropdownMenuItem(
        leadingIcon = {
          Icon(
            Icons.Rounded.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
          )
        },
        text = { Text(stringResource(R.string.about)) },
        onClick = {},
      )
    }
  }
}

@Composable
private fun RainbowButton(status: RainbowStatus, onClick: () -> Unit) {
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
      modifier = Modifier.animateContentSize(),
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