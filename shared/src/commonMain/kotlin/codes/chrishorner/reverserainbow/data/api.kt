package codes.chrishorner.reverserainbow.data

expect val ApiEndpoint: String

expect fun imageUrl(apiUrl: String): String

const val NytConnectionsUrl = "https://www.nytimes.com/games/connections"