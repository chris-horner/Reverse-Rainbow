package codes.chrishorner.reverserainbow.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
  error("SharedTransitionLayout missing from composable hierarchy.")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
  error("AnimatedContent missing from composable hierarchy.")
}

const val SplashScreenFadeMillis = 150L

val JumpStartEasing = CubicBezierEasing(.36f,-0.65f,.3f,1f)
val JumpEndEasing = CubicBezierEasing(.68f,0f,.75f,1.63f)

class OvershootEasing(private val tension: Float) : Easing {

  override fun transform(fraction: Float): Float {
    val fraction = fraction - 1f
    return fraction * fraction * ((tension + 1) * fraction + tension) + 1f
  }
}

fun <T> tileSpringSpec(): FiniteAnimationSpec<T> {
  return spring(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMediumLow,
  )
}

/**
 * Slides and fades an individual tiles when the `Grid` enters the screen.
 *
 * `row` 0 is the top of the grid. `row` 3 is the bottom.
 */
@Composable
fun getGridEnterTransitionFor(row: Int): EnterTransition {
  val duration = 200
  // Animate tiles lower down in the grid earlier than those higher up to create a nice effect.
  val delay = 200 - (row * 50)

  val maxSlideDistance = with(LocalDensity.current) { 48.dp.roundToPx() }

  return slideInVertically(
    animationSpec = tween(
      durationMillis = duration,
      delayMillis = delay,
      easing = OvershootEasing(1f),
    ),
    initialOffsetY = { -(it / 2).coerceAtMost(maxSlideDistance) },
  ) + fadeIn(
    animationSpec = tween(
      durationMillis = duration,
      delayMillis = delay,
    )
  )
}