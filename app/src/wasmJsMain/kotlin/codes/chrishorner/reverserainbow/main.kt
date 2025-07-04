package codes.chrishorner.reverserainbow

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import codes.chrishorner.reverserainbow.ui.screens.MainUi
import codes.chrishorner.reverserainbow.ui.theme.ReverseRainbowTheme
import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.LogLevel
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
  KmLogging.setLogLevel(LogLevel.Verbose)

  ComposeViewport(document.body!!) {
    val scope = rememberCoroutineScope()
    val gameLoader = remember { GameLoader(scope = scope) }
    LaunchedEffect(Unit) { gameLoader.refresh() }

    ReverseRainbowTheme {
      MainUi(
        loaderState = gameLoader.state.value,
        onRefresh = {
          gameLoader.refresh()
        },
        splashIconSize = DpSize(260.dp, 260.dp),
        onOpenNyt = {
          window.location.href = "https://www.nytimes.com/games/connections"
        },
      )
    }
  }
}