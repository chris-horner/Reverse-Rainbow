package codes.chrishorner.planner.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Makes sure content don't stretch too wide on larger screen sizes.
 */
@Composable
fun CappedWidthContainer(content: @Composable () -> Unit) {
  Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .widthIn(max = CappedWidth)
        .fillMaxHeight()
    ) {
      content()
    }
  }
}

val CappedWidth = 500.dp

