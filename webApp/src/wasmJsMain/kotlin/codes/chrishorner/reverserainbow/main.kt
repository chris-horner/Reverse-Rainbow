package codes.chrishorner.reverserainbow

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import codes.chrishorner.reverserainbow.data.LocalPersistence
import codes.chrishorner.reverserainbow.data.WebPersistence
import codes.chrishorner.reverserainbow.ui.screens.MainUi
import codes.chrishorner.reverserainbow.ui.theme.ReverseRainbowTheme
import codes.chrishorner.reverserainbow.ui.theme.getInter
import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.LogLevel
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
  KmLogging.setLogLevel(LogLevel.Verbose)

  ComposeViewport(document.body!!) {
    val scope = rememberCoroutineScope()
    val fontResolver = LocalFontFamilyResolver.current
    val fontFamily = getInter()
    val persistence = WebPersistence()

    val gameLoader = remember {
      GameLoader(
        scope = scope,
        resourceLoader = { fontResolver.preload(fontFamily) },
        persistence = persistence,
      )
    }
    LaunchedEffect(Unit) { gameLoader.refresh() }

    CompositionLocalProvider(LocalPersistence provides persistence) {
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
}