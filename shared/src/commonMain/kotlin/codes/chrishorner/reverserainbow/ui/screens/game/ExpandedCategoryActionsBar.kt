package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.backgroundColor
import codes.chrishorner.reverserainbow.ui.tileSpringSpec

@Composable
fun CategoryAnimationScope.ExpandedCategoryActionsBar(
  expandedCategory: Category,
  onClick: (Category) -> Unit,
  onCategoryClear: (Category) -> Unit,
  onCollapseCategories: () -> Unit,
) {
  when (LocalLayoutOrientation.current) {
    LayoutOrientation.Portrait -> {
      HorizontalExpandedCategoryActionsBar(
        expandedCategory,
        onClick,
        onCategoryClear,
        onCollapseCategories
      )
    }

    LayoutOrientation.Landscape -> {
      VerticalExpandedCategoryActionsBar(
        expandedCategory,
        onClick,
        onCategoryClear,
        onCollapseCategories
      )
    }
  }
}

@Composable
private fun CategoryAnimationScope.HorizontalExpandedCategoryActionsBar(
  expandedCategory: Category,
  onClick: (Category) -> Unit,
  onCategoryClear: (Category) -> Unit,
  onCollapseCategories: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxWidth().height(CategoryActionLayoutSize)) {

    Box(
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .sharedBoundsForExpandedCategory(expandedCategory)
        .fillMaxWidth()
        .height(CategoryActionBarSize)
        .background(expandedCategory.backgroundColor, shape = TileShape)
    )

    Row(
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .fillMaxWidth()
        .height(CategoryActionLayoutSize)
        .padding(vertical = CategoryActionBarPadding)
    ) {
      for (category in Category.entries) {
        if (category == expandedCategory) {
          ExpandedCategoryCancelAndClear(
            category = category,
            onClear = onCategoryClear,
            onCancel = onCollapseCategories,
          )
        } else {
          ExpandedSwapButton(
            expandedCategory = expandedCategory,
            swapCategory = category,
            onClick = { onClick(category) },
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryAnimationScope.VerticalExpandedCategoryActionsBar(
  expandedCategory: Category,
  onClick: (Category) -> Unit,
  onCategoryClear: (Category) -> Unit,
  onCollapseCategories: () -> Unit,
) {
  Box(modifier = Modifier.width(CategoryActionLayoutSize).fillMaxHeight()) {

    Box(
      modifier = Modifier
        .padding(vertical = 8.dp)
        .sharedBoundsForExpandedCategory(expandedCategory)
        .width(CategoryActionBarSize)
        .fillMaxHeight()
        .background(expandedCategory.backgroundColor, shape = TileShape)
    )

    Column(
      verticalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .width(CategoryActionLayoutSize)
        .fillMaxHeight()
        .padding(horizontal = CategoryActionBarPadding)
    ) {
      for (category in Category.entries.reversed()) {
        if (category == expandedCategory) {
          ExpandedCategoryCancelAndClear(
            category = category,
            onClear = onCategoryClear,
            onCancel = onCollapseCategories,
          )
        } else {
          ExpandedSwapButton(
            expandedCategory = expandedCategory,
            swapCategory = category,
            onClick = { onClick(category) },
          )
        }
      }
    }
  }
}

@Composable
context(animationScope: CategoryAnimationScope)
private fun Modifier.sharedBoundsForExpandedCategory(
  category: Category
): Modifier = with(animationScope) {
  return sharedBounds(
    sharedContentState = rememberSharedContentState(category),
    animatedVisibilityScope = this,
    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    enter = EnterTransition.None,
    exit = ExitTransition.None,
    boundsTransform = { _, _ -> tileSpringSpec() }
  )
}