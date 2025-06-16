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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.R
import codes.chrishorner.planner.data.RainbowStatus
import codes.chrishorner.planner.ui.Icons
import codes.chrishorner.planner.ui.util.BetterDropdownMenu

/**
 * 3 dot menu with secondary actions in the app.
 */
@Composable
fun Menu(
  onAction: (BottomBarAction) -> Unit,
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
            Icons.DeleteSweep,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        },
        text = { Text(stringResource(R.string.reset)) },
        onClick = {
          expanded = false
          onAction(BottomBarAction.ResetClick)
        }
      )

      DropdownMenuItem(
        leadingIcon = {
          Icon(
            Icons.Shuffle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        },
        text = { Text(stringResource(R.string.shuffle)) },
        onClick = {
          expanded = false
          onAction(BottomBarAction.ShuffleClick)
        }
      )

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
          onAction(BottomBarAction.AboutClick)
        },
      )
    }
  }
}

@Composable
fun OpenNytButton(
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
fun RainbowButton(
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