package codes.chrishorner.planner

import assertk.assertThat
import assertk.assertions.isEqualTo
import codes.chrishorner.planner.data.Tile
import codes.chrishorner.planner.data.Tile.Content
import codes.chrishorner.planner.data.TileFetchResult
import codes.chrishorner.planner.data.fetchTiles
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import kotlin.test.Test

class FetchTilesTest {
  @Test
  fun `server error`() = runTest {
    val result = fetchTiles(
      clock = june14at9am,
      timeZone = melbourneTimeZone,
      networkCall = { url -> httpResponse(url, code = 500, message = "Internal Server Error") },
      context = this.coroutineContext,
    )

    assertThat(result).isEqualTo(TileFetchResult.HttpFailure)
  }

  @Test
  fun `parsing failure`() = runTest {
    val result = fetchTiles(
      clock = june14at9am,
      timeZone = melbourneTimeZone,
      networkCall = { url ->
        httpResponse(url, code = 200, message = "OK", body = INVALID_JSON.toResponseBody())
      },
      context = this.coroutineContext,
    )

    assertThat(result).isEqualTo(TileFetchResult.ParsingFailure)
  }

  @Test
  fun `network failure`() = runTest {
    val result = fetchTiles(
      clock = june14at9am,
      timeZone = melbourneTimeZone,
      networkCall = { url -> throw IOException() },
      context = this.coroutineContext,
    )

    assertThat(result).isEqualTo(TileFetchResult.NetworkFailure)
  }

  @Test
  fun `success with text tiles`() = runTest {
    val result = fetchTiles(
      clock = june14at9am,
      timeZone = melbourneTimeZone,
      networkCall = { url ->
        httpResponse(url, code = 200, message = "OK", body = VALID_TEXT_JSON.toResponseBody())
      },
      context = this.coroutineContext,
    )

    assertThat(result).isEqualTo(
      TileFetchResult.Success(
        persistentListOf(
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
      ),
    )
  }

  @Test
  fun `success with image tiles`() = runTest {
    val result = fetchTiles(
      clock = june14at9am,
      timeZone = melbourneTimeZone,
      networkCall = { url ->
        httpResponse(url, code = 200, message = "OK", body = VALID_IMAGE_JSON.toResponseBody())
      },
      context = this.coroutineContext,
    )

    assertThat(result).isEqualTo(
      TileFetchResult.Success(
        persistentListOf(
          Tile(content = Content.Image(url = "0.svg", description = "⊾"), initialPosition = 0),
          Tile(content = Content.Image(url = "1.svg", description = "&"), initialPosition = 1),
          Tile(content = Content.Image(url = "2.svg", description = "X"), initialPosition = 2),
          Tile(content = Content.Image(url = "3.svg", description = ")"), initialPosition = 3),
          Tile(content = Content.Image(url = "4.svg", description = "N"), initialPosition = 4),
          Tile(content = Content.Image(url = "5.svg", description = "O"), initialPosition = 5),
          Tile(content = Content.Image(url = "6.svg", description = "P"), initialPosition = 6),
          Tile(content = Content.Image(url = "7.svg", description = "€"), initialPosition = 7),
          Tile(content = Content.Image(url = "8.svg", description = "→"), initialPosition = 8),
          Tile(content = Content.Image(url = "9.svg", description = "$"), initialPosition = 9),
          Tile(content = Content.Image(url = "10.svg", description = "("), initialPosition = 10),
          Tile(content = Content.Image(url = "11.svg", description = "+"), initialPosition = 11),
          Tile(content = Content.Image(url = "12.svg", description = "£"), initialPosition = 12),
          Tile(content = Content.Image(url = "13.svg", description = "✔"), initialPosition = 13),
          Tile(content = Content.Image(url = "14.svg", description = "R"), initialPosition = 14),
          Tile(content = Content.Image(url = "15.svg", description = "¥"), initialPosition = 15),
        )
      )
    )
  }

  @Test
  fun `failure with invalid tiles`() = runTest {
    val result = fetchTiles(
      clock = june14at9am,
      timeZone = melbourneTimeZone,
      networkCall = { url ->
        httpResponse(
          url = url,
          code = 200,
          message = "OK",
          body = VALID_JSON_INVALID_TILES.toResponseBody()
        )
      },
      context = this.coroutineContext,
    )

    assertThat(result).isEqualTo(TileFetchResult.ParsingFailure)
  }

  @Test
  fun `local year, month, and day are passed to NYT URL`() = runTest {
    var capturedUrl: String? = null

    fetchTiles(
      // Use a clock at June 14, 11pm UTC
      clock = object : Clock {
        override fun now() = Instant.parse("2025-06-14T23:00:00Z")
      },
      timeZone = melbourneTimeZone,
      networkCall = { url ->
        capturedUrl = url
        httpResponse(url, code = 200, message = "OK", body = VALID_TEXT_JSON.toResponseBody())
      }
    )

    // URL should be June 15 as provided time zone is ahead of UTC.
    assertThat(capturedUrl).isEqualTo("https://www.nytimes.com/svc/connections/v2/2025-06-15.json")
  }

  private fun httpResponse(
    url: String,
    code: Int,
    message: String,
    body: ResponseBody? = null,
  ): Response = Response.Builder()
    .request(Request.Builder().url(url).build())
    .protocol(Protocol.HTTP_2)
    .code(code)
    .message(message)
    .apply { if (body != null) body(body) }
    .build()

  private companion object TestData {
    val june14at9am = object : Clock {
      override fun now() = Instant.parse("2025-06-14T09:00:00Z")
    }

    val melbourneTimeZone = TimeZone.of("Australia/Melbourne")

    const val INVALID_JSON = "Not JSON!"

    const val VALID_TEXT_JSON = """
      {
        "status": "OK",
        "id": 773,
        "print_date": "2025-06-14",
        "editor": "Wyna Liu",
        "categories": [
          {
            "title": "GODDESS",
            "cards": [
              {
                "content": "DIVA",
                "position": 15
              },
              {
                "content": "ICON",
                "position": 10
              },
              {
                "content": "LEGEND",
                "position": 7
              },
              {
                "content": "QUEEN",
                "position": 1
              }
            ]
          },
          {
            "title": "ELEMENTS OF A CLASSIC “MOM” TATTOO",
            "cards": [
              {
                "content": "ARROW",
                "position": 9
              },
              {
                "content": "HEART",
                "position": 6
              },
              {
                "content": "MOM",
                "position": 0
              },
              {
                "content": "RIBBON",
                "position": 2
              }
            ]
          },
          {
            "title": "DOGS OF THE SCREEN",
            "cards": [
              {
                "content": "ASTRO",
                "position": 14
              },
              {
                "content": "BLUE",
                "position": 4
              },
              {
                "content": "HOOCH",
                "position": 11
              },
              {
                "content": "TOTO",
                "position": 8
              }
            ]
          },
          {
            "title": "___ TERRIER",
            "cards": [
              {
                "content": "BORDER",
                "position": 3
              },
              {
                "content": "BOSTON",
                "position": 5
              },
              {
                "content": "BULL",
                "position": 13
              },
              {
                "content": "RAT",
                "position": 12
              }
            ]
          }
        ]
      }
    """

    const val VALID_IMAGE_JSON = """
      {
        "status": "OK",
        "id": 672,
        "print_date": "2025-04-01",
        "editor": "Wyna Liu",
        "categories": [
          {
            "title": "CURRENCY SYMBOLS",
            "cards": [
              {
                "position": 9,
                "image_url": "9.svg",
                "image_alt_text": "$"
              },
              {
                "position": 7,
                "image_url": "7.svg",
                "image_alt_text": "€"
              },
              {
                "position": 12,
                "image_url": "12.svg",
                "image_alt_text": "£"
              },
              {
                "position": 15,
                "image_url": "15.svg",
                "image_alt_text": "¥"
              }
            ]
          },
          {
            "title": "AND/TOGETHER WITH",
            "cards": [
              {
                "position": 1,
                "image_url": "1.svg",
                "image_alt_text": "&"
              },
              {
                "position": 11,
                "image_url": "11.svg",
                "image_alt_text": "+"
              },
              {
                "position": 4,
                "image_url": "4.svg",
                "image_alt_text": "N"
              },
              {
                "position": 2,
                "image_url": "2.svg",
                "image_alt_text": "X"
              }
            ]
          },
          {
            "title": "EMOTICON MOUTHS",
            "cards": [
              {
                "position": 10,
                "image_url": "10.svg",
                "image_alt_text": "("
              },
              {
                "position": 3,
                "image_url": "3.svg",
                "image_alt_text": ")"
              },
              {
                "position": 5,
                "image_url": "5.svg",
                "image_alt_text": "O"
              },
              {
                "position": 6,
                "image_url": "6.svg",
                "image_alt_text": "P"
              }
            ]
          },
          {
            "title": "\"RIGHT\"",
            "cards": [
              {
                "position": 14,
                "image_url": "14.svg",
                "image_alt_text": "R"
              },
              {
                "position": 8,
                "image_url": "8.svg",
                "image_alt_text": "→"
              },
              {
                "position": 0,
                "image_url": "0.svg",
                "image_alt_text": "⊾"
              },
              {
                "position": 13,
                "image_url": "13.svg",
                "image_alt_text": "✔"
              }
            ]
          }
        ]
      }
    """

    const val VALID_JSON_INVALID_TILES = """
      {
        "status": "OK",
        "id": 773,
        "print_date": "2025-06-14",
        "editor": "Wyna Liu",
        "categories": [
          {
            "title": "UNKNOWN 1",
            "cards": [
              {
                "unknown_content": "UNKNOWN",
                "position": 15
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 10
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 7
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 1
              }
            ]
          },
          {
            "title": "UNKNOWN 2",
            "cards": [
              {
                "unknown_content": "UNKNOWN",
                "position": 9
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 6
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 0
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 2
              }
            ]
          },
          {
            "title": "UNKNOWN 3",
            "cards": [
              {
                "unknown_content": "UNKNOWN",
                "position": 14
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 4
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 11
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 8
              }
            ]
          },
          {
            "title": "UNKNOWN 4",
            "cards": [
              {
                "unknown_content": "UNKNOWN",
                "position": 3
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 5
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 13
              },
              {
                "unknown_content": "UNKNOWN",
                "position": 12
              }
            ]
          }
        ]
      }
    """
  }
}