package codes.chrishorner.planner.ui.screens.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.GameLoader.FailureType
import codes.chrishorner.planner.R
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.theme.TileShape
import codes.chrishorner.planner.ui.theme.plannerColors
import codes.chrishorner.planner.ui.util.CappedWidthContainer

@Composable
fun ErrorUi(failureType: FailureType, onRetry: () -> Unit) {

  CappedWidthContainer {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .systemBarsPadding()
        .padding(vertical = 16.dp)
    ) {
      Spacer(modifier = Modifier.weight(3f))

      Text(
        text = when (failureType) {
          FailureType.NETWORK -> stringResource(R.string.error_title_network)
          FailureType.HTTP -> stringResource(R.string.error_title_http)
          FailureType.PARSING -> stringResource(R.string.error_title_parsing)
        },
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.size(24.dp))

      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .fillMaxWidth()
          .widthIn(max = 320.dp)
      ) {
        CategoryBlock(
          category = Category.YELLOW,
          backgroundColor = MaterialTheme.plannerColors.yellowSurface,
          foregroundColor = MaterialTheme.plannerColors.onYellowSurface,
          image = painterResource(R.drawable.sad_face1),
        )
        CategoryBlock(
          category = Category.GREEN,
          backgroundColor = MaterialTheme.plannerColors.greenSurface,
          foregroundColor = MaterialTheme.plannerColors.onYellowSurface,
          image = painterResource(R.drawable.sad_face2),
        )
        CategoryBlock(
          category = Category.BLUE,
          backgroundColor = MaterialTheme.plannerColors.blueSurface,
          foregroundColor = MaterialTheme.plannerColors.onBlueSurface,
          image = painterResource(R.drawable.sad_face3),
        )
        CategoryBlock(
          category = Category.PURPLE,
          backgroundColor = MaterialTheme.plannerColors.purpleSurface,
          foregroundColor = MaterialTheme.plannerColors.onPurpleSurface,
          image = painterResource(R.drawable.sad_face4),
        )
      }

      Spacer(modifier = Modifier.size(24.dp))

      Text(
        text = when (failureType) {
          FailureType.NETWORK -> stringResource(R.string.error_message_network)
          FailureType.HTTP -> stringResource(R.string.error_message_http)
          FailureType.PARSING -> stringResource(R.string.error_message_parsing)
        },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .widthIn(max = 300.dp)
      )

      Spacer(modifier = Modifier.size(32.dp))

      OutlinedButton(
        onClick = onRetry,
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.widthIn(min = 248.dp)
      ) {
        Text(text = stringResource(R.string.error_retry_button))
      }

      Spacer(modifier = Modifier.weight(5f))
    }
  }
}

@Composable
private fun CategoryBlock(
  category: Category,
  backgroundColor: Color,
  foregroundColor: Color,
  image: Painter,
) = with(LocalSharedTransitionScope.current) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .sharedBounds(
        sharedContentState = rememberSharedContentState(category),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(52.dp)
      .background(
        color = backgroundColor,
        shape = TileShape,
      )
  ) {
    Image(
      painter = image,
      contentDescription = null,
      colorFilter = ColorFilter.tint(foregroundColor),
    )
  }
}
