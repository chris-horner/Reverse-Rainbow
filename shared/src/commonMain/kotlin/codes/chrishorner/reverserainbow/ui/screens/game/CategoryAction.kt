package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.ui.Icons
import codes.chrishorner.reverserainbow.ui.JumpEndEasing
import codes.chrishorner.reverserainbow.ui.JumpStartEasing
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.backgroundColor
import codes.chrishorner.reverserainbow.ui.theme.foregroundColor
import codes.chrishorner.reverserainbow.ui.tileSpringSpec

val CategoryActionButtonSize = 64.dp

/**
 * Visual representation of what `CategoryAction`s are currently available for a given `Category`.
 *
 * Shown inside `CategoryActionsBar` either below or beside of the main game grid.
 */
@Composable
fun CategoryAnimationScope.CategoryAction(
  category: Category,
  action: CategoryAction,
  onClick: (Category) -> Unit,
  boardComplete: Boolean,
  modifier: Modifier = Modifier,
) {
  val alpha by animateFloatAsState(
    targetValue = if (action == CategoryAction.DISABLED) 0.5f else 1f,
    label = "category action alpha",
  )

  val jumpAnimatable = rememberJumpAnimatable(category, boardComplete)

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .recordCategoryActionPosition()
      .animateCategoryEnterExit(category)
      // This first sharedBounds is within CategoryAnimationScope, allowing this action button to
      // visually transform when it's expanded.
      .sharedBounds(
        sharedContentState = rememberSharedContentState(category),
        animatedVisibilityScope = this@CategoryAction,
        boundsTransform = { _, _ -> tileSpringSpec() }
      )
      // This second sharedBounds is for the global navigation scope, allowing this action button to
      // be a shared element when navigating between Loading, About, and Error screens.
      .then(
        with(LocalSharedTransitionScope.current) {
          Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(category),
            animatedVisibilityScope = LocalAnimatedContentScope.current,
            boundsTransform = { _, _ -> tileSpringSpec() }
          )
        }
      )
      .offset { jumpAnimatable.value }
      .alpha(alpha)
      .size(CategoryActionButtonSize)
      .background(
        shape = TileShape,
        color = category.backgroundColor,
      )
      .clickable(
        enabled = action != CategoryAction.DISABLED,
        onClick = { onClick(category) },
      )
  ) {
    val iconVector = when (action) {
      CategoryAction.CLEAR -> Icons.Delete
      CategoryAction.SWAP_SELECTED -> Icons.SwapVert
      CategoryAction.FINISH -> Icons.Check
      CategoryAction.EXPAND -> Icons.MoreHoriz
      else -> null
    }

    if (iconVector != null) {
      Icon(
        imageVector = iconVector,
        contentDescription = null,
        tint = category.foregroundColor,
        modifier = Modifier
          .sharedBounds(
            sharedContentState = rememberSharedContentState(
              key = getCategoryActionIconKey(category),
              config = object : SharedTransitionScope.SharedContentConfig {
                override val SharedTransitionScope.SharedContentState.isEnabled: Boolean
                  get() = action == CategoryAction.EXPAND
              },
            ),
            animatedVisibilityScope = this@CategoryAction,
          ),
      )
    }
  }
}

fun getCategoryActionIconKey(category: Category): String = when (category) {
  Category.YELLOW -> "YellowActionIcon"
  Category.GREEN -> "GreenActionIcon"
  Category.BLUE -> "BlueActionIcon"
  Category.PURPLE -> "PurpleActionIcon"
}

context(animationScope: CategoryAnimationScope)
private fun Modifier.animateCategoryEnterExit(category: Category): Modifier = with(animationScope) {
  return when (orientation) {
    LayoutOrientation.Portrait -> {
      animateEnterExit(
        enter = slideInHorizontally(
          animationSpec = tileSpringSpec(),
          initialOffsetX = { getHorizontalExitPositionFor(category) },
        ),
        exit = slideOutHorizontally(
          animationSpec = tileSpringSpec(),
          targetOffsetX = { getHorizontalExitPositionFor(category) },
        ),
      )
    }

    LayoutOrientation.Landscape -> {
      animateEnterExit(
        enter = slideInVertically(
          animationSpec = tileSpringSpec(),
          initialOffsetY = { getVerticalExitPositionFor(category) },
        ),
        exit = slideOutVertically(
          animationSpec = tileSpringSpec(),
          targetOffsetY = { getVerticalExitPositionFor(category) },
        ),
      )
    }
  }
}

/**
 * When a category is expanded, other categories are animated offscreen. We use previously
 * recorded layout information to determine an aesthetically pleasing exit position to create
 * a nice "explosion" effect.
 *
 * It's way easier to keep the horizontal and vertical variants separate than to introduce a
 * "primary axis" abstraction.
 */
private fun CategoryAnimationScope.getHorizontalExitPositionFor(category: Category): Int {
  val lastSelectedCategory = state.lastExpandedCategory ?: return 0
  val containerWidth = state.containerSize.width
  val itemWidth = state.itemSize.width

  val gap = (containerWidth - (itemWidth * Category.entries.size)) / (Category.entries.size + 1)

  val selectedCategoryIndex = Category.entries.indexOf(lastSelectedCategory)
  val thisCategoryIndex = Category.entries.indexOf(category)

  val indexOffset = thisCategoryIndex - selectedCategoryIndex
  val containerEdge = if (indexOffset > 0) containerWidth else 0

  return containerEdge + indexOffset * (containerWidth + gap)
}

private fun CategoryAnimationScope.getVerticalExitPositionFor(category: Category): Int {
  val lastSelectedCategory = state.lastExpandedCategory ?: return 0
  val containerHeight = state.containerSize.height
  val itemHeight = state.itemSize.height

  val gap = (containerHeight - (itemHeight * Category.entries.size)) / (Category.entries.size + 1)

  val selectedCategoryIndex = Category.entries.reversed().indexOf(lastSelectedCategory)
  val thisCategoryIndex = Category.entries.reversed().indexOf(category)

  val indexOffset = thisCategoryIndex - selectedCategoryIndex
  val containerEdge = if (indexOffset > 0) containerHeight else 0

  return containerEdge + indexOffset * (containerHeight + gap)
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
  val orientation = LocalLayoutOrientation.current

  // TODO: Fix this running when changing expanded category.
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

    val jumpDistance = with(density) { 16.dp.roundToPx() }
    val jumpTarget = when (orientation) {
      LayoutOrientation.Portrait -> IntOffset(0, -jumpDistance)
      LayoutOrientation.Landscape -> IntOffset(jumpDistance, 0)
    }

    jumpAnimatable.animateTo(
      targetValue = jumpTarget,
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