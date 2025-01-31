package codes.chrishorner.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import codes.chrishorner.planner.ui.screens.HomeUi
import codes.chrishorner.planner.ui.theme.PlannerTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PlannerTheme {
        HomeUi()
      }
    }
  }
}
