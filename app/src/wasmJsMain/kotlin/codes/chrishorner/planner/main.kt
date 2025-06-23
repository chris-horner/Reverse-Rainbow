package codes.chrishorner.planner

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import codes.chrishorner.planner.ui.screens.MainUi
import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.LogLevel
import com.diamondedge.logging.logging
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
  KmLogging.setLogLevel(LogLevel.Verbose)

  ComposeViewport(document.body!!) {
    val scope = rememberCoroutineScope()
    val gameLoader = remember { GameLoader(scope = scope) }
    LaunchedEffect(Unit) { gameLoader.refresh() }

    MainUi(
      loaderState = gameLoader.state.value,
      onRefresh = {
        logging("Planner").d { "Here?" }
        gameLoader.refresh()
      },
      splashIconSize = DpSize(260.dp, 260.dp),
      onOpenNyt = {
        window.location.href = "https://www.nytimes.com/games/connections"
      },
    )
  }
}