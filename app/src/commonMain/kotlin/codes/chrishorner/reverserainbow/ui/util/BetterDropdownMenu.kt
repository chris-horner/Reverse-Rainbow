package codes.chrishorner.reverserainbow.ui.util

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import codes.chrishorner.reverserainbow.ui.LayoutOrientation
import codes.chrishorner.reverserainbow.ui.LocalLayoutOrientation

/**
 * A copy of Material3's `DropdownMenu`, but with simpler position calculation that doesn't have
 * bugs when attempting to show near the bottom of the screen.
 */
@Composable
fun BetterDropdownMenu(
  expanded: Boolean,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  val orientation = LocalLayoutOrientation.current
  val expandedState = remember { MutableTransitionState(false) }
  expandedState.targetState = expanded

  if (expandedState.currentState || expandedState.targetState) {
    val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
    val density = LocalDensity.current
    val popupPositionProvider =
      remember(density) {
        object : PopupPositionProvider {
          override fun calculatePosition(
            anchorBounds: IntRect,
            windowSize: IntSize,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize
          ): IntOffset {
            val anchorOffset = when (orientation) {
              LayoutOrientation.Portrait -> IntOffset(
                x = popupContentSize.width - anchorBounds.width,
                y = popupContentSize.height,
              )

              LayoutOrientation.Landscape -> IntOffset(
                x = popupContentSize.width - anchorBounds.width,
                y = -anchorBounds.height,
              )
            }

            return anchorBounds.topLeft - anchorOffset
          }
        }
      }

    Popup(
      onDismissRequest = onDismissRequest,
      popupPositionProvider = popupPositionProvider,
      properties = PopupProperties(focusable = true),
    ) {
      DropdownMenuContent(
        expandedState = expandedState,
        transformOriginState = transformOriginState,
        scrollState = rememberScrollState(),
        shape = MenuDefaults.shape,
        containerColor = MenuDefaults.containerColor,
        tonalElevation = MenuDefaults.TonalElevation,
        shadowElevation = MenuDefaults.ShadowElevation,
        modifier = modifier,
        content = content,
      )
    }
  }
}

@Composable
fun DropdownMenuContent(
  modifier: Modifier,
  expandedState: MutableTransitionState<Boolean>,
  transformOriginState: MutableState<TransformOrigin>,
  scrollState: ScrollState,
  shape: Shape,
  containerColor: Color,
  tonalElevation: Dp,
  shadowElevation: Dp,
  content: @Composable ColumnScope.() -> Unit
) {
  val transition = rememberTransition(expandedState, "BetterDropdownMenu")
  val scaleAnimationSpec = spring<Float>(
    dampingRatio = 0.9f,
    stiffness = 1400f,
  )
  val alphaAnimationSpec = spring<Float>(
    dampingRatio = 1f,
    stiffness = 3800f,
  )
  val elevationAnimationSpec = spring<Dp>(
    dampingRatio = 0.9f,
    stiffness = 1400f,
  )

  val scale by transition.animateFloat(transitionSpec = { scaleAnimationSpec }) { expanded ->
    if (expanded) 1f else 0.8f
  }

  val alpha by transition.animateFloat(transitionSpec = { alphaAnimationSpec }) { expanded ->
    if (expanded) 1f else 0f
  }

  val elevation by transition.animateDp(transitionSpec = { elevationAnimationSpec }) { expanded ->
    if (expanded) shadowElevation else 0.dp
  }

  val isInspecting = LocalInspectionMode.current
  Surface(
    modifier = Modifier.graphicsLayer {
      scaleX =
        if (!isInspecting) scale
        else if (expandedState.targetState) 1f else 0.8f
      scaleY =
        if (!isInspecting) scale
        else if (expandedState.targetState) 1f else 0.8f
      this.alpha =
        if (!isInspecting) alpha
        else if (expandedState.targetState) 1f else 0f
      transformOrigin = transformOriginState.value
    },
    shape = shape,
    color = containerColor,
    tonalElevation = tonalElevation,
    shadowElevation = elevation,
  ) {
    Column(
      modifier =
      modifier
        .padding(vertical = 8.dp)
        .width(IntrinsicSize.Max)
        .verticalScroll(scrollState),
      content = content
    )
  }
}