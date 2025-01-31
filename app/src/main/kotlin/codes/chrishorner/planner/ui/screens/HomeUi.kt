package codes.chrishorner.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import codes.chrishorner.planner.ui.theme.PlannerTheme

@Composable
fun HomeUi() {
  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) { innerPadding ->
    Text(
      text = "Hello world!",
      modifier = Modifier.padding(innerPadding),
    )
  }
}

@Preview
@Composable
private fun HomeUiPreview() {
  PlannerTheme {
    HomeUi()
  }
}