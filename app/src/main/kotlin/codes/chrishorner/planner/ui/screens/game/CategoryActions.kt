package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.CategoryAction
import codes.chrishorner.planner.data.CategoryStatus
import codes.chrishorner.planner.ui.Icons
import codes.chrishorner.planner.ui.LayoutOrientation
import codes.chrishorner.planner.ui.LocalAnimatedContentScope
import codes.chrishorner.planner.ui.LocalLayoutOrientation
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.theme.plannerColors
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun CategoryActions(
  categoryStatuses: ImmutableMap<Category, CategoryStatus>,
  onCategoryClick: (Category) -> Unit,
) {
  when (LocalLayoutOrientation.current) {
    LayoutOrientation.Portrait -> {
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth(),
      ) {
        for ((category, status) in categoryStatuses) {
          CategoryAction(category, status, onCategoryClick)
        }
      }
    }

    LayoutOrientation.Landscape -> {
      Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxHeight(),
      ) {
        for ((category, status) in categoryStatuses) {
          CategoryAction(category, status, onCategoryClick)
        }
      }
    }
  }
}

@Composable
private fun CategoryAction(
  category: Category,
  status: CategoryStatus,
  onClick: (Category) -> Unit,
) = with(LocalSharedTransitionScope.current) {
  val action = status.action
  val colors = getColors(category)
  val alpha by animateFloatAsState(
    targetValue = if (action == CategoryAction.DISABLED) 0.5f else 1f,
    label = "category action alpha",
  )

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
      .alpha(alpha)
      .size(64.dp)
      .background(
        shape = RoundedCornerShape(8.dp),
        color = colors.background,
      )
      .clip(RoundedCornerShape(8.dp))
      .clickable(
        enabled = action != CategoryAction.DISABLED,
        onClick = { onClick(category) },
      )
  ) {

    if (action == CategoryAction.CLEAR) {
      Icon(
        imageVector = Icons.Clear,
        contentDescription = null,
        tint = colors.icon,
      )
    } else if (action == CategoryAction.SWAP) {
      Icon(
        imageVector = Icons.Shuffle,
        contentDescription = null,
        tint = colors.icon,
      )
    }
  }
}

private data class CategoryActionColors(
  val background: Color,
  val icon: Color,
)

@Composable
private fun getColors(category: Category): CategoryActionColors {
  val backgroundColor: Color
  val iconColor: Color

  when (category) {
    Category.YELLOW -> {
      backgroundColor = MaterialTheme.plannerColors.yellowSurface
      iconColor = MaterialTheme.plannerColors.onYellowSurface
    }

    Category.GREEN -> {
      backgroundColor = MaterialTheme.plannerColors.greenSurface
      iconColor = MaterialTheme.plannerColors.onGreenSurface
    }

    Category.BLUE -> {
      backgroundColor = MaterialTheme.plannerColors.blueSurface
      iconColor = MaterialTheme.plannerColors.onBlueSurface
    }

    Category.PURPLE -> {
      backgroundColor = MaterialTheme.plannerColors.purpleSurface
      iconColor = MaterialTheme.plannerColors.onPurpleSurface
    }
  }

  return CategoryActionColors(backgroundColor, iconColor)
}