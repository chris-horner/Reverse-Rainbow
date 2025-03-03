@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package codes.chrishorner.planner.ui.screens.about

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.BuildConfig
import codes.chrishorner.planner.R
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.ui.util.CappedWidthContainer
import codes.chrishorner.planner.ui.Icons
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.theme.plannerColors

@Composable
fun AboutUi(onBack: () -> Unit) {
  Scaffold(topBar = { TopBar(onBack) }) { paddingValues ->
    CappedWidthContainer {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(paddingValues),
      ) {
        Spacer(modifier = Modifier.size(32.dp))

        Text(
          text = stringResource(R.string.about_app_name),
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.headlineMedium,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
          text = BuildConfig.VERSION_NAME,
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.titleSmall,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.size(32.dp))

        Entry(Category.YELLOW, Icons.Person, stringResource(R.string.about_point1))
        Entry(Category.GREEN, Icons.Warning, stringResource(R.string.about_point2))
        Entry(Category.BLUE, Icons.Block, stringResource(R.string.about_point3))
        Entry(Category.PURPLE, Icons.GitHub, stringResource(R.string.about_point4))
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
          contentDescription = stringResource(R.string.back_description),
        )
      }
    },
    title = { Text(stringResource(R.string.about)) },
  )
}

@Composable
private fun Entry(
  category: Category,
  icon: ImageVector,
  text: String,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 72.dp)
      .padding(16.dp)
  ) {
    Block(category, icon)
    Text(
      text = text,
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
  val backgroundColor = when (category) {
    Category.YELLOW -> MaterialTheme.plannerColors.yellowSurface
    Category.GREEN -> MaterialTheme.plannerColors.greenSurface
    Category.BLUE -> MaterialTheme.plannerColors.blueSurface
    Category.PURPLE -> MaterialTheme.plannerColors.purpleSurface
  }

  val foregroundColor = when (category) {
    Category.YELLOW -> MaterialTheme.plannerColors.onYellowSurface
    Category.GREEN -> MaterialTheme.plannerColors.onGreenSurface
    Category.BLUE -> MaterialTheme.plannerColors.onBlueSurface
    Category.PURPLE -> MaterialTheme.plannerColors.onPurpleSurface
  }

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
        color = backgroundColor,
        shape = RoundedCornerShape(6.dp),
      )
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = foregroundColor,
    )
  }
}