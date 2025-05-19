package codes.chrishorner.planner.ui.screens.game

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// This approach seems to have fewer bugs than the new `autoSize` parameter on `BasicText`.
@Composable
fun TileText(
  text: String,
  color: Color,
) = with(LocalDensity.current) {
  BoxWithConstraints {
    val textMeasurer = rememberTextMeasurer()
    var style = MaterialTheme.typography.titleMedium.copy(
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      lineHeight = (MaterialTheme.typography.titleMedium.lineHeight.toPx() - 4.dp.toPx()).toSp(),
      color = color,
    )

    fun measure(text: String) = textMeasurer.measure(
      text = text,
      style = style,
      constraints = this.constraints,
      softWrap = false
    )

    var result = measure(text)

    // Take advantage of the fact that Connections is only in English. Manually replace spaces with
    // newlines if we happen to overflow. This seems more reliable than what Compose's LineBreak API
    // is able to provide.
    val text = if (result.didOverflowWidth) text.replace(' ', '\n') else text
    result = measure(text)

    while (result.hasVisualOverflow) {
      style = style.copy(fontSize = style.fontSize * 0.9f)
      result = measure(text)

      if (style.fontSize.toPx() <= 8.dp.toPx()) break
    }

    Text(
      text = text,
      color = color,
      style = style,
    )
  }
}
