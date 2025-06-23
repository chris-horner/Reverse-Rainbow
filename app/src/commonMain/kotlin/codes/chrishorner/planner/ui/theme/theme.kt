package codes.chrishorner.planner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PlannerTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = if (isSystemInDarkTheme()) MaterialDarkScheme else MaterialLightScheme,
    content = content,
  )
}