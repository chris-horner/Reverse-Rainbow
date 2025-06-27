@file:OptIn(ExperimentalSerializationApi::class)

package codes.chrishorner.planner.data

import codes.chrishorner.planner.Game
import com.diamondedge.logging.logging
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

/**
 * Hits The New York Times' API, parses their JSON, and returns a list of [Tile] objects that can be
 * used to construct a [Game].
 */
suspend fun fetchTiles(
  clock: Clock = Clock.System,
  timeZone: TimeZone = TimeZone.currentSystemDefault(),
  httpEngine: HttpClientEngine? = null,
): TileFetchResult {
  val today = clock.now().toLocalDateTime(timeZone)
  val year = today.year
  val month = today.monthNumber.toString().padStart(2, '0')
  val day = today.dayOfMonth.toString().padStart(2, '0')
  val url = "$ApiEndpoint$year-$month-$day.json"

  return try {
    HttpClient(httpEngine).get(url).toResult()
  } catch (e: Exception) {
    logging("Reverse Rainbow").e(e) { "Failed to fetch tiles." }
    TileFetchResult.NetworkFailure
  }
}

sealed interface TileFetchResult {
  data class Success(val tiles: ImmutableList<Tile>) : TileFetchResult

  sealed interface Failure : TileFetchResult

  data object NetworkFailure : Failure
  data object HttpFailure : Failure
  data object ParsingFailure : Failure
}

private fun HttpClient(engine: HttpClientEngine?): HttpClient {
  val config: HttpClientConfig<*>.() -> Unit = {
    install(ContentNegotiation) { json() }
  }

  val client = if (engine != null) {
    HttpClient(engine, config)
  } else {
    HttpClient(config)
  }

  return client
}

private suspend fun HttpResponse.toResult(): TileFetchResult {
  if (!status.isSuccess()) {
    logging("Reverse Rainbow").e { "Tile fetching failed with code ${status.value}" }
    return TileFetchResult.HttpFailure
  }

  try {
    val apiResponse = body<ApiResponse>()
    val tiles = apiResponse.categories
      .flatMap { it.cards }
      .sortedBy { it.position }
      .map { it.asTile() }
      .toImmutableList()
    return TileFetchResult.Success(tiles)
  } catch (e: Exception) {
    logging("Reverse Rainbow").e( e) { "Failed to parse tiles from server response." }
    return TileFetchResult.ParsingFailure
  }
}

@Serializable
@JsonIgnoreUnknownKeys
private data class ApiResponse(
  val status: String,
  val categories: List<ApiCategory>,
)

@Serializable
@JsonIgnoreUnknownKeys
private data class ApiCategory(
  val cards: List<ApiCard>
)

// Match the network API. https://publicobject.com/2016/01/20/strict-naming-conventions-are-a-liability/
@Suppress("PropertyName")
@Serializable
@JsonIgnoreUnknownKeys
private data class ApiCard(
  val position: Int,
  val content: String? = null,
  val image_url: String? = null,
  val image_alt_text: String? = null,
)

private fun ApiCard.asTile(): Tile {
  return Tile(
    initialPosition = position,
    content = when {
      image_url != null -> Tile.Content.Image(image_url, image_alt_text)
      content != null -> Tile.Content.Text(content)
      else -> throw SerializationException("Unknown content type.")
    }
  )
}