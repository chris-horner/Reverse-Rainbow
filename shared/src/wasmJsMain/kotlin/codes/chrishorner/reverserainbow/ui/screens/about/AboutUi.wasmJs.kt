package codes.chrishorner.reverserainbow.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.resources.Res
import codes.chrishorner.reverserainbow.resources.about_android_message
import codes.chrishorner.reverserainbow.resources.about_play_badge_description
import codes.chrishorner.reverserainbow.resources.play_badge
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun PlayStoreMessage() {
  val uriHandler = LocalUriHandler.current

  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.padding(16.dp)
  ) {

    Text(
      text = stringResource(Res.string.about_android_message),
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.bodyLarge,
    )

    Image(
      painter = painterResource(Res.drawable.play_badge),
      contentDescription = stringResource(Res.string.about_play_badge_description),
      modifier = Modifier
        .clickable {
          uriHandler.openUri(
            "https://play.google.com/store/apps/details?id=codes.chrishorner.reverserainbow"
          )
        }
    )
  }
}