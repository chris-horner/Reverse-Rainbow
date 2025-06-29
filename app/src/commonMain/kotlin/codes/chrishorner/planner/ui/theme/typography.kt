package codes.chrishorner.planner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import codes.chrishorner.planner.ui.LocalUiMode
import codes.chrishorner.planner.ui.UiMode

val Typography.tile: TextStyle
  @Composable
  get() {
    val uiMode = LocalUiMode.current
    val density = LocalDensity.current

    val style = remember(uiMode, density) {
      when (uiMode) {
        UiMode.Small -> TextStyle(
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          fontSize = 16.sp,
          lineHeight = 20.sp,
          letterSpacing = 0.2.sp,
        )

        UiMode.Large -> TextStyle(
          fontWeight = FontWeight.ExtraBold,
          textAlign = TextAlign.Center,
          fontSize = 18.sp,
          lineHeight = 22.sp,
          letterSpacing = 0.1.sp,
        )
      }
    }

    return style
  }