package codes.chrishorner.reverserainbow.ui.screens.error

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.GameLoader.FailureType
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.resources.Res
import codes.chrishorner.reverserainbow.resources.error_message_http
import codes.chrishorner.reverserainbow.resources.error_message_network
import codes.chrishorner.reverserainbow.resources.error_message_parsing
import codes.chrishorner.reverserainbow.resources.error_retry_button
import codes.chrishorner.reverserainbow.resources.error_title_http
import codes.chrishorner.reverserainbow.resources.error_title_network
import codes.chrishorner.reverserainbow.resources.error_title_parsing
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.backgroundColor
import codes.chrishorner.reverserainbow.ui.theme.foregroundColor
import codes.chrishorner.reverserainbow.ui.util.CappedWidthContainer
import org.jetbrains.compose.resources.stringResource

/**
 * Shows various messages depending on the `failureType`.
 */
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
          FailureType.NETWORK -> stringResource(Res.string.error_title_network)
          FailureType.HTTP -> stringResource(Res.string.error_title_http)
          FailureType.PARSING -> stringResource(Res.string.error_title_parsing)
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
          image = ErrorImages.SadFace1,
        )
        CategoryBlock(
          category = Category.GREEN,
          image = ErrorImages.SadFace2,
        )
        CategoryBlock(
          category = Category.BLUE,
          image = ErrorImages.SadFace3,
        )
        CategoryBlock(
          category = Category.PURPLE,
          image = ErrorImages.SadFace4,
        )
      }

      Spacer(modifier = Modifier.size(24.dp))

      Text(
        text = when (failureType) {
          FailureType.NETWORK -> stringResource(Res.string.error_message_network)
          FailureType.HTTP -> stringResource(Res.string.error_message_http)
          FailureType.PARSING -> stringResource(Res.string.error_message_parsing)
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
        Text(text = stringResource(Res.string.error_retry_button))
      }

      Spacer(modifier = Modifier.weight(5f))
    }
  }
}

@Composable
private fun CategoryBlock(
  category: Category,
  image: ImageVector,
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
        color = category.backgroundColor,
        shape = TileShape,
      )
  ) {
    Image(
      imageVector = image,
      contentDescription = null,
      colorFilter = ColorFilter.tint(category.foregroundColor),
    )
  }
}
