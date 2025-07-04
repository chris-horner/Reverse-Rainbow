package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.data.RainbowStatus
import codes.chrishorner.reverserainbow.ui.screens.game.BottomBarAction.OpenNytClick
import codes.chrishorner.reverserainbow.ui.screens.game.BottomBarAction.RainbowClick

enum class BottomBarAction {
  AboutClick,
  ResetClick,
  ShuffleClick,
  RainbowClick,
  OpenNytClick,
}

/**
 * Shown at the bottom of the screen in all UI configurations except for small screens in landscape.
 */
@Composable
fun BottomBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onAction: (BottomBarAction) -> Unit,
) {
  BottomAppBar(
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    actions = {
      Spacer(modifier = Modifier.size(16.dp))

      OpenNytButton(showNytButton, onClick = { onAction(OpenNytClick) })

      Spacer(modifier = Modifier.size(16.dp))

      RainbowButton(rainbowStatus, onClick = { onAction(RainbowClick) })

      Spacer(modifier = Modifier.weight(1f))

      Menu(onAction)
    },
  )
}

/**
 * Only shown on small screens in landscape. Arranges the bar actions vertically.
 */
@Composable
fun SideBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onAction: (BottomBarAction) -> Unit,
  modifier: Modifier,
) {
  Column(modifier = modifier) {
    Menu(onAction)

    Spacer(modifier = Modifier.size(32.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      OpenNytButton(showNytButton, onClick = { onAction(OpenNytClick) }, Modifier.fillMaxWidth())
    }

    Spacer(modifier = Modifier.size(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      RainbowButton(rainbowStatus, onClick = { onAction(RainbowClick) }, Modifier.fillMaxWidth())
    }
  }
}
