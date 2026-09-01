package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.data.LocalPersistence
import codes.chrishorner.reverserainbow.resources.Res
import codes.chrishorner.reverserainbow.resources.welcome_dismiss_button
import codes.chrishorner.reverserainbow.resources.welcome_message
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.getGridEnterTransitionFor
import codes.chrishorner.reverserainbow.ui.util.PreviewUi
import com.adamglin.composecontinuousroundedcornershape.ContinuousRoundedCornerShape
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeMessage(modifier: Modifier = Modifier) = with(LocalAnimatedContentScope.current) {
  val persistence = LocalPersistence.current
  val dismissed by persistence.hasDismissedWelcomeMessage
  val scope = rememberCoroutineScope()

  AnimatedVisibility(
    visible = !dismissed,
    enter = EnterTransition.None,
    exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessHigh)) +
      scaleOut(targetScale = 0.92f, animationSpec = spring(stiffness = Spring.StiffnessHigh)),
    modifier = modifier
      .then(
        if (LocalLayoutOrientation.current == LayoutOrientation.Portrait) {
          Modifier.animateEnterExit(
            // The welcome card is rendered above the top row of the connections grid, so we give it
            enter = getGridEnterTransitionFor(row = -1),
            exit = fadeOut(),
          )
        } else {
          Modifier
        }
      )
  ) {
    ElevatedCard(
      colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
      ),
      shape = ContinuousRoundedCornerShape(12.dp),
    ) {
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp),
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Image(
            imageVector = rememberLogo(),
            contentDescription = null,
            modifier = Modifier
              .align(Alignment.Top)
              .size(72.dp),
          )

          Text(
            text = stringResource(Res.string.welcome_message),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterVertically),
          )
        }

        OutlinedButton(
          onClick = {
            scope.launch { persistence.dismissWelcomeMessage() }
          },
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
          ),
        ) {
          Text(stringResource(Res.string.welcome_dismiss_button))
        }
      }
    }
  }
}

@Composable
@PreviewLightDark
private fun WelcomeMessagePreview() = PreviewUi(dismissedWelcomeMessage = false) {
  Box(modifier = Modifier.padding(16.dp)) {
    WelcomeMessage()
  }
}