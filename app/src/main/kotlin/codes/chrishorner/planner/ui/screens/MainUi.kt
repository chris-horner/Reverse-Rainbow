package codes.chrishorner.planner.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import codes.chrishorner.planner.GameLoader.LoaderState
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.screens.error.ErrorUi
import codes.chrishorner.planner.ui.screens.game.GameUi
import codes.chrishorner.planner.ui.screens.loading.LoadingUi

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainUi(
  loaderState: LoaderState,
  splashIconSize: DpSize,
  onRefresh: () -> Unit,
  onOpenNyt: () -> Unit,
) {
  var loadingAnimationDone by remember { mutableStateOf(loaderState !is LoaderState.Loading) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    SharedTransitionLayout {
      val state = if (!loadingAnimationDone) LoaderState.Loading else loaderState

      AnimatedContent(targetState = state, label = "MainUi") { targetState ->
        CompositionLocalProvider(
          LocalSharedTransitionScope provides this@SharedTransitionLayout,
          LocalAnimatedContentScope provides this
        ) {
          when (targetState) {
            LoaderState.Loading -> LoadingUi(
              splashIconSize, onAnimationDone = { loadingAnimationDone = true },
            )

            is LoaderState.Failure -> ErrorUi(targetState.type, onRetry = onRefresh)
            is LoaderState.Success -> GameUi(targetState.game, onOpenNyt)
          }
        }
      }
    }
  }
}
