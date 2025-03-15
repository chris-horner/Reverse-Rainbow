package codes.chrishorner.planner.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.compositionLocalOf

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
  error("SharedTransitionLayout missing from composable hierarchy.")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
  error("AnimatedContent missing from composable hierarchy.")
}

const val SplashScreenFadeMillis = 150L

object OvershootEasing : Easing {

  private val interpolator = OvershootInterpolator(6f)

  override fun transform(fraction: Float): Float {
    return interpolator.getInterpolation(fraction)
  }
}
