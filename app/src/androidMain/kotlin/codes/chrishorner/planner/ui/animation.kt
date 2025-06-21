package codes.chrishorner.planner.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.compositionLocalOf

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
  error("SharedTransitionLayout missing from composable hierarchy.")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
  error("AnimatedContent missing from composable hierarchy.")
}

const val SplashScreenFadeMillis = 150L

val JumpStartEasing = CubicBezierEasing(.36f,-0.65f,.3f,1f)
val JumpEndEasing = CubicBezierEasing(.68f,0f,.75f,1.63f)

class OvershootEasing(tension: Float) : Easing {

  private val interpolator = OvershootInterpolator(tension)

  override fun transform(fraction: Float): Float {
    return interpolator.getInterpolation(fraction)
  }
}
