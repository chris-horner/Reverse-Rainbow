package codes.chrishorner.reverserainbow.data

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Don't bother pulling in data-store since we currently only need a single boolean and can work
// around the limitations of SharedPreferences.
class AndroidPersistence(context: Context) : Persistence {

  private val sharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

  override val hasDismissedWelcomeMessage: State<Boolean>
    field: MutableState<Boolean> = mutableStateOf(false)

  override suspend fun load() = withContext(Dispatchers.IO) {
    val dismissed = sharedPrefs.getBoolean(DismissedWelcomeMessageKey, false)
    hasDismissedWelcomeMessage.value = dismissed
  }

  override suspend fun dismissWelcomeMessage() = withContext(Dispatchers.IO) {
    sharedPrefs.edit(commit = true) {
      putBoolean(DismissedWelcomeMessageKey, true)
    }

    hasDismissedWelcomeMessage.value = true
  }
}

private const val DismissedWelcomeMessageKey = "dismissed_welcome_message"