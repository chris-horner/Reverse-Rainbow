package codes.chrishorner.planner.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.GameLoader.LoaderState.Loading
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.screens.game.GameUi
import codes.chrishorner.planner.ui.screens.loading.LoadingUi

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainUi(
  loaderState: GameLoader.LoaderState,
  onRefresh: () -> Unit,
) {
  var loadingAnimationDone by remember { mutableStateOf(loaderState !is Loading) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.systemBars)
  ) {
    SharedTransitionLayout {
      AnimatedContent(targetState = loaderState, label = "MainUi") { loaderState ->
        CompositionLocalProvider(
          LocalSharedTransitionScope provides this@SharedTransitionLayout,
          LocalAnimatedContentScope provides this
        ) {
          when (loaderState) {
            Loading -> LoadingUi(onReady = { loadingAnimationDone = true }, complete = false)
            is GameLoader.LoaderState.Failure -> {}
            is GameLoader.LoaderState.Success -> {
              if (!loadingAnimationDone) {
                LoadingUi(onReady = { loadingAnimationDone = true }, complete = true)
              } else {
                GameUi(loaderState.game)
              }
            }
          }
        }
      }
    }
  }
}
