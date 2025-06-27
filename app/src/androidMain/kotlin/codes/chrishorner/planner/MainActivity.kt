package codes.chrishorner.planner

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import codes.chrishorner.planner.ui.SplashScreenFadeMillis
import codes.chrishorner.planner.ui.screens.MainUi
import codes.chrishorner.planner.ui.theme.PlannerTheme
import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.LogLevel

class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<GameLoaderViewModel>()

  // We want to seamlessly fade from the splash screen into a complex loading animation. To do that,
  // we need to keep track of how big the splash screen's icon was once we have that information,
  // and shuttle it along to LoadingUi.
  private val splashIconSize = mutableStateOf(DpSize.Unspecified)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    KmLogging.setLogLevel(if (BuildConfig.DEBUG) LogLevel.Verbose else LogLevel.Off)
    enableEdgeToEdge()
    handleSplashScreen()

    val gameLoader = viewModel.gameLoader

    setContent {
      PlannerTheme {
        MainUi(
          loaderState = gameLoader.state.value,
          splashIconSize = splashIconSize.value,
          onRefresh = { gameLoader.refresh() },
          onOpenNyt = {
            startActivity(
              Intent(Intent.ACTION_VIEW, "https://www.nytimes.com/games/connections".toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
          }
        )
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.gameLoader.refreshIfNecessary()
  }

  /**
   * Listens for the resolved size of the splash screen icon, then fades out and removes splash
   * screen content.
   */
  private fun handleSplashScreen() {
    splashScreen.setOnExitAnimationListener { splashScreenView ->
      splashScreenView.iconView?.doOnLayout {
        splashIconSize.value = DpSize(it.width.pxToDp(), it.height.pxToDp())
      }

      splashScreenView.animate()
        .alpha(0f)
        .setDuration(SplashScreenFadeMillis)
        .withEndAction {
          splashScreenView.remove()
        }
    }
  }

  private fun Int.pxToDp(): Dp {
    val value = this / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    return value.dp
  }
}
