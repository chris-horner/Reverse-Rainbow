package codes.chrishorner.reverserainbow.screenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

/**
 * Renders a snapshot for every non-private `@Preview` in the project.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class PreviewScreenshotTest(private val preview: ComposablePreview<AndroidPreviewInfo>) {
  companion object {
    @JvmStatic
    @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
    fun previews(): List<ComposablePreview<AndroidPreviewInfo>> =
      AndroidComposablePreviewScanner()
        .scanPackageTrees("codes.chrishorner.reverserainbow")
        .getPreviews()
  }

  @Test
  fun snapshot() {
    val info = preview.previewInfo

    val nightMode =
      if (info.uiMode and AndroidUiModes.UI_MODE_NIGHT_MASK == AndroidUiModes.UI_MODE_NIGHT_YES) {
        "night"
      } else {
        "notnight"
      }

    val qualifiers = buildList {
      if (info.widthDp > 0 && info.heightDp > 0) {
        add("w${info.widthDp}dp")
        add("h${info.heightDp}dp")
      }
      add(nightMode)
      add("xhdpi")
    }

    // Configure Robolectric to match the configuration of the `@Preview` annotation.
    RuntimeEnvironment.setQualifiers("+" + qualifiers.joinToString("-"))

    // Our file name requirements are pretty simple - strip out what we don't need.
    val filename = AndroidPreviewScreenshotIdBuilder(preview)
      .ignoreClassName()
      .ignoreIdFor("uiMode")
      .ignoreIdFor("widthDp")
      .ignoreIdFor("heightDp")
      .build()

    captureRoboImage(filePath = "$filename.png") {
      ApplyPreviewEnvironment(preview) {
        preview()
      }
    }
  }
}

@Composable
private fun ApplyPreviewEnvironment(
  preview: ComposablePreview<AndroidPreviewInfo>,
  content: @Composable () -> Unit,
) {
  val info = preview.previewInfo
  val baseDensity = LocalDensity.current
  val fontScaleAdjustedDensity = Density(baseDensity.density, info.fontScale)

  CompositionLocalProvider(LocalDensity provides fontScaleAdjustedDensity) {
    // widthDp/heightDp default to -1 when @Preview doesn't set them.
    val sizeModifier = Modifier
      .then(if (info.widthDp > 0) Modifier.width(info.widthDp.dp) else Modifier)
      .then(if (info.heightDp > 0) Modifier.height(info.heightDp.dp) else Modifier)

    Box(sizeModifier) {
      content()
    }
  }
}
