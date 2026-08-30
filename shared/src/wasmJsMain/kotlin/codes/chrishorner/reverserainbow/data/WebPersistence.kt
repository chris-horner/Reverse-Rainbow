package codes.chrishorner.reverserainbow.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.diamondedge.logging.logging
import kotlinx.browser.localStorage

class WebPersistence : Persistence {

  override val hasDismissedWelcomeMessage: State<Boolean>
    field: MutableState<Boolean> = mutableStateOf(false)

  override suspend fun load() {
    val dismissed = try {
      localStorage.getItem(DismissedWelcomeMessageKey) == "true"
    } catch (e: JsException) {
      logging("Reverse Rainbow").e(e) { "Failed to read welcome message dismissal." }
      false
    }

    hasDismissedWelcomeMessage.value = dismissed
  }

  override suspend fun dismissWelcomeMessage() {
    try {
      localStorage.setItem(DismissedWelcomeMessageKey, "true")
    } catch (e: JsException) {
      logging("Reverse Rainbow").e(e) { "Failed to persist welcome message dismissal." }
    }

    hasDismissedWelcomeMessage.value = true
  }
}

private const val DismissedWelcomeMessageKey = "dismissed_welcome_message"
