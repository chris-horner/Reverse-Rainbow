package codes.chrishorner.reverserainbow.ui.screens.loading

import androidx.compose.animation.Animatable
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.ui.LocalAnimatedContentScope
import codes.chrishorner.reverserainbow.ui.LocalSharedTransitionScope
import codes.chrishorner.reverserainbow.ui.OvershootEasing
import codes.chrishorner.reverserainbow.ui.SplashScreenFadeMillis
import codes.chrishorner.reverserainbow.ui.theme.PlannerColors
import codes.chrishorner.reverserainbow.ui.theme.TileShape
import codes.chrishorner.reverserainbow.ui.theme.plannerColors
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

/**
 * Begins by placing 4 colored squares in an arrangement that closely matches the app icon shown in
 * the splash screen. Those squares then animate outwards in 4 directions. Once the squares have
 * reached their final distance from the center, `onAnimationDone` will be invoked.
 *
 * If this composable continues to be shown on screen (if loading is taking a long time), then the
 * squares will spin in a circle every few seconds.
 */
@Composable
fun LoadingUi(splashIconSize: DpSize, onAnimationDone: () -> Unit) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize(),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(splashIconSize),
    ) {
      LoadingTiles(onAnimationDone)
    }
  }
}

@Composable
private fun LoadingTiles(
  onAnimationDone: () -> Unit,
) = with(LocalSharedTransitionScope.current) {
  val scope = rememberCoroutineScope()
  val colors = MaterialTheme.plannerColors
  val state = remember(colors) { LoadingAnimationState(scope, colors) }
  LaunchedEffect(state.completedIntro) {
    if (state.completedIntro) onAnimationDone()
  }

  LoadingTile(Category.YELLOW, animationState = state)
  LoadingTile(Category.GREEN, animationState = state)
  LoadingTile(Category.BLUE, animationState = state)
  LoadingTile(Category.PURPLE, animationState = state)
}

@Composable
private fun SharedTransitionScope.LoadingTile(
  category: Category,
  animationState: LoadingAnimationState,
) {
  Box(
    modifier = Modifier
      .offset { animationState.offsets.getValue(category).value.toPxOffset() }
      .graphicsLayer {
        scaleX = animationState.scale
        scaleY = animationState.scale
      }
      .sharedBounds(
        sharedContentState = rememberSharedContentState(category),
        animatedVisibilityScope = LocalAnimatedContentScope.current,
      )
      .size(64.dp)
      // Use `drawWithCache` rather than `background` to avoid recomposing every frame as we animate
      // the tile's color.
      .drawWithCache {
        val outline = TileShape.createOutline(size, layoutDirection, this)
        onDrawBehind {
          drawOutline(outline, color = animationState.colors.getValue(category).value)
        }
      }
  )
}

private const val InitialAnimationMillis = 500
private const val PauseBeforeSpinMillis = 100
private const val SpinCycleMillis = 1_800

@Stable
private class LoadingAnimationState(
  scope: CoroutineScope,
  colors: PlannerColors,
) {
  private val distanceAnimatable = Animatable(52.dp, Dp.VectorConverter)
  private val angleAnimatable = Animatable(0f)
  private val scaleAnimatable = Animatable(1.05f)
  private val colorAnimatables = Category.entries.associateWith {
    Animatable(colors.logoBackgroundTile)
  }

  var completedIntro by mutableStateOf(false)
    private set

  val offsets: ImmutableMap<Category, State<DpOffset>> =
    Category.entries.associateWith { category ->
      val startAngle = when (category) {
        Category.YELLOW -> 225f.toRadians()
        Category.GREEN -> 315f.toRadians()
        Category.BLUE -> 135f.toRadians()
        Category.PURPLE -> 45f.toRadians()
      }

      derivedStateOf {
        DpOffset(
          x = cos(angleAnimatable.value + startAngle) * distanceAnimatable.value,
          y = sin(angleAnimatable.value + startAngle) * distanceAnimatable.value,
        )
      }
    }.toImmutableMap()

  val colors: ImmutableMap<Category, State<Color>> =
    colorAnimatables.mapValues { (_, anim) -> anim.asState() }.toImmutableMap()

  val scale by scaleAnimatable.asState()

  init {
    scope.launch {
      delay(SplashScreenFadeMillis.milliseconds)

      val initialAnimations = buildList {
        add(
          async {
            distanceAnimatable.animateTo(
              targetValue = 80.dp,
              animationSpec = tween(
                durationMillis = InitialAnimationMillis,
                easing = OvershootEasing(6f),
              )
            )
          }
        )

        add(
          async {
            scaleAnimatable.animateTo(
              targetValue = 1f,
              animationSpec = tween(durationMillis = InitialAnimationMillis),
            )
          }
        )

        colorAnimatables.forEach { (category, anim) ->
          add(
            async {
              anim.animateTo(
                targetValue = when (category) {
                  Category.YELLOW -> colors.yellowSurface
                  Category.GREEN -> colors.greenSurface
                  Category.BLUE -> colors.blueSurface
                  Category.PURPLE -> colors.purpleSurface
                },
                animationSpec = tween(durationMillis = InitialAnimationMillis),
              )
            }
          )
        }
      }

      initialAnimations.awaitAll()
      completedIntro = true

      delay(PauseBeforeSpinMillis.milliseconds)

      angleAnimatable.animateTo(
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
          animation = tween(
            durationMillis = SpinCycleMillis,
            easing = FastOutSlowInEasing,
          ),
        ),
      )
    }
  }
}

context(density: Density)
private fun DpOffset.toPxOffset(): IntOffset = with(density) {
  IntOffset(x.roundToPx(), y.roundToPx())
}

private fun Float.toRadians(): Float = (this / 180.0 * PI).toFloat()
