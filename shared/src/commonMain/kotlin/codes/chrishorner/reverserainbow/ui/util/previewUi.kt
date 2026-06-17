package codes.chrishorner.reverserainbow.ui.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.ReverseRainbowTheme

/**
 * Render the app background, and provide some defaults for composition locals.
 */
@Composable
fun PreviewUi(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  ReverseRainbowTheme {
    SharedTransitionLayout {
      AnimatedContent(targetState = Unit) { _ ->
        CompositionLocalProvider(
          LocalSharedTransitionScope provides this@SharedTransitionLayout,
          LocalAnimatedContentScope provides this@AnimatedContent,
        ) {
          Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
            content()
          }
        }
      }
    }
  }
}
