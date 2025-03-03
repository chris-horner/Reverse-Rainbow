package codes.chrishorner.planner.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

enum class LayoutOrientation {
  Portrait,
  Landscape,
}

enum class UiMode {
  Small,
  Large,
}

val LocalLayoutOrientation = staticCompositionLocalOf { LayoutOrientation.Portrait }
val LocalUiMode = staticCompositionLocalOf { UiMode.Small }

/**
 * Sits at the root, looks at the available width and height, and decides the current
 * [LocalLayoutOrientation] and [LocalUiMode].
 */
@Composable
fun UiLayoutManager(content: @Composable () -> Unit) {
  BoxWithConstraints {
    val layout = when {
      maxHeight < maxWidth && maxHeight < 700.dp -> LayoutOrientation.Landscape
      else -> LayoutOrientation.Portrait
    }

    val uiMode = when {
      min(maxWidth, maxHeight) > 700.dp -> UiMode.Large
      else -> UiMode.Small
    }

    CompositionLocalProvider(
      LocalLayoutOrientation.provides(layout),
      LocalUiMode.provides(uiMode)
    ) {
      content()
    }
  }
}