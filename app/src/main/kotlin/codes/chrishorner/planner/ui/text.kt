package codes.chrishorner.planner.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// This approach seems to have fewer bugs than the new `autoSize` parameter on `BasicText`.
@Composable
fun AutoSizeText(
  text: String,
  modifier: Modifier = Modifier,
  maxLines: Int = Int.MAX_VALUE,
  color: Color = Color.Unspecified,
  style: TextStyle = LocalTextStyle.current,
) {
  var textStyle by remember { mutableStateOf(style) }
  var readyToDraw by remember { mutableStateOf(false) }

  Text(
    text = text,
    color = color,
    maxLines = maxLines,
    style = textStyle,
    softWrap = false,
    onTextLayout = { result ->
      if (result.didOverflowWidth) {
        val reducedSize = textStyle.fontSize * 0.9f
        if (reducedSize <= 8.sp) {
          textStyle = textStyle.copy(fontSize = 8.sp)
          readyToDraw = true
        } else {
          textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
        }
      } else {
        readyToDraw = true
      }
    },
    modifier = modifier.drawWithContent {
      if (readyToDraw) drawContent()
    },
  )
}
