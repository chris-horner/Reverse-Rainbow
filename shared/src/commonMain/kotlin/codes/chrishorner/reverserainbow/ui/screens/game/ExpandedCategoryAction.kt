package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.resources.Res
import codes.chrishorner.reverserainbow.resources.clear_category
import codes.chrishorner.reverserainbow.resources.collapse_category
import codes.chrishorner.reverserainbow.ui.Icons
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.backgroundColor
import codes.chrishorner.reverserainbow.ui.theme.iconColor
import codes.chrishorner.reverserainbow.ui.tileSpringSpec
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoryAnimationScope.ExpandedSwapButton(
  category: Category,
  onClick: () -> Unit,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .renderInSharedTransitionScopeOverlay()
      .animateExpandedSwapEnterExit(category)
      .size(CategoryActionButtonSize)
      .background(
        shape = TileShape,
        color = category.backgroundColor,
      )
      .clickable(onClick = onClick)
  ) {
    Icon(
      imageVector = Icons.SwapVert,
      contentDescription = null,
      tint = category.iconColor,
    )
  }
}

context(animationScope: CategoryAnimationScope)
private fun Modifier.animateExpandedSwapEnterExit(
  category: Category,
): Modifier = with(animationScope) {
  return when (orientation) {
    LayoutOrientation.Portrait -> {
      animateEnterExit(
        enter = slideInHorizontally(
          animationSpec = tileSpringSpec(),
          initialOffsetX = { getHorizontalExitPositionFor(category) }
        ) + fadeIn(
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        ),
        exit = slideOutHorizontally(
          animationSpec = spring(stiffness = Spring.StiffnessMedium),
          targetOffsetX = { getHorizontalExitPositionFor(category) }
        ) + fadeOut(
          animationSpec = spring(stiffness = Spring.StiffnessMedium),
        ),
      )
    }

    LayoutOrientation.Landscape -> {
      animateEnterExit(
        enter = slideInVertically(
          animationSpec = tileSpringSpec(),
          initialOffsetY = { getVerticalExitPositionFor(category) }
        ) + fadeIn(
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        ),
        exit = slideOutVertically(
          animationSpec = spring(stiffness = Spring.StiffnessMedium),
          targetOffsetY = { getVerticalExitPositionFor(category) }
        ) + fadeOut(
          animationSpec = spring(stiffness = Spring.StiffnessMedium),
        ),
      )
    }
  }
}

/**
 * When expanded buttons show and hide, we want them to "explode" out from the initial category
 * that was expanded. We use the layout information captured by CategoryAnimationScope to determine
 * where this position should be for each category.
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
  val indexOffset = selectedCategoryIndex - thisCategoryIndex

  return indexOffset * (itemWidth + gap)
}

private fun CategoryAnimationScope.getVerticalExitPositionFor(category: Category): Int {
  val lastSelectedCategory = state.lastExpandedCategory ?: return 0
  val containerHeight = state.containerSize.width
  val itemHeight = state.itemSize.width

  val gap = (containerHeight - (itemHeight * Category.entries.size)) / (Category.entries.size + 1)

  val selectedCategoryIndex = Category.entries.indexOf(lastSelectedCategory)
  val thisCategoryIndex = Category.entries.indexOf(category)
  val indexOffset = selectedCategoryIndex - thisCategoryIndex

  return indexOffset * (itemHeight + gap)
}

val CategoryActionClearButtonSize = 48.dp
val CategoryActionClearButtonGap = 16.dp

@Composable
fun CategoryAnimationScope.ExpandedCategoryCancelAndClear(
  category: Category,
  onCancel: () -> Unit,
  onClear: (Category) -> Unit,
) {
  when (LocalLayoutOrientation.current) {
    LayoutOrientation.Portrait -> {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CancelButton(category, onCancel)
        Spacer(modifier = Modifier.height(CategoryActionClearButtonGap))
        ClearButton(category, onClear)
      }
    }

    LayoutOrientation.Landscape -> {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CancelButton(category, onCancel)
        Spacer(modifier = Modifier.width(CategoryActionClearButtonGap))
        ClearButton(category, onClear)
      }
    }
  }
}

@Composable
private fun CategoryAnimationScope.CancelButton(category: Category, onClick: () -> Unit) {
  IconButton(
    onClick = onClick,
    modifier = Modifier.size(CategoryActionButtonSize)
  ) {
    Icon(
      imageVector = Icons.Clear,
      tint = category.iconColor,
      contentDescription = stringResource(Res.string.collapse_category),
      modifier = Modifier
        .renderInSharedTransitionScopeOverlay()
        .sharedBounds(
          sharedContentState = rememberSharedContentState(key = getCategoryActionIconKey(category)),
          animatedVisibilityScope = this,
        ),
    )
  }
}

@Composable
private fun CategoryAnimationScope.ClearButton(category: Category, onClick: (Category) -> Unit) {
  FilledIconButton(
    onClick = { onClick(category) },
    colors = IconButtonDefaults.filledIconButtonColors(
      containerColor = category.backgroundColor,
    ),
    modifier = Modifier
      .size(CategoryActionClearButtonSize)
      .zIndex(-1f)
      .animateClearButtonEnterExit()
  ) {
    Icon(
      imageVector = Icons.Delete,
      tint = category.iconColor,
      contentDescription = stringResource(Res.string.clear_category),
    )
  }
}

@Composable
context(animationScope: CategoryAnimationScope)
private fun Modifier.animateClearButtonEnterExit() = with(animationScope) {
  val gapHeightPx = with(LocalDensity.current) { CategoryActionClearButtonGap.roundToPx() }

  when (orientation) {
    LayoutOrientation.Portrait -> {
      animateEnterExit(
        enter = slideInVertically(
          initialOffsetY = { fullHeight -> -fullHeight - gapHeightPx },
          animationSpec = tileSpringSpec(),
        ),
        exit = slideOutVertically(
          targetOffsetY = { fullHeight -> -fullHeight - gapHeightPx },
          animationSpec = tileSpringSpec(),
        ),
      )
    }

    LayoutOrientation.Landscape -> {
      animateEnterExit(
        enter = slideInHorizontally(
          initialOffsetX = { fullWidth -> -fullWidth - gapHeightPx },
          animationSpec = tileSpringSpec(),
        ),
        exit = slideOutHorizontally(
          targetOffsetX = { fullWidth -> -fullWidth - gapHeightPx },
          animationSpec = tileSpringSpec(),
        ),
      )
    }
  }
}
