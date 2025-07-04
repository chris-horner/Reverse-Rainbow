package codes.chrishorner.reverserainbow

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.reverserainbow.data.TileFetchResult
import codes.chrishorner.reverserainbow.data.fetchTiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Fetches today's Connections game from The New York Times API.
 */
@Stable
class GameLoader(
  private val scope: CoroutineScope,
  initialState: LoaderState = LoaderState.Loading,
  private val tileFetcher: suspend () -> TileFetchResult = ::fetchTiles,
  private val clock: Clock = Clock.System,
  private val timeZoneProvider: () -> TimeZone = { TimeZone.currentSystemDefault() },
) {

  sealed interface LoaderState {
    data object Loading : LoaderState
    data class Failure(val type: FailureType) : LoaderState

    @Stable
    class Success(val date: LocalDate, val game: Game) : LoaderState
  }

  enum class FailureType {
    NETWORK,
    HTTP,
    PARSING,
  }

  private var _state = mutableStateOf(initialState)
  val state: State<LoaderState> = _state

  fun refresh() = scope.launch(start = CoroutineStart.UNDISPATCHED) {
    _state.value = LoaderState.Loading

    val result = tileFetcher()
    val currentLocalDate = clock.now().toLocalDateTime(timeZoneProvider()).date

    _state.value = when (result) {
      is TileFetchResult.Success -> LoaderState.Success(currentLocalDate, Game(result.tiles))
      is TileFetchResult.Failure -> LoaderState.Failure(
        type = when (result) {
          TileFetchResult.HttpFailure -> FailureType.HTTP
          TileFetchResult.NetworkFailure -> FailureType.NETWORK
          TileFetchResult.ParsingFailure -> FailureType.PARSING
        }
      )
    }
  }

  fun refreshIfNecessary() {
    val currentState = _state.value
    val currentLocalDate = clock.now().toLocalDateTime(timeZoneProvider()).date

    if (currentState !is LoaderState.Success || currentState.date != currentLocalDate) {
      refresh()
    }
  }
}