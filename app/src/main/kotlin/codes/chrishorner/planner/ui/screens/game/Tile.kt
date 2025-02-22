package codes.chrishorner.planner.ui.screens.game

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.ui.AutoSizeText
import codes.chrishorner.planner.ui.LocalSharedTransitionScope
import codes.chrishorner.planner.ui.theme.plannerColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Tile(card: Card, onClick: () -> Unit, modifier: Modifier) {
  val tileColors = getColors(card)
  val backgroundColor by animateColorAsState(tileColors.background, animationSpec = spring(stiffness = Spring.StiffnessHigh))
  val textColor by animateColorAsState(tileColors.text, animationSpec = spring(stiffness = Spring.StiffnessHigh))

  with(LocalSharedTransitionScope.current) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = modifier
        .animateBounds(this)
        .background(
          color = backgroundColor,
          shape = RoundedCornerShape(6.dp)
        )
        .clip(RoundedCornerShape(6.dp))
        .clickable(onClick = onClick)
        .padding(8.dp)
        .zIndex(
          4f - card.currentPosition
        ) // Makes sure cards animating to the top render over others.
    ) {
      AutoSizeText(
        // TODO: Render different types of content.
        text = (card.content as Card.Content.Text).content,
        maxLines = 2,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
        ),
        color = textColor,
      )
    }
  }
}

private data class TileColors(
  val background: Color,
  val text: Color,
)

@Composable
private fun getColors(card: Card): TileColors {
  val primaryColor: Color
  val secondaryColor: Color

  when (card.category) {
    Category.YELLOW -> {
      primaryColor = MaterialTheme.plannerColors.yellowSurface
      secondaryColor = MaterialTheme.plannerColors.onYellowSurface
    }
    Category.GREEN -> {
      primaryColor = MaterialTheme.plannerColors.greenSurface
      secondaryColor = MaterialTheme.plannerColors.onGreenSurface
    }
    Category.BLUE -> {
      primaryColor = MaterialTheme.plannerColors.blueSurface
      secondaryColor = MaterialTheme.plannerColors.onBlueSurface
    }
    Category.PURPLE -> {
      primaryColor = MaterialTheme.plannerColors.purpleSurface
      secondaryColor = MaterialTheme.plannerColors.onPurpleSurface
    }
    null -> {
      primaryColor = MaterialTheme.colorScheme.surfaceContainer
      secondaryColor = MaterialTheme.colorScheme.onSurface
    }
  }

  val backgroundColor = if (card.selected) secondaryColor else primaryColor
  val textColor = if (card.selected) primaryColor else secondaryColor

  return TileColors(backgroundColor, textColor)
}