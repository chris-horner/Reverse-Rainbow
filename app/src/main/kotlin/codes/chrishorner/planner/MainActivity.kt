package codes.chrishorner.planner

import android.content.Context
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
import androidx.core.view.doOnLayout
import codes.chrishorner.planner.ui.SplashScreenFadeMillis
import codes.chrishorner.planner.ui.screens.MainUi
import codes.chrishorner.planner.ui.theme.PlannerTheme

class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<GameLoader.ViewModelWrapper>()
  private val splashIconSize = mutableStateOf(DpSize.Unspecified)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    handleSplashScreen()

    val gameLoader = viewModel.gameLoader

    setContent {
      PlannerTheme {
        MainUi(
          loaderState = gameLoader.state.value,
          splashIconSize = splashIconSize.value,
          onRefresh = { gameLoader.refresh() },
        )
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.gameLoader.refreshIfNecessary()
  }

  private fun handleSplashScreen() {
    splashScreen.setOnExitAnimationListener { splashScreenView ->
      splashScreenView.doOnLayout {
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
}

context(Context)
private fun Int.pxToDp(): Dp {
  val value = this / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
  return value.dp
}