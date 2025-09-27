package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AppBarColumn
import androidx.compose.material3.AppBarMenuState
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.AppBarScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.ui.Icons
import org.jetbrains.compose.resources.stringResource
import reverserainbow.app.generated.resources.Res
import reverserainbow.app.generated.resources.about
import reverserainbow.app.generated.resources.menu
import reverserainbow.app.generated.resources.open_nyt
import reverserainbow.app.generated.resources.reset
import reverserainbow.app.generated.resources.shuffle

/**
 * Game actions to be shown in an `AppBar` when in a portrait layout.
 */
@Composable
fun HorizontalAppBarActions(
  showNytButton: Boolean,
  onResetClick: () -> Unit,
  onShuffleClick: () -> Unit,
  onAboutClick: () -> Unit,
  onOpenNytClick: () -> Unit,
) {
  AppBarRow(
    maxItemCount = 2,
    overflowIndicator = { state -> MenuButton(state) },
    content = {
      customItem(
        appbarContent = {
          AnimatedContent(
            targetState = showNytButton,
            transitionSpec = { ButtonEnterSpec togetherWith ButtonExitSpec },
          ) { targetState ->
            if (targetState) {
              TextButton(
                onClick = onOpenNytClick,
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = MaterialTheme.colorScheme.primary,
                ),
              ) {
                Icon(
                  imageVector = Icons.OpenInNew,
                  contentDescription = null,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(Res.string.open_nyt))
              }
            } else {
              Spacer(modifier = Modifier.size(48.dp))
            }
          }
        },

        menuContent = { state ->
          if (!showNytButton) return@customItem

          DropdownMenuItem(
            text = { Text(stringResource(Res.string.open_nyt)) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Construction,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
              )
            },
            onClick = {
              state.dismiss()
              onOpenNytClick()
            },
          )
        },
      )

      actions(onResetClick, onShuffleClick, onAboutClick)
    },
  )
}

/**
 * Game actions shown in a [VerticalFloatingToolbar] for use in a landscape layout.
 */
@Composable
fun VerticalToolbar(
  showNytButton: Boolean,
  onResetClick: () -> Unit,
  onShuffleClick: () -> Unit,
  onAboutClick: () -> Unit,
  onOpenNytClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  VerticalFloatingToolbar(
    expanded = showNytButton,
    trailingContent = {
      TooltipBox(
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(stringResource(Res.string.open_nyt)) } },
        state = rememberTooltipState(),
      ) {
        IconButton(
          onClick = onOpenNytClick,
        ) {
          Icon(
            imageVector = Icons.OpenInNew,
            contentDescription = stringResource(Res.string.open_nyt),
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }
    },
    modifier = modifier,
  ) {
    AppBarColumn(
      maxItemCount = 1,
      overflowIndicator = { state -> MenuButton(state) },
      content = {
        actions(onResetClick, onShuffleClick, onAboutClick)
      },
    )
  }
}

@Composable
private fun MenuButton(state: AppBarMenuState) {
  TooltipBox(
    positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
    tooltip = { PlainTooltip { Text(stringResource(Res.string.menu)) } },
    state = rememberTooltipState(),
  ) {
    FilledTonalIconButton(
      onClick = { state.show() },
      colors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
      ),
    ) {
      Icon(
        imageVector = Icons.MoreVert,
        contentDescription = stringResource(Res.string.menu),
      )
    }
  }
}

private fun AppBarScope.actions(
  onResetClick: () -> Unit,
  onShuffleClick: () -> Unit,
  onAboutClick: () -> Unit,
) {
  customItem(
    appbarContent = {
      TooltipBox(
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(stringResource(Res.string.reset)) } },
        state = rememberTooltipState(),
      ) {
        IconButton(onClick = onResetClick) {
          Icon(
            imageVector = Icons.Restart,
            contentDescription = stringResource(Res.string.reset),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    },

    menuContent = { state ->
      DropdownMenuItem(
        text = { Text(stringResource(Res.string.reset)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Restart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        },
        onClick = {
          state.dismiss()
          onResetClick()
        },
      )
    },
  )

  customItem(
    appbarContent = {
      TooltipBox(
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(stringResource(Res.string.shuffle)) } },
        state = rememberTooltipState(),
      ) {
        IconButton(onClick = onShuffleClick) {
          Icon(
            imageVector = Icons.Shuffle,
            contentDescription = stringResource(Res.string.shuffle),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    },

    menuContent = { state ->
      DropdownMenuItem(
        text = { Text(stringResource(Res.string.shuffle)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Shuffle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        },
        onClick = {
          state.dismiss()
          onShuffleClick()
        },
      )
    },
  )

  customItem(
    appbarContent = {
      TooltipBox(
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(stringResource(Res.string.about)) } },
        state = rememberTooltipState(),
      ) {
        IconButton(onClick = onAboutClick) {
          Icon(
            imageVector = Icons.Info,
            contentDescription = stringResource(Res.string.about),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    },

    menuContent = { state ->
      DropdownMenuItem(
        text = { Text(stringResource(Res.string.about)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
          )
        },
        onClick = {
          state.dismiss()
          onAboutClick()
        },
      )
    },
  )
}

private val ButtonEnterSpec = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
  slideInHorizontally(
    animationSpec = spring(
      stiffness = Spring.StiffnessMediumLow,
      dampingRatio = Spring.DampingRatioMediumBouncy,
    ),
    initialOffsetX = { fullWidth -> fullWidth / 2 },
  )

private val ButtonExitSpec = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
  slideOutHorizontally(
    animationSpec = spring(
      stiffness = Spring.StiffnessMedium,
      dampingRatio = Spring.DampingRatioNoBouncy,
    ),
    targetOffsetX = { fullWidth -> fullWidth / 2 },
  )