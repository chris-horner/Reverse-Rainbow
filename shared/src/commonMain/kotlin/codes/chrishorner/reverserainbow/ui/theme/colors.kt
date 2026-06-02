package codes.chrishorner.reverserainbow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface PlannerColors {
  val primary: Color
  val background: Color
  val tile: Color
  val onTile: Color
  val yellowSurface: Color
  val onYellowSurface: Color
  val greenSurface: Color
  val onGreenSurface: Color
  val blueSurface: Color
  val onBlueSurface: Color
  val purpleSurface: Color
  val onPurpleSurface: Color
}

val MaterialTheme.plannerColors: PlannerColors
  @Composable
  get() = if (isSystemInDarkTheme()) ColorsDark else ColorsLight

val MaterialLightScheme = lightColorScheme(
  primary = ColorsLight.primary,
  background = ColorsLight.background,
  surfaceContainer = ColorsLight.tile,
  onSurface = ColorsLight.onTile,
  onBackground = ColorsLight.onTile,
)

val MaterialDarkScheme = lightColorScheme(
  primary = ColorsDark.primary,
  background = ColorsDark.background,
  surfaceContainer = ColorsDark.tile,
  onSurface = ColorsDark.onTile,
  onBackground = ColorsDark.onTile,
)

object ColorsLight : PlannerColors {
  override val primary = Color(0xFFE16031)
  override val background = Color(0xFFFEFAF9)
  override val tile = Color(0xFFF8EFEC)
  override val onTile = Color(0xFF29242B)
  override val yellowSurface = Color(0xFFFFD979)
  override val onYellowSurface = Color(0xFF513E01)
  override val greenSurface = Color(0xFFB2E083)
  override val onGreenSurface = Color(0xFF32410C)
  override val blueSurface = Color(0xFF7ADDE8)
  override val onBlueSurface = Color(0xFF00424C)
  override val purpleSurface = Color(0xFFA597F1)
  override val onPurpleSurface = Color(0xFF2C2140)
}

object ColorsDark : PlannerColors {
  override val primary = Color(0xFFFC9867)
  override val background = Color(0xFF211F22)
  override val tile = Color(0xFF302E32)
  override val onTile = Color(0xFFFCFCFA)
  override val yellowSurface = Color(0xFFFFE08B)
  override val onYellowSurface = Color(0xFF513E01)
  override val greenSurface = Color(0xFFB8E38E)
  override val onGreenSurface = Color(0xFF32410C)
  override val blueSurface = Color(0xFF8DE2EC)
  override val onBlueSurface = Color(0xFF00424C)
  override val purpleSurface = Color(0xFFAC9DF2)
  override val onPurpleSurface = Color(0xFF2C2140)
}
