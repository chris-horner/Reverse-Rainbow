package codes.chrishorner.reverserainbow.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
interface Persistence {
  val hasDismissedWelcomeMessage: State<Boolean>
  suspend fun load()
  suspend fun dismissWelcomeMessage()
}

val LocalPersistence = staticCompositionLocalOf<Persistence> {
  error("Persistence composition local not set.")
}