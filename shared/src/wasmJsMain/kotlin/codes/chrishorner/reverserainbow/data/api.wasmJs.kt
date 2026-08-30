package codes.chrishorner.reverserainbow.data

import io.ktor.http.Url

// In the browser we need to worry about CORS, so we proxy the requests through Netlify.
// See webApp/src/wasmJsMain/resources/_redirects

actual val ApiEndpoint: String = "${currentOrigin()}/api/connections/"

actual fun imageUrl(apiUrl: String): String {
  val protocol = Url(apiUrl).protocol.name
  return "${currentOrigin()}/api/proxy/${apiUrl.removePrefix("$protocol://")}"
}

private fun currentOrigin(): String = js("window.location.origin")
