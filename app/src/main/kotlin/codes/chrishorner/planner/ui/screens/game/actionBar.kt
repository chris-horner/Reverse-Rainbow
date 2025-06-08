package codes.chrishorner.planner.ui.screens.game

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
import codes.chrishorner.planner.data.RainbowStatus

/**
 * Shown at the bottom of the screen in all UI configurations except for small screens in landscape.
 */
@Composable
fun BottomBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onAboutClick: () -> Unit,
  onResetClick: () -> Unit,
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

      Menu(onAboutClick, onResetClick)
    },
  )
}

@Composable
fun SideBar(
  showNytButton: Boolean,
  rainbowStatus: RainbowStatus,
  onAboutClick: () -> Unit,
  onResetClick: () -> Unit,
  onRainbowClick: () -> Unit,
  onOpenNytClick: () -> Unit,
  modifier: Modifier,
) {
  Column(modifier = modifier) {
    Menu(onAboutClick, onResetClick)

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
