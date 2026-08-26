package codes.chrishorner.reverserainbow.data

// Android doesn't care about CORS so we can use the NYT URLs without proxying.

actual const val ApiEndpoint: String = "https://www.nytimes.com/svc/connections/v2/"

actual fun imageUrl(apiUrl: String): String = apiUrl
