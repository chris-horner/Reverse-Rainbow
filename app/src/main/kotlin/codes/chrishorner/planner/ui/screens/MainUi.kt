package codes.chrishorner.planner.ui.screens

import androidx.activity.compose.BackHandler
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
import codes.chrishorner.planner.Game
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.GameLoader.LoaderState
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.screens.about.AboutUi
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
  var navDestination by remember { mutableStateOf(NavDestination.Game) }

  BackHandler(enabled = navDestination == NavDestination.About) {
    navDestination = NavDestination.Game
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    SharedTransitionLayout {
      val state = if (!loadingAnimationDone) LoaderState.Loading else loaderState

      val screen = when {
        !loadingAnimationDone -> Screen.Loading
        navDestination == NavDestination.About -> Screen.About
        else -> when (state) {
          is LoaderState.Failure -> Screen.Error(state.type)
          LoaderState.Loading -> Screen.Loading
          is LoaderState.Success -> Screen.Loaded(state.game)
        }
      }

      AnimatedContent(targetState = screen, label = "MainUi") { targetScreen ->
        CompositionLocalProvider(
          LocalSharedTransitionScope provides this@SharedTransitionLayout,
          LocalAnimatedContentScope provides this
        ) {
          when (targetScreen) {
            is Screen.Loading -> LoadingUi(
              splashIconSize = splashIconSize,
              onAnimationDone = { loadingAnimationDone = true },
            )

            is Screen.Loaded -> GameUi(
              targetScreen.game, onOpenNyt,
              onClickAbout = { navDestination = NavDestination.About }
            )

            is Screen.Error -> ErrorUi(targetScreen.type, onRetry = onRefresh)

            is Screen.About -> AboutUi(onBack = { navDestination = NavDestination.Game })
          }
        }
      }
    }
  }
}

private enum class NavDestination {
  Game,
  About,
}

private sealed interface Screen {
  data object Loading : Screen
  data class Error(val type: GameLoader.FailureType) : Screen
  data class Loaded(val game: Game) : Screen
  data object About : Screen
}