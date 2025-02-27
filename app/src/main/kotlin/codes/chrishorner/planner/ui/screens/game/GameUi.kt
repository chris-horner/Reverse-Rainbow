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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
      BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        actions = {
          Spacer(modifier = Modifier.size(52.dp))

          Spacer(modifier = Modifier.weight(1f))

          RainbowButton(model.rainbowStatus)

          Spacer(modifier = Modifier.weight(1f))

          IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
              .sizeIn(minWidth = 52.dp, minHeight = 52.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.MoreVert,
              contentDescription = stringResource(R.string.menu_description),
            )
          }
        },
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
private fun RainbowButton(status: RainbowStatus) {
  val rainbowRotation by animateFloatAsState(
    targetValue = if (status == RainbowStatus.REVERSIBLE) 180f else 0f
  )

  val text = when (status) {
    RainbowStatus.DISABLED -> ""
    RainbowStatus.SETTABLE -> stringResource(R.string.make_rainbow_button)
    RainbowStatus.REVERSIBLE -> stringResource(R.string.reverse_rainbow_button)
  }

  AnimatedVisibility(
    visible = status != RainbowStatus.DISABLED,
    enter = fadeIn(
      animationSpec = spring(stiffness = Spring.StiffnessMedium)
    ) + slideInVertically(
      animationSpec = spring(
        stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy
      ),
      initialOffsetY = { height -> height },
    ),
    exit = fadeOut(
      animationSpec = spring(stiffness = Spring.StiffnessMedium)
    ) + slideOutVertically(
      animationSpec = spring(
        stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioNoBouncy
      ),
      targetOffsetY = { height -> height },
    ),
  ) {
    OutlinedButton(
      onClick = {},
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