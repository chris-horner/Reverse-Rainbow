@file:OptIn(ExperimentalSerializationApi::class)

package codes.chrishorner.planner.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.decodeFromStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.LocalDate

suspend fun fetchCards(): CardFetchResult {
  val today = LocalDate.now()
  val year = today.year
  val month = today.monthValue.toString().padStart(2, '0')
  val day = today.dayOfMonth.toString().padStart(2, '0')

  val client = OkHttpClient()
  val request = Request.Builder()
    .url("https://www.nytimes.com/svc/connections/v2/$year-$month-$day.json")
    .build()

  return withContext(Dispatchers.IO) {
    try {
      client.newCall(request).execute().use { response -> response.toResult() }
    } catch (_: Exception) {
      CardFetchResult.NetworkFailure
    }
  }
}

sealed interface CardFetchResult {
  data class Success(val cards: List<Card>) : CardFetchResult

  sealed interface Failure : CardFetchResult

  data object NetworkFailure : Failure
  data object HttpFailure : Failure
  data object ParsingFailure : Failure
}

private fun Response.toResult(): CardFetchResult = use {
  if (!isSuccessful) {
    Log.e("Planner", "Card fetching failed with code $code")
    return CardFetchResult.HttpFailure
  }

  val cards = try {
    val stream = body!!.byteStream()
    val apiResponse = Json.decodeFromStream<ApiResponse>(stream)
    apiResponse.categories
      .flatMap { it.cards }
      .sortedBy { it.position }
      .map { it.asCard() }
  } catch (e: Exception) {
    Log.e("Planner", "Failed to parse cards from server response.", e)
    return CardFetchResult.ParsingFailure
  }

  CardFetchResult.Success(cards)
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

@Serializable
@JsonIgnoreUnknownKeys
private data class ApiCard(
  val position: Int,
  val content: String? = null,
  val image_url: String? = null,
  val image_alt_text: String? = null,
)

private fun ApiCard.asCard(): Card {
  return Card(
    content = when {
      image_url != null -> Card.Content.Image(image_url, image_alt_text)
      content != null -> Card.Content.Text(content)
      else -> throw SerializationException("Unknown content type.")
    }
  )
}