package codes.chrishorner.planner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import codes.chrishorner.planner.GameLoader
import codes.chrishorner.planner.GameLoader.LoaderState.Loading
import codes.chrishorner.planner.ui.screens.game.GameUi
import codes.chrishorner.planner.ui.screens.loading.LoadingUi

@Composable
fun MainUi(
  loaderState: GameLoader.LoaderState,
  onRefresh: () -> Unit,
) {
  var loadingAnimationDone by remember { mutableStateOf(loaderState !is Loading) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.systemBars)
  ) {
      when (loaderState) {
        Loading -> LoadingUi(onReady = { loadingAnimationDone = true }, complete = false)
        is GameLoader.LoaderState.Failure -> {}
        is GameLoader.LoaderState.Success -> {
          if (!loadingAnimationDone) {
            LoadingUi(onReady = { loadingAnimationDone = true }, complete = true)
          } else {
            GameUi(loaderState.game)
          }
        }
      }
  }
}
