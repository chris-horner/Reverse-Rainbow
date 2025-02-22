@file:OptIn(ExperimentalSharedTransitionApi::class)

package codes.chrishorner.planner.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
  error("SharedTransitionLayout missing from composable hierarchy.")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
  error("AnimatedContent missing from composable hierarchy.")
}