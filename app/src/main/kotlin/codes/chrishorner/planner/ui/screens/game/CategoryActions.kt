package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
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
import codes.chrishorner.planner.data.CategoryStatus
import codes.chrishorner.planner.ui.Shuffle
import codes.chrishorner.planner.ui.theme.plannerColors

@Composable
fun CategoryActions(
  categoryStatuses: Map<Category, CategoryStatus>,
  onCategoryClick: (Category) -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier.fillMaxWidth(),
  ) {
    for ((category, status) in categoryStatuses) {
      val colors = getColors(category)
      val alpha by animateFloatAsState(if (status == CategoryStatus.DISABLED) 0.5f else 1f)

      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(64.dp)
          .alpha(alpha)
          .background(
            shape = RoundedCornerShape(8.dp),
            color = colors.background,
          )
          .clip(RoundedCornerShape(8.dp))
          .clickable(
            enabled = status != CategoryStatus.DISABLED,
            onClick = { onCategoryClick(category) },
          )
      ) {

        if (status == CategoryStatus.CLEARABLE) {
          Icon(
            imageVector = Icons.Rounded.Clear,
            contentDescription = null,
            tint = colors.icon,
          )
        } else if (status == CategoryStatus.SWAPPABLE) {
          Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = null,
            tint = colors.icon,
          )
        }
      }
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