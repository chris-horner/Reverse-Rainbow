package codes.chrishorner.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import codes.chrishorner.planner.ui.screens.HomeUi
import codes.chrishorner.planner.ui.theme.PlannerTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val viewModel by viewModels<GameLoader.ViewModelWrapper>()
    val gameLoader = viewModel.gameLoader
    if (savedInstanceState == null) {
      gameLoader.refresh()
    }

    setContent {
      PlannerTheme {
        HomeUi(
          loaderState = gameLoader.state.value,
          onRefresh = { gameLoader.refresh() },
        )
      }
    }
  }
}
