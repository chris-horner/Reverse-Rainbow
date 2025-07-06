package codes.chrishorner.reverserainbow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ReverseRainbowTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = if (isSystemInDarkTheme()) MaterialDarkScheme else MaterialLightScheme,
    content = content,
    typography = getTypography(),
  )
}