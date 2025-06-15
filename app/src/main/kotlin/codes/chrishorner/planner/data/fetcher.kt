package codes.chrishorner.planner.data

import com.diamondedge.logging.logging
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.decodeFromStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlin.coroutines.CoroutineContext

suspend fun fetchTiles(
  clock: Clock = Clock.System,
  timeZone: TimeZone = TimeZone.currentSystemDefault(),
  networkCall: (url: String) -> Response = ::realNetworkCall,
  context: CoroutineContext = Dispatchers.IO,
): TileFetchResult {
  val today = clock.now().toLocalDateTime(timeZone)
  val year = today.year
  val month = today.monthNumber.toString().padStart(2, '0')
  val day = today.dayOfMonth.toString().padStart(2, '0')
  val url = "https://www.nytimes.com/svc/connections/v2/$year-$month-$day.json"

  return withContext(context) {
    try {
      val response = networkCall(url)
      response.toResult()
    } catch (_: Exception) {
      TileFetchResult.NetworkFailure
    }
  }
}

sealed interface TileFetchResult {
  data class Success(val tiles: ImmutableList<Tile>) : TileFetchResult

  sealed interface Failure : TileFetchResult

  data object NetworkFailure : Failure
  data object HttpFailure : Failure
  data object ParsingFailure : Failure
}

private fun realNetworkCall(url: String): Response {
  val request = Request.Builder().url(url).build()
  return OkHttpClient().newCall(request).execute()
}

private fun Response.toResult(): TileFetchResult {
  if (!isSuccessful) {
    logging("Planner").e { "Tile fetching failed with code $code" }
    return TileFetchResult.HttpFailure
  }

  return use {
    val tiles = try {
      val stream = body!!.byteStream()
      val apiResponse = Json.decodeFromStream<ApiResponse>(stream)
      apiResponse.categories
        .flatMap { it.cards }
        .sortedBy { it.position }
        .map { it.asTile() }
        .toImmutableList()
    } catch (e: Exception) {
      logging("Planner").e(err = e) { "Failed to parse tiles from server response." }
      return TileFetchResult.ParsingFailure
    }

    TileFetchResult.Success(tiles)
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

@Suppress("PropertyName") // Match the network API. https://publicobject.com/2016/01/20/strict-naming-conventions-are-a-liability/
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