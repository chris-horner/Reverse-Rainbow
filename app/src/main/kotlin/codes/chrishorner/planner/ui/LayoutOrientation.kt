package codes.chrishorner.planner.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

enum class LayoutOrientation {
  Portrait,
  Landscape,
}

val LocalLayoutOrientation = staticCompositionLocalOf { LayoutOrientation.Portrait }

@Composable
fun UiLayoutManager(content: @Composable () -> Unit) {
  BoxWithConstraints {
    val layout = when {
      maxHeight < maxWidth && maxHeight < 700.dp -> LayoutOrientation.Landscape
      else -> LayoutOrientation.Portrait
    }

    CompositionLocalProvider(LocalLayoutOrientation.provides(layout)) {
      content()
    }
  }
}