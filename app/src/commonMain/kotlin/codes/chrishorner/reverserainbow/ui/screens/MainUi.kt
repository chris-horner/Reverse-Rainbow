package codes.chrishorner.reverserainbow.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import codes.chrishorner.reverserainbow.Game
import codes.chrishorner.reverserainbow.GameLoader
import codes.chrishorner.reverserainbow.GameLoader.LoaderState
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.UiLayoutManager
import codes.chrishorner.reverserainbow.ui.screens.about.AboutUi
import codes.chrishorner.reverserainbow.ui.screens.error.ErrorUi
import codes.chrishorner.reverserainbow.ui.screens.game.GameUi
import codes.chrishorner.reverserainbow.ui.screens.loading.LoadingUi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Root UI of the app. MainUi is responsible for:
 * - drawing the app background
 * - maintaining a dirt simple navigation stack
 * - showing appropriate screens based on [LoaderState]
 * - forwarding events back up to the main UI host.
 */
@Composable
fun MainUi(
  loaderState: LoaderState,
  splashIconSize: DpSize,
  onRefresh: () -> Unit,
  onOpenNyt: () -> Unit,
) {
  var loadingAnimationDone by remember { mutableStateOf(loaderState !is LoaderState.Loading) }
  var destination by rememberSaveable { mutableStateOf(NavDestination.Game) }

  // Since this app is so simple we don't make use of any navigation frameworks. A
  // consequence of that is that calls to rememberSaveable will be cleared on navigation, so
  // we take care of holding that state ourselves.
  val saveableStateHolder = rememberSaveableStateHolder()

  val screen = deriveScreenFrom(destination, loadingAnimationDone, loaderState)
  val transition = rememberPredictiveBackTransition(
    currentScreen = screen,
    isBackEnabled = destination == NavDestination.About,
    // Only one subscreen in the app. If we're going back it's to the Game destination.
    backTarget = deriveScreenFrom(NavDestination.Game, loadingAnimationDone, loaderState),
    onBackCompleted = { destination = NavDestination.Game },
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    UiLayoutManager {
      SharedTransitionLayout {
        transition.AnimatedContent(
          transitionSpec = transitionSpec,
        ) { targetScreen ->
          CompositionLocalProvider(
            LocalSharedTransitionScope provides this@SharedTransitionLayout,
            LocalAnimatedContentScope provides this
          ) {
            // Use the target screen's class as a key for associated saved state.
            saveableStateHolder.SaveableStateProvider(key = targetScreen::class.toString()) {

              ShowScreen(
                screen = targetScreen,
                splashIconSize = splashIconSize,
                onAction = { action ->
                  when (action) {
                    ScreenAction.FinishLoading -> loadingAnimationDone = true
                    is ScreenAction.Navigate -> destination = action.destination
                    ScreenAction.OpenNyt -> onOpenNyt()
                    ScreenAction.Refresh -> onRefresh()
                  }
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun rememberPredictiveBackTransition(
  currentScreen: Screen,
  backTarget: Screen,
  isBackEnabled: Boolean,
  onBackCompleted: () -> Unit,
): Transition<Screen> {
  val transitionState = remember { SeekableTransitionState(currentScreen) }
  val coroutineScope = rememberCoroutineScope()
  val navEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
  val currentBackTarget by rememberUpdatedState(backTarget)

  var isPredictiveBackInProgress by remember { mutableStateOf(false) }

  // If currentScreen changes outside a predictive back swipe, then kick off the animation.
  LaunchedEffect(currentScreen, isPredictiveBackInProgress) {
    if (!isPredictiveBackInProgress && transitionState.targetState != currentScreen) {
      // If a previous transition is still in flight, snap it to completion and yield a frame so
      // AnimatedContent fully exits the outgoing content.
      if (transitionState.currentState != transitionState.targetState) {
        transitionState.snapTo(transitionState.targetState)
        withFrameNanos {}
      }
      transitionState.animateTo(currentScreen)
    }
  }

  val inNavigationGesture by remember {
    derivedStateOf { navEventState.transitionState is NavigationEventTransitionState.InProgress }
  }

  // If we start a navigation gesture handle it similarly to playing the animation but instead seek
  // the animation based on the gesture progress.
  LaunchedEffect(inNavigationGesture) {
    if (!inNavigationGesture) return@LaunchedEffect

    isPredictiveBackInProgress = true

    if (transitionState.currentState != transitionState.targetState) {
      transitionState.snapTo(transitionState.targetState)
      withFrameNanos {}
    }

    transitionState.seekTo(fraction = 0f, targetState = currentBackTarget)

    snapshotFlow { (navEventState.transitionState as? NavigationEventTransitionState.InProgress)?.latestEvent?.progress }
      .filterNotNull()
      .collect { progress ->
        transitionState.seekTo(fraction = progress, targetState = currentBackTarget)
      }
  }

  NavigationBackHandler(
    state = navEventState,
    isBackEnabled = isBackEnabled,
    onBackCompleted = {
      onBackCompleted()
      // If we were in a predictive back swipe make sure we finish the animation.
      if (isPredictiveBackInProgress) {
        coroutineScope.launch {
          transitionState.animateTo(currentBackTarget)
          isPredictiveBackInProgress = false
        }
      }
    },
    onBackCancelled = {
      coroutineScope.launch {
        transitionState.snapTo(transitionState.currentState)
        isPredictiveBackInProgress = false
      }
    },
  )

  return rememberTransition(transitionState, label = "MainUi")
}

private enum class NavDestination {
  Game,
  About,
}

// All screens that can be shown in the app.
@Stable
private sealed interface Screen {
  data object Loading : Screen
  data class Error(val type: GameLoader.FailureType) : Screen
  data class Loaded(val date: LocalDate, val game: Game) : Screen
  data object About : Screen
}

private sealed interface ScreenAction {
  data class Navigate(val destination: NavDestination) : ScreenAction
  data object Refresh : ScreenAction
  data object OpenNyt : ScreenAction
  data object FinishLoading : ScreenAction
}

private fun deriveScreenFrom(
  navDestination: NavDestination,
  loadingAnimationDone: Boolean,
  loaderState: LoaderState
): Screen {
  return when (navDestination) {
    NavDestination.About -> Screen.About
    NavDestination.Game -> when (loaderState) {
      is LoaderState.Failure -> Screen.Error(loaderState.type)
      LoaderState.Loading -> Screen.Loading
      // Even if we've finished loading, continue to show the loading screen until the loading
      // animation is complete.
      is LoaderState.Success -> if (!loadingAnimationDone) {
        Screen.Loading
      } else {
        Screen.Loaded(loaderState.date, loaderState.game)
      }
    }
  }
}

@Composable
private fun ShowScreen(
  screen: Screen,
  splashIconSize: DpSize,
  onAction: (ScreenAction) -> Unit,
) {
  when (screen) {
    is Screen.Loading -> LoadingUi(
      splashIconSize = splashIconSize,
      onAnimationDone = { onAction(ScreenAction.FinishLoading) },
    )

    is Screen.Loaded -> GameUi(
      game = screen.game,
      date = screen.date,
      onOpenNyt = { onAction(ScreenAction.OpenNyt) },
      onClickAbout = { onAction(ScreenAction.Navigate(NavDestination.About)) }
    )

    is Screen.Error -> ErrorUi(screen.type, onRetry = { onAction(ScreenAction.Refresh) })

    is Screen.About -> AboutUi(onBack = { onAction(ScreenAction.Navigate(NavDestination.Game)) })
  }
}

private val forwardsTransform: ContentTransform = run {
  val inSpec = fadeIn(
    animationSpec = tween(220, delayMillis = 90)
  ) + scaleIn(
    initialScale = 0.92f,
    animationSpec = tween(220, delayMillis = 90, easing = EaseOutBack),
  )

  val outSpec = fadeOut(animationSpec = tween(90))

  inSpec togetherWith outSpec
}

private val backwardsTransform: ContentTransform = run {
  val inSpec = fadeIn(
    animationSpec = tween(220, delayMillis = 90),
  ) + scaleIn(
    initialScale = 1.12f,
    animationSpec = tween(220, delayMillis = 90, easing = EaseOutBack),
  )

  val outSpec = fadeOut(
    animationSpec = tween(durationMillis = 90),
  ) + scaleOut(
    targetScale = 0.92f,
    animationSpec = tween(90, easing = EaseInBack),
  )

  inSpec togetherWith outSpec
}

private val transitionSpec: AnimatedContentTransitionScope<Screen>.() -> ContentTransform = {
  if (initialState is Screen.About) backwardsTransform else forwardsTransform
}