package codes.chrishorner.reverserainbow.ui.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.ReverseRainbowTheme

/**
 * Render the app background, and provide some defaults for composition locals.
 */
@Composable
fun PreviewUi(
  orientation: LayoutOrientation = LayoutOrientation.Portrait,
  width: Dp = Dp.Unspecified,
  height: Dp = Dp.Unspecified,
  content: @Composable () -> Unit,
) {
  ReverseRainbowTheme {
    SharedTransitionLayout {
      AnimatedContent(targetState = Unit) { _ ->
        CompositionLocalProvider(
          LocalSharedTransitionScope provides this@SharedTransitionLayout,
          LocalAnimatedContentScope provides this@AnimatedContent,
          LocalLayoutOrientation provides orientation,
        ) {
          Box(
            modifier = Modifier
              .then(if (width.isSpecified) Modifier.width(width) else Modifier)
              .then(if (height.isSpecified) Modifier.height(height) else Modifier)
              .background(MaterialTheme.colorScheme.background)
          ) {
            content()
          }
        }
      }
    }
  }
}

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Small Light", widthDp = 360, heightDp = 800)
@Preview(
  name = "Small Dark",
  widthDp = 360,
  heightDp = 800,
  uiMode = AndroidUiModes.UI_MODE_NIGHT_YES,
)
annotation class PreviewLightDarkPortraitSmall

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(
  name = "Small Landscape",
  widthDp = 800,
  heightDp = 360,
)
annotation class PreviewLandscapeSmall