package codes.chrishorner.reverserainbow.ui.screens.loading

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.OvershootEasing
import codes.chrishorner.reverserainbow.ui.SplashScreenFadeMillis
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.plannerColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Begins by placing 4 colored squares in an arrangement that closely matches the app icon shown in
 * the splash screen. Those squares then animate outwards in 4 directions. Once the squares have
 * reached their final distance from the center, `onAnimationDone` will be invoked.
 *
 * If this composable continues to be shown on screen (if loading is taking a long time), then the
 * squares will spin in a circle every few seconds.
 */
@Composable
fun LoadingUi(splashIconSize: DpSize, onAnimationDone: () -> Unit) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize(),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(splashIconSize),
    ) {
      LoadingTiles(onAnimationDone)
    }
  }
}

@Composable
private fun LoadingTiles(
  onAnimationDone: () -> Unit,
) = with(LocalSharedTransitionScope.current) {
  val scope = rememberCoroutineScope()
  val state = remember { LoadingAnimationState(scope) }
  LaunchedEffect(state.completedIntro) {
    if (state.completedIntro) onAnimationDone()
  }

  Box(
    modifier = Modifier
      .offset { intOffsetFrom(state.yellowOffset) }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.YELLOW),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.yellowSurface, shape = TileShape)
  )

  Box(
    modifier = Modifier
      .offset { intOffsetFrom(state.greenOffset) }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.GREEN),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.greenSurface, shape = TileShape)
  )

  Box(
    modifier = Modifier
      .offset { intOffsetFrom(state.blueOffset) }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.BLUE),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.blueSurface, shape = TileShape)
  )

  Box(
    modifier = Modifier
      .offset { intOffsetFrom(state.purpleOffset) }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.PURPLE),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.purpleSurface, shape = TileShape)
  )
}

@Stable
private class LoadingAnimationState(scope: CoroutineScope) {
  private val distanceAnimatable = Animatable(48.dp, Dp.VectorConverter)
  private val angleAnimatable = Animatable(0f)

  var completedIntro by mutableStateOf(false)
    private set

  val yellowOffset by derivedStateOf {
    DpOffset(
      x = cos(angleAnimatable.value + YellowAngle) * distanceAnimatable.value,
      y = sin(angleAnimatable.value + YellowAngle) * distanceAnimatable.value,
    )
  }

  val greenOffset by derivedStateOf {
    DpOffset(
      x = cos(angleAnimatable.value + GreenAngle) * (distanceAnimatable.value),
      y = sin(angleAnimatable.value + GreenAngle) * (distanceAnimatable.value),
    )
  }

  val blueOffset by derivedStateOf {
    DpOffset(
      x = cos(angleAnimatable.value + BlueAngle) * (distanceAnimatable.value),
      y = sin(angleAnimatable.value + BlueAngle) * (distanceAnimatable.value),
    )
  }

  val purpleOffset by derivedStateOf {
    DpOffset(
      x = cos(angleAnimatable.value + PurpleAngle) * (distanceAnimatable.value),
      y = sin(angleAnimatable.value + PurpleAngle) * (distanceAnimatable.value),
    )
  }

  init {
    scope.launch {
      delay(SplashScreenFadeMillis)

      distanceAnimatable.animateTo(
        targetValue = 80.dp,
        animationSpec = tween(
          durationMillis = 500,
          easing = OvershootEasing(6f),
        )
      )

      completedIntro = true

      delay(100)
      angleAnimatable.animateTo(
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
          animation = tween(
            durationMillis = 1_800,
            easing = FastOutSlowInEasing,
          ),
        ),
      )
    }
  }
}

private fun Density.intOffsetFrom(dpOffset: DpOffset): IntOffset {
  return IntOffset(dpOffset.x.roundToPx(), dpOffset.y.roundToPx())
}

// Starting angles for each of the colors.
private val YellowAngle = 225f.toRadians()
private val GreenAngle = 315f.toRadians()
private val BlueAngle = 135f.toRadians()
private val PurpleAngle = 45f.toRadians()

private fun Float.toRadians(): Float = (this / 180.0 * PI).toFloat()