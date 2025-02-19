package codes.chrishorner.planner.ui.screens.loading

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import codes.chrishorner.planner.ui.theme.plannerColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val TileSizeDp = 52.dp
private val explodeTween = tween<Float>(
  durationMillis = 500,
  delayMillis = 200,
  easing = { OvershootInterpolator(6f).getInterpolation(it) },
)
private val collapseTween = tween<Float>(
  durationMillis = 500,
  easing = FastOutLinearInEasing,
)

private data class Tile(
  val color: Color,
  /**
   * We animate a single `angle` value to make the tiles spin. Keep track of an offset for each tile
   * so we can position them around in a circle.
   */
  val angleOffset: Float
)

@Composable
fun LoadingAnimation(onReady: () -> Unit, complete: Boolean = false) = with(LocalDensity.current) {
  val tiles = rememberTiles()
  val completes = snapshotFlow { complete }

  // Start by animating the four squares outwards.
  val offsetFromCenterAnimation = remember { Animatable(42.dp.toPx()) }
  val alphaAnimation = remember { Animatable(1f) }

  LaunchedEffect(Unit) {
    offsetFromCenterAnimation.animateTo(64.dp.toPx(), animationSpec = explodeTween)
    delay(100)
    // Wait until we've been told loading is complete, then animate the squares back in while fading
    // them out at the same time.
    completes.first { it }
    launch {
      offsetFromCenterAnimation.animateTo(0.dp.toPx(), animationSpec = collapseTween)
    }
    launch {
      alphaAnimation.animateTo(0f, animationSpec = collapseTween)
      onReady()
    }
  }

  val offsetFromCenter = offsetFromCenterAnimation.value

  // Then make them spin.
  val infiniteTransition = rememberInfiniteTransition()
  val angle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = 1_400,
        easing = FastOutSlowInEasing,
      ),
      initialStartOffset = StartOffset(explodeTween.delay + explodeTween.durationMillis),
    ),
  )

  val tileSize = Size(TileSizeDp.toPx(), TileSizeDp.toPx())
  val cornerRadius = CornerRadius(x = 6.dp.toPx(), y = 6.dp.toPx())

  Canvas(modifier = Modifier.size(212.dp).alpha(alphaAnimation.value)) {
    tiles.fastForEach { tile ->
      val positionInCircle = Offset(
        x = cos(angle + tile.angleOffset),
        y = sin(angle + tile.angleOffset),
      )
      val positionFromCenter = (positionInCircle * offsetFromCenter) - (tileSize.asOffset() / 2f)
      val position = center + positionFromCenter

      drawRoundRect(
        color = tile.color,
        topLeft = position,
        size = tileSize,
        cornerRadius = cornerRadius,
      )
    }
  }
}

@Composable
private fun rememberTiles(): List<Tile> {
  val colors = MaterialTheme.plannerColors

  return remember(colors) {
    listOf(
      Tile(
        color = colors.yellowSurface,
        angleOffset = Math.toRadians(225.0).toFloat(),
      ),
      Tile(
        color = colors.greenSurface,
        angleOffset = Math.toRadians(315.0).toFloat(),
      ),
      Tile(
        color = colors.blueSurface,
        angleOffset = Math.toRadians(135.0).toFloat(),
      ),
      Tile(
        color = colors.purpleSurface,
        angleOffset = Math.toRadians(45.0).toFloat(),
      )
    )
  }
}

private fun Size.asOffset() = Offset(width, height)