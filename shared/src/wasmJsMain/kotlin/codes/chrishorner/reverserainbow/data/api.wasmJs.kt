package codes.chrishorner.reverserainbow.data

// Requests go through a Netlify proxy rewrite (see webApp/src/wasmJsMain/resources/_redirects)
// which forwards to https://www.nytimes.com/svc/connections/v2/ server-side, avoiding browser CORS.
actual val ApiEndpoint: String = "${currentOrigin()}/api/connections/"

private fun currentOrigin(): String = js("window.location.origin")
