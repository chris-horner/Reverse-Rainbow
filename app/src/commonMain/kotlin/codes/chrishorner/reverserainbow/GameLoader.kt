package codes.chrishorner.reverserainbow

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import codes.chrishorner.reverserainbow.data.TileFetchResult
import codes.chrishorner.reverserainbow.data.fetchTiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Fetches today's Connections game from The New York Times API and loads any necessary resources.
 */
@Stable
class GameLoader(
  private val scope: CoroutineScope,
  initialState: LoaderState = LoaderState.Loading,
  private val tileFetcher: suspend () -> TileFetchResult = ::fetchTiles,
  /**
   * Resources on web are fetched asynchronously, so we give web clients the opportunity to
   * include pre-loading those as part of the game load operation.
   */
  private val resourceLoader: suspend () -> Unit = {},
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

    val fetchTilesJob = async { tileFetcher() }
    val loadResourcesJob = async { resourceLoader() }

    loadResourcesJob.await()
    val tilesResult = fetchTilesJob.await()
    val currentLocalDate = clock.now().toLocalDateTime(timeZoneProvider()).date

    _state.value = when (tilesResult) {
      is TileFetchResult.Success -> LoaderState.Success(currentLocalDate, Game(tilesResult.tiles))
      is TileFetchResult.Failure -> LoaderState.Failure(
        type = when (tilesResult) {
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