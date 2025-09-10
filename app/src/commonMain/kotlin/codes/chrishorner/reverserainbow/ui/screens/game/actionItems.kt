package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
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
import codes.chrishorner.reverserainbow.ui.Icons
import codes.chrishorner.reverserainbow.ui.util.BetterDropdownMenu
import org.jetbrains.compose.resources.stringResource
import reverserainbow.app.generated.resources.Res
import reverserainbow.app.generated.resources.about
import reverserainbow.app.generated.resources.menu_description
import reverserainbow.app.generated.resources.open_nyt
import reverserainbow.app.generated.resources.reset
import reverserainbow.app.generated.resources.shuffle

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
        contentDescription = stringResource(Res.string.menu_description),
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
        text = { Text(stringResource(Res.string.reset)) },
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
        text = { Text(stringResource(Res.string.shuffle)) },
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
        text = { Text(stringResource(Res.string.about)) },
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
      Text(stringResource(Res.string.open_nyt))
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