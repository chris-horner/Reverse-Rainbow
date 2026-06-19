package codes.chrishorner.reverserainbow.screenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

@RunWith(Parameterized::class)
class PreviewScreenshotTest(
  private val preview: ComposablePreview<AndroidPreviewInfo>,
) {
  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun previews(): List<ComposablePreview<AndroidPreviewInfo>> =
      AndroidComposablePreviewScanner()
        .scanPackageTrees("codes.chrishorner.reverserainbow")
        .getPreviews()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun snapshot() = runDesktopComposeUiTest {
    setContent {
      ApplyPreviewEnvironment(preview) {
        preview()
      }
    }

    val filename = AndroidPreviewScreenshotIdBuilder(preview)
      // uiMode already appends "Light" and "Dark" in the name
      .ignoreIdFor("uiMode")
      .build()

    onRoot().captureRoboImage(
      filePath = "src/jvmTest/snapshots/$filename.png",
      roborazziOptions = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(
          // Account for differences between environments when rendering fonts.
          changeThreshold = 0.02f,
        ),
      ),
    )
  }
}

/**
 * We make use of Compose Desktop to render snapshots quickly on the JVM, but a consequence is that
 * `@Preview` annotation fields need to be translated into something Compose Desktop can work with.
 *
 * Currently this support light/dark configurations, font scale, and size.
 */
@OptIn(InternalComposeUiApi::class)
@Composable
private fun ApplyPreviewEnvironment(
  preview: ComposablePreview<AndroidPreviewInfo>,
  content: @Composable () -> Unit,
) {
  val info = preview.previewInfo

  val systemTheme =
    if (info.uiMode and AndroidUiModes.UI_MODE_NIGHT_MASK == AndroidUiModes.UI_MODE_NIGHT_YES) {
      SystemTheme.Dark
    } else {
      SystemTheme.Light
    }

  val baseDensity = LocalDensity.current
  val fontScaleAdjustedDensity = Density(baseDensity.density, info.fontScale)

  CompositionLocalProvider(
    LocalSystemTheme provides systemTheme,
    LocalDensity provides fontScaleAdjustedDensity,
  ) {
    // widthDp/heightDp default to -1 when @Preview doesn't set them.
    val sizeModifier = Modifier
      .then(if (info.widthDp > 0) Modifier.width(info.widthDp.dp) else Modifier)
      .then(if (info.heightDp > 0) Modifier.height(info.heightDp.dp) else Modifier)

    Box(sizeModifier) {
      content()
    }
  }
}
