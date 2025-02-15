package codes.chrishorner.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import codes.chrishorner.planner.ui.screens.MainUi
import codes.chrishorner.planner.ui.theme.PlannerTheme

class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<GameLoader.ViewModelWrapper>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val gameLoader = viewModel.gameLoader

    setContent {
      PlannerTheme {
        MainUi(
          loaderState = gameLoader.state.value,
          onRefresh = { gameLoader.refresh() },
        )
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.gameLoader.refreshIfNecessary()
  }
}
