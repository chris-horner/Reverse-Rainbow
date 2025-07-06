package codes.chrishorner.reverserainbow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation.Settings
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import codes.chrishorner.reverserainbow.ui.LocalUiMode
import codes.chrishorner.reverserainbow.ui.UiMode
import org.jetbrains.compose.resources.Font
import reverserainbow.app.generated.resources.Res
import reverserainbow.app.generated.resources.inter_variable

@Suppress("UnusedReceiverParameter") // Useful for scoping call sites.
val Typography.tile: TextStyle
  @Composable
  get() {
    val uiMode = LocalUiMode.current
    val density = LocalDensity.current
    val fontFamily = getInter()

    val style = remember(uiMode, density) {
      when (uiMode) {
        UiMode.Small -> TextStyle(
          fontWeight = Bold,
          textAlign = TextAlign.Center,
          fontSize = 16.sp,
          lineHeight = 20.sp,
          letterSpacing = 0.2.sp,
          fontFamily = fontFamily,
        )

        UiMode.Large -> TextStyle(
          fontWeight = Bold,
          textAlign = TextAlign.Center,
          fontSize = 18.sp,
          lineHeight = 22.sp,
          letterSpacing = 0.1.sp,
          fontFamily = fontFamily,
        )
      }
    }

    return style
  }

@Composable
fun getInter(): FontFamily = FontFamily(
  Font(
    resource = Res.font.inter_variable,
    variationSettings = Settings(weight(Normal.weight)),
    weight = Normal,
  ),
  Font(
    resource = Res.font.inter_variable,
    variationSettings = Settings(weight(Medium.weight)),
    weight = Medium,
  ),
  Font(
    resource = Res.font.inter_variable,
    variationSettings = Settings(weight(Bold.weight)),
    weight = Bold,
  ),
  Font(
    resource = Res.font.inter_variable,
    variationSettings = Settings(weight(ExtraBold.weight)),
    weight = ExtraBold,
  ),
)

@Composable
fun getTypography(): Typography {
  val fontFamily = getInter()

  return Typography(
    displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = fontFamily),
    displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = fontFamily),
    displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = fontFamily),
    headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = fontFamily),
    headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = fontFamily),
    headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = fontFamily),
    titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = fontFamily),
    titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = fontFamily),
    titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = fontFamily),
    bodyLarge = MaterialTheme.typography.bodyLarge.copy(
      fontFamily = fontFamily,
      letterSpacing = 0.3.sp,
    ),
    bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = fontFamily),
    bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
    labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = fontFamily),
    labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = fontFamily),
    labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = fontFamily),
  )
}
