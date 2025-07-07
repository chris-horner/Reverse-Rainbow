package codes.chrishorner.reverserainbow

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import codes.chrishorner.reverserainbow.GameLoader.FailureType
import codes.chrishorner.reverserainbow.GameLoader.LoaderState
import codes.chrishorner.reverserainbow.GameLoaderTest.TestData.june14at9am
import codes.chrishorner.reverserainbow.GameLoaderTest.TestData.loadedGameModel
import codes.chrishorner.reverserainbow.GameLoaderTest.TestData.loadedTiles
import codes.chrishorner.reverserainbow.GameLoaderTest.TestData.melbourneTimeZone
import codes.chrishorner.reverserainbow.data.Category
import codes.chrishorner.reverserainbow.data.CategoryAction
import codes.chrishorner.reverserainbow.data.CategoryStatus
import codes.chrishorner.reverserainbow.data.GameModel
import codes.chrishorner.reverserainbow.data.RainbowStatus
import codes.chrishorner.reverserainbow.data.Tile
import codes.chrishorner.reverserainbow.data.Tile.Content
import codes.chrishorner.reverserainbow.data.TileFetchResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameLoaderTest {
  private val resultProvider = Channel<TileFetchResult>()

  @Test
  fun `refresh with successful fetch loads game`() = runTest {
    val loader = GameLoader(
      scope = this,
      tileFetcher = resultProvider::receive,
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
    )

    loader.refresh()
    resultProvider.send(TileFetchResult.Success(loadedTiles))
    advanceUntilIdle()

    val state = loader.state.value as LoaderState.Success
    assertThat(state.date).isEqualTo(LocalDate.parse("2025-06-14"))
    assertThat(state.game.model.value).isEqualTo(loadedGameModel)
  }

  @Test
  fun `refresh with failure loads failure state`() = runTest {
    val loader = GameLoader(
      scope = this,
      tileFetcher = resultProvider::receive,
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
    )

    loader.refresh()
    resultProvider.send(TileFetchResult.NetworkFailure)
    advanceUntilIdle()

    assertThat(loader.state.value).isEqualTo(LoaderState.Failure(type = FailureType.NETWORK))
  }

  @Test
  fun `refresh with currently loaded game updates to loading state`() = runTest {
    val loader = GameLoader(
      scope = backgroundScope,
      tileFetcher = resultProvider::receive,
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
      initialState = LoaderState.Success(
        date = LocalDate.parse("2025-06-14"),
        game = Game(loadedTiles),
      ),
    )

    assertThat(loader.state.value).isInstanceOf<LoaderState.Success>()

    loader.refresh()
    advanceUntilIdle()

    assertThat(loader.state.value).isEqualTo(LoaderState.Loading)
  }

  @Test
  fun `refreshIfNecessary refreshes if current state is failure`() = runTest {
    val loader = GameLoader(
      scope = backgroundScope,
      tileFetcher = resultProvider::receive,
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
      initialState = LoaderState.Failure(type = FailureType.NETWORK),
    )

    assertThat(loader.state.value).isEqualTo(LoaderState.Failure(type = FailureType.NETWORK))

    loader.refreshIfNecessary()
    advanceUntilIdle()

    assertThat(loader.state.value).isEqualTo(LoaderState.Loading)
  }

  @Test
  fun `refreshIfNecessary refreshes if current local date is different`() = runTest {
    val loader = GameLoader(
      scope = backgroundScope,
      tileFetcher = resultProvider::receive,
      // Current date is June 14h.
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
      initialState = LoaderState.Success(
        // Mark current game as loaded from June 13th.
        date = LocalDate.parse("2025-06-13"),
        game = Game(loadedTiles),
      ),
    )

    assertThat(loader.state.value).isInstanceOf<LoaderState.Success>()

    loader.refreshIfNecessary()
    advanceUntilIdle()

    assertThat(loader.state.value).isEqualTo(LoaderState.Loading)
  }

  @Test
  fun `refreshIfNecessary does not refresh if current local date is same`() = runTest {
    val loader = GameLoader(
      scope = backgroundScope,
      tileFetcher = resultProvider::receive,
      // Current date is June 14h.
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
      initialState = LoaderState.Success(
        // Mark current game as loaded from June 14th.
        date = LocalDate.parse("2025-06-14"),
        game = Game(loadedTiles),
      ),
    )

    assertThat(loader.state.value).isInstanceOf<LoaderState.Success>()

    loader.refreshIfNecessary()
    advanceUntilIdle()

    assertThat(loader.state.value).isInstanceOf<LoaderState.Success>()
  }

  @Test
  fun `refreshIfNecessary refreshes if timezone change changes the local date`() = runTest {
    // Start in Vancouver, on one side of the international date line.
    var currentTimeZone = TimeZone.of("America/Vancouver")
    val vancouver3pmJune14 = LocalDateTime.parse("2025-06-14T15:00:00").toInstant(currentTimeZone)

    val loader = GameLoader(
      scope = backgroundScope,
      tileFetcher = resultProvider::receive,
      clock = object : Clock {
        override fun now() = vancouver3pmJune14
      },
      timeZoneProvider = { currentTimeZone },
      initialState = LoaderState.Success(
        // Mark current game as loaded from June 14th.
        date = LocalDate.parse("2025-06-14"),
        game = Game(loadedTiles),
      ),
    )

    assertThat(loader.state.value).isInstanceOf<LoaderState.Success>()

    // Move to Melbourne, which is on the other side of the international date line.
    // 3pm on June 14th in Vancouver is 8am on June 15th in Melbourne.
    currentTimeZone = melbourneTimeZone
    loader.refreshIfNecessary()
    advanceUntilIdle()

    assertThat(loader.state.value).isEqualTo(LoaderState.Loading)
  }

  @Test
  fun `refresh waits for resources to load before updating state`() = runTest {
    val resourceResults = Channel<Unit>()

    val loader = GameLoader(
      scope = this,
      tileFetcher = resultProvider::receive,
      resourceLoader = resourceResults::receive,
      clock = june14at9am,
      timeZoneProvider = { melbourneTimeZone },
    )

    loader.refresh()
    resultProvider.send(TileFetchResult.Success(loadedTiles))
    advanceUntilIdle()

    // Resource loader hasn't completed yet, so we should still be loading.
    assertThat(loader.state.value).isEqualTo(LoaderState.Loading)

    resourceResults.send(Unit)
    advanceUntilIdle()

    // And with resources loaded, we should now be successfully loaded.
    assertThat(loader.state.value).isInstanceOf<LoaderState.Success>()
  }

  private object TestData {
    val june14at9am = object : Clock {
      override fun now() = Instant.parse("2025-06-14T09:00:00Z")
    }

    val melbourneTimeZone = TimeZone.of("Australia/Melbourne")

    val loadedTiles = persistentListOf(
      Tile(content = Content.Text("MOM"), initialPosition = 0),
      Tile(content = Content.Text("QUEEN"), initialPosition = 1),
      Tile(content = Content.Text("RIBBON"), initialPosition = 2),
      Tile(content = Content.Text("BORDER"), initialPosition = 3),
      Tile(content = Content.Text("BLUE"), initialPosition = 4),
      Tile(content = Content.Text("BOSTON"), initialPosition = 5),
      Tile(content = Content.Text("HEART"), initialPosition = 6),
      Tile(content = Content.Text("LEGEND"), initialPosition = 7),
      Tile(content = Content.Text("TOTO"), initialPosition = 8),
      Tile(content = Content.Text("ARROW"), initialPosition = 9),
      Tile(content = Content.Text("ICON"), initialPosition = 10),
      Tile(content = Content.Text("HOOCH"), initialPosition = 11),
      Tile(content = Content.Text("RAT"), initialPosition = 12),
      Tile(content = Content.Text("BULL"), initialPosition = 13),
      Tile(content = Content.Text("ASTRO"), initialPosition = 14),
      Tile(content = Content.Text("DIVA"), initialPosition = 15),
    )

    val loadedGameModel = GameModel(
      tiles = loadedTiles,
      categoryStatuses = persistentMapOf(
        Category.YELLOW to CategoryStatus(
          complete = false,
          allSelected = false,
          bulkSelectable = false,
          action = CategoryAction.DISABLED,
        ),
        Category.GREEN to CategoryStatus(
          complete = false,
          allSelected = false,
          bulkSelectable = false,
          action = CategoryAction.DISABLED,
        ),
        Category.BLUE to CategoryStatus(
          complete = false,
          allSelected = false,
          bulkSelectable = false,
          action = CategoryAction.DISABLED,
        ),
        Category.PURPLE to CategoryStatus(
          complete = false,
          allSelected = false,
          bulkSelectable = false,
          action = CategoryAction.DISABLED,
        ),
      ),
      rainbowStatus = RainbowStatus.DISABLED,
      mostlyComplete = false,
    )
  }
}