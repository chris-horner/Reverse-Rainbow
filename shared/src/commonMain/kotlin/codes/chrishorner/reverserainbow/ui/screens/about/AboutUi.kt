package codes.chrishorner.reverserainbow.ui.screens.about

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.BuildKonfig
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.resources.Res
import codes.chrishorner.reverserainbow.resources.about
import codes.chrishorner.reverserainbow.resources.about_app_name
import codes.chrishorner.reverserainbow.resources.about_point1
import codes.chrishorner.reverserainbow.resources.about_point1_hyperlink
import codes.chrishorner.reverserainbow.resources.about_point2
import codes.chrishorner.reverserainbow.resources.about_point2_hyperlink
import codes.chrishorner.reverserainbow.resources.about_point3
import codes.chrishorner.reverserainbow.resources.about_point3_hyperlink
import codes.chrishorner.reverserainbow.resources.about_point4
import codes.chrishorner.reverserainbow.resources.about_point4_hyperlink
import codes.chrishorner.reverserainbow.resources.back_description
import codes.chrishorner.reverserainbow.ui.Icons
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.backgroundColor
import codes.chrishorner.reverserainbow.ui.theme.foregroundColor
import codes.chrishorner.reverserainbow.ui.util.CappedWidthContainer
import codes.chrishorner.reverserainbow.ui.util.PreviewLandscapeSmall
import codes.chrishorner.reverserainbow.ui.util.PreviewLightDarkPortraitSmall
import codes.chrishorner.reverserainbow.ui.util.PreviewUi
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutUi(
  onBack: () -> Unit,
  /**
   * Configurable so snapshot tests don't invalidate when changing versions.
   */
  versionName: String = BuildKonfig.versionName,
) {
  Scaffold(topBar = { TopBar(onBack) }) { paddingValues ->
    CappedWidthContainer {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(paddingValues),
      ) {
        Spacer(modifier = Modifier.size(32.dp))

        Text(
          text = stringResource(Res.string.about_app_name),
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.headlineMedium,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
          text = versionName,
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.titleSmall,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.size(32.dp))

        Entry(
          category = Category.YELLOW,
          icon = Icons.Construction,
          text = stringResource(Res.string.about_point1),
          hyperlinkText = stringResource(Res.string.about_point1_hyperlink),
          url = "https://chrishorner.codes",
        )
        Entry(
          category = Category.GREEN,
          icon = Icons.Warning,
          text = stringResource(Res.string.about_point2),
          hyperlinkText = stringResource(Res.string.about_point2_hyperlink),
          url = "https://www.nytimes.com",
        )
        Entry(
          category = Category.BLUE,
          icon = Icons.EditNote,
          text = stringResource(Res.string.about_point3),
          hyperlinkText = stringResource(Res.string.about_point3_hyperlink),
          url = "https://www.nytimes.com/games/connections",
        )
        Entry(
          category = Category.PURPLE,
          icon = Icons.GitHub,
          text = stringResource(Res.string.about_point4),
          hyperlinkText = stringResource(Res.string.about_point4_hyperlink),
          url = "https://github.com/chris-horner/Reverse-Rainbow",
        )

        PlayStoreMessage()
      }
    }
  }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
  TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      titleContentColor = MaterialTheme.colorScheme.onBackground,
      navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
    ),
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          Icons.ArrowBack,
          contentDescription = stringResource(Res.string.back_description),
        )
      }
    },
    title = { Text(stringResource(Res.string.about)) },
  )
}

@Composable
private fun Entry(
  category: Category,
  icon: ImageVector,
  text: String,
  hyperlinkText: String,
  url: String,
  modifier: Modifier = Modifier,
) {
  val uriHandler = LocalUriHandler.current

  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clickable { uriHandler.openUri(url) }
      .fillMaxWidth()
      .heightIn(min = 72.dp)
      .padding(16.dp)
  ) {
    Block(category, icon)
    Text(
      text = text.styleWithHyperlink(hyperlinkText),
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun Block(
  category: Category,
  icon: ImageVector,
) = with(LocalSharedTransitionScope.current) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .sharedBounds(
        sharedContentState = rememberSharedContentState(category),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
        boundsTransform = { _, _ ->
          spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
          )
        }
      )
      .size(48.dp)
      .background(
        color = category.backgroundColor,
        shape = TileShape,
      )
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = category.foregroundColor,
    )
  }
}

@Composable
private fun String.styleWithHyperlink(hyperlinkText: String): AnnotatedString {
  val start = this.indexOf(hyperlinkText)
  val end = start + hyperlinkText.length

  require(start >= 0) {
    "Hyperlink text: $hyperlinkText - not found in $this"
  }

  return buildAnnotatedString {
    append(this@styleWithHyperlink)
    addStyle(
      style = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
      ),
      start = start,
      end = end,
    )
  }
}

@Composable
expect fun PlayStoreMessage()

@PreviewLightDarkPortraitSmall
@Composable
internal fun AboutUiPreview() = PreviewUi {
  AboutUi(versionName = "1.0.0", onBack = {})
}

@PreviewLandscapeSmall
@Composable
internal fun AboutUiLandscapePreview() = PreviewUi {
  AboutUi(versionName = "1.0.0", onBack = {})
}