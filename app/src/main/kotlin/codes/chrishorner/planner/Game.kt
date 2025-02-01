package codes.chrishorner.planner

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import codes.chrishorner.planner.data.Card
import codes.chrishorner.planner.data.Category
import codes.chrishorner.planner.data.GameState

@Stable
interface Game {
  val state: GameState
  fun select(card: Card)
  fun submit(category: Category, cards: List<Card>)

  companion object {
    fun from(cards: List<Card>): Game {
      val state = GameState(cardRows = cards.chunked(4))
      return RealGame(state)
    }

    fun from(state: GameState): Game {
      return RealGame(state)
    }
  }
}

private class RealGame(initialState: GameState) : Game {
  private var _state by mutableStateOf(initialState)
  override val state: GameState = _state

  override fun select(card: Card) {
    TODO("Not yet implemented")
  }

  override fun submit(category: Category, cards: List<Card>) {
    TODO("Not yet implemented")
  }
}