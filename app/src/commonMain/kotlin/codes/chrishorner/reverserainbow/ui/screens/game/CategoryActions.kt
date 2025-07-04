package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.data.CategoryStatus
import codes.chrishorner.reverserainbow.data.RainbowStatus
import codes.chrishorner.reverserainbow.ui.Icons
import codes.chrishorner.reverserainbow.ui.JumpEndEasing
import codes.chrishorner.reverserainbow.ui.JumpStartEasing
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.plannerColors
import kotlinx.collections.immutable.ImmutableMap

/**
 * 4 square buttons shown underneath or beside the Connections grid. Provides affordances for
 * assigning, clearing, and swapping tiles to different categories.
 */
@Composable
fun CategoryActions(
  categoryStatuses: ImmutableMap<Category, CategoryStatus>,
  rainbowStatus: RainbowStatus,
  onCategoryClick: (Category) -> Unit,
  modifier: Modifier,
) {
  val boardComplete = rainbowStatus != RainbowStatus.DISABLED

  when (LocalLayoutOrientation.current) {
    LayoutOrientation.Portrait -> {
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth(),
      ) {
        for ((category, status) in categoryStatuses) {
          CategoryAction(category, status, boardComplete, onCategoryClick)
        }
      }
    }

    LayoutOrientation.Landscape -> {
      Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxHeight(),
      ) {
        for ((category, status) in categoryStatuses) {
          CategoryAction(category, status, boardComplete, onCategoryClick)
        }
      }
    }
  }
}

@Composable
private fun CategoryAction(
  category: Category,
  status: CategoryStatus,
  boardComplete: Boolean,
  onClick: (Category) -> Unit,
) = with(LocalSharedTransitionScope.current) {
  val action = status.action
  val colors = getColors(category)
  val alpha by animateFloatAsState(
    targetValue = if (action == CategoryAction.DISABLED) 0.5f else 1f,
    label = "category action alpha",
  )
  val jumpAnimatable = rememberJumpAnimatable(category, boardComplete)

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
      .offset { jumpAnimatable.value }
      .alpha(alpha)
      .size(64.dp)
      .background(
        shape = TileShape,
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

/**
 * Provides an `Animatable` for each category action button to "jump" when the board is completed.
 */
@Composable
private fun rememberJumpAnimatable(
  category: Category,
  boardComplete: Boolean,
): Animatable<IntOffset, AnimationVector2D> {
  val jumpAnimatable = remember { Animatable(IntOffset.Zero, IntOffset.VectorConverter) }
  val density = LocalDensity.current

  var runCelebration by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(boardComplete, density) {
    if (!boardComplete) runCelebration = false

    if (!boardComplete || runCelebration) return@LaunchedEffect

    val delay = when (category) {
      Category.YELLOW -> 0
      Category.GREEN -> 60
      Category.BLUE -> 120
      Category.PURPLE -> 180
    }

    val yPosition = with(density) { (-16).dp.roundToPx() }

    jumpAnimatable.animateTo(
      targetValue = IntOffset(0, yPosition),
      animationSpec = tween(durationMillis = 240, delayMillis = delay, easing = JumpStartEasing)
    )
    jumpAnimatable.animateTo(
      targetValue = IntOffset.Zero,
      animationSpec = tween(durationMillis = 220, easing = JumpEndEasing)
    )

    runCelebration = true
  }

  return jumpAnimatable
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