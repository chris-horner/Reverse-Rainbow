package codes.chrishorner.planner.ui.screens.loading

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.SplashScreenFadeMillis
import codes.chrishorner.planner.ui.theme.plannerColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoadingUi(splashIconSize: DpSize, onAnimationDone: () -> Unit) {
  with(LocalSharedTransitionScope.current) {
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
}

context(SharedTransitionScope)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BoxScope.LoadingTiles(
  onAnimationDone: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val state = remember { LoadingAnimationState(scope) }
  LaunchedEffect(state.completedIntro) {
    if (state.completedIntro) onAnimationDone()
  }

  Box(
    modifier = Modifier
      .offset { state.yellowOffset.toIntOffset() }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.YELLOW),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.yellowSurface, shape = RoundedCornerShape(6.dp))
  )

  Box(
    modifier = Modifier
      .offset { state.greenOffset.toIntOffset() }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.GREEN),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.greenSurface, shape = RoundedCornerShape(6.dp))
  )

  Box(
    modifier = Modifier
      .offset { state.blueOffset.toIntOffset() }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.BLUE),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.blueSurface, shape = RoundedCornerShape(6.dp))
  )

  Box(
    modifier = Modifier
      .offset { state.purpleOffset.toIntOffset() }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(Category.PURPLE),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      .background(MaterialTheme.plannerColors.purpleSurface, shape = RoundedCornerShape(6.dp))
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
          easing = { OvershootInterpolator(6f).getInterpolation(it) },
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

context (Density)
private fun DpOffset.toIntOffset() = IntOffset(x.roundToPx(), y.roundToPx())

// Starting angles for each of the colors.
private val YellowAngle = Math.toRadians(225.0).toFloat()
private val GreenAngle = Math.toRadians(315.0).toFloat()
private val BlueAngle = Math.toRadians(135.0).toFloat()
private val PurpleAngle = Math.toRadians(45.0).toFloat()