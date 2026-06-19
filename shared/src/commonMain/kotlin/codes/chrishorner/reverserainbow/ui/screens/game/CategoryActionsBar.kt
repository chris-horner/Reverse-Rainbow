package codes.chrishorner.reverserainbow.ui.screens.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation
import codes.chrishorner.reverserainbow.ui.util.PreviewUi
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

val CategoryActionBarPadding = 8.dp
val CategoryActionBarSize = CategoryActionButtonSize + (CategoryActionBarPadding * 2)
val CategoryActionLayoutSize = CategoryActionBarSize +
  CategoryActionClearButtonGap +
  CategoryActionClearButtonSize

/**
 * 4 square buttons shown underneath or beside the Connections grid. Provides affordances for
 * assigning, clearing, and swapping tiles to different categories.
 */
@Composable
fun CategoryActionsBar(
  categoryActions: ImmutableMap<Category, CategoryAction>,
  expandedCategory: Category? = null,
  boardComplete: Boolean,
  onCategoryClick: (Category) -> Unit = {},
  onCategoryClear: (Category) -> Unit = {},
  onCollapseCategories: () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  val animationState = remember { CategoryAnimationScope.State() }
  animationState.recordExpandedCategory(expandedCategory)

  SharedTransitionLayout(modifier = modifier) {
    AnimatedContent(
      targetState = expandedCategory,
      transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
    ) { currentlyExpandedCategory ->

      val animationScope = CategoryAnimationScope(
        state = animationState,
        orientation = LocalLayoutOrientation.current,
        sharedTransitionScope = this@SharedTransitionLayout,
        animatedVisibilityScope = this,
      )

      with(animationScope) {
        if (currentlyExpandedCategory == null) {

          CollapsedCategoryActions(
            categoryActions = categoryActions,
            boardComplete = boardComplete,
            onCategoryClick = onCategoryClick,
          )
        } else {

          ExpandedCategoryActionsBar(
            expandedCategory = currentlyExpandedCategory,
            onClick = onCategoryClick,
            onCollapseCategories = onCollapseCategories,
            onCategoryClear = onCategoryClear,
          )
        }
      }
    }
  }
}

/**
 * The category actions bar has some complex animations they rely on the positioning and sizing of
 * elements in both the expanded and collapsed states.
 *
 * This custom scope takes care of holding onto that layout information for later use, as well as
 * shuttling both the `SharedTransitionScope` and `AnimatedVisibilityScope` to the various shared
 * element transitions as we move between expanded and collapsed states.
 */
class CategoryAnimationScope(
  val state: State,
  val orientation: LayoutOrientation,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
) : SharedTransitionScope by sharedTransitionScope,
  AnimatedVisibilityScope by animatedVisibilityScope {

  class State {
    var itemSize: IntSize = IntSize.Zero
    var containerSize: IntSize = IntSize.Zero
    var lastExpandedCategory: Category? = null

    fun recordExpandedCategory(expandedCategory: Category?) {
      if (expandedCategory != null) {
        lastExpandedCategory = expandedCategory
      }
    }
  }

  fun Modifier.recordCategoryActionPosition(): Modifier {
    return onGloballyPositioned { coordinates ->
      if (coordinates.size != IntSize.Zero) {
        state.itemSize = coordinates.size
      }

      val parentCoordinates = coordinates.parentLayoutCoordinates

      if (parentCoordinates != null && parentCoordinates.size != IntSize.Zero) {
        state.containerSize = parentCoordinates.size
      }
    }
  }
}

@Composable
private fun CategoryAnimationScope.CollapsedCategoryActions(
  categoryActions: ImmutableMap<Category, CategoryAction>,
  boardComplete: Boolean,
  onCategoryClick: (Category) -> Unit,
) {
  when (LocalLayoutOrientation.current) {
    LayoutOrientation.Portrait -> {
      HorizontalCollapsedCategoryActions(categoryActions, boardComplete, onCategoryClick)
    }

    LayoutOrientation.Landscape -> {
      VerticalCollapsedCategoryActions(categoryActions, boardComplete, onCategoryClick)
    }
  }
}

@Composable
private fun CategoryAnimationScope.HorizontalCollapsedCategoryActions(
  categoryActions: ImmutableMap<Category, CategoryAction>,
  boardComplete: Boolean,
  onCategoryClick: (Category) -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier
      .fillMaxWidth()
      .height(CategoryActionLayoutSize)
      .padding(vertical = CategoryActionBarPadding)
  ) {
    for (category in Category.entries) {
      CategoryAction(
        category = category,
        action = categoryActions.getValue(category),
        onClick = onCategoryClick,
        boardComplete = boardComplete,
      )
    }
  }
}

@Composable
private fun CategoryAnimationScope.VerticalCollapsedCategoryActions(
  categoryActions: ImmutableMap<Category, CategoryAction>,
  boardComplete: Boolean,
  onCategoryClick: (Category) -> Unit,
) {
  Column(
    verticalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier
      .fillMaxHeight()
      .width(CategoryActionLayoutSize)
      .padding(horizontal = CategoryActionBarPadding)
  ) {
    for (category in Category.entries.reversed()) {
      CategoryAction(
        category = category,
        action = categoryActions.getValue(category),
        onClick = onCategoryClick,
        boardComplete = boardComplete,
      )
    }
  }
}

@PreviewLightDark
@Composable
private fun CategoryActionsBarPreview() = PreviewUi(width = 360.dp) {
  CategoryActionsBar(
    categoryActions = persistentMapOf(
      Category.YELLOW to CategoryAction.ASSIGN,
      Category.GREEN to CategoryAction.DISABLED,
      Category.BLUE to CategoryAction.SWAP_SELECTED,
      Category.PURPLE to CategoryAction.CLEAR,
    ),
    expandedCategory = null,
    boardComplete = false,
  )
}

@Preview
@Composable
private fun VerticalCategoryActionsBarPreview() = PreviewUi(
  height = 360.dp,
  orientation = LayoutOrientation.Landscape,
) {
  CategoryActionsBar(
    categoryActions = persistentMapOf(
      Category.YELLOW to CategoryAction.ASSIGN,
      Category.GREEN to CategoryAction.DISABLED,
      Category.BLUE to CategoryAction.SWAP_SELECTED,
      Category.PURPLE to CategoryAction.CLEAR,
    ),
    expandedCategory = null,
    boardComplete = false,
  )
}