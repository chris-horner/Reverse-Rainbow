package codes.chrishorner.reverserainbow.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import codes.chrishorner.reverserainbow.resources.Res
import codes.chrishorner.reverserainbow.resources.about
import codes.chrishorner.reverserainbow.resources.back_description
import codes.chrishorner.reverserainbow.resources.clear_category
import codes.chrishorner.reverserainbow.resources.menu
import codes.chrishorner.reverserainbow.resources.shuffle
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BaselineProfileGenerator {

  @get:Rule
  val baselineProfileRule = BaselineProfileRule()

  @Test
  fun startup() = baselineProfileRule.collect(
    packageName = PackageName,
    includeInStartupProfile = true,
  ) {
    pressHome()
    startActivityAndWait()
    awaitGameLoad()
  }

  @Test
  fun gameJourney() = baselineProfileRule.collect(packageName = PackageName) {
    pressHome()
    startActivityAndWait()
    awaitGameLoad()

    with(device) {
      // Select the first four tiles.
      repeat(4) { position ->
        requireObject(By.res("tile_$position")).click()
        waitForIdle()
      }

      // Assign them to yellow.
      requireObject(By.res("category_action_YELLOW")).click()
      waitForIdle()

      // Yellow now holds four tiles and nothing is selected, so tapping it expands the category.
      requireObject(By.res("category_action_YELLOW")).click()
      waitForIdle()

      // Clear the category - resetting the board.
      requireObject(By.desc(Res.string.clear_category.resolve())).click()
      waitForIdle()

      // Do a shuffle - 'cause why not?
      clickToolbarAction(Res.string.shuffle)
      waitForIdle()

      // Visit the About screen and go back.
      clickToolbarAction(Res.string.about)
      requireObject(By.desc(Res.string.back_description.resolve()))
      pressBack()
      requireObject(By.res("grid"))
    }
  }
}

private fun UiAutomatorTestScope.awaitGameLoad() {
  device.requireObject(By.res("grid"), timeout = 20.seconds)
}

private fun UiDevice.clickToolbarAction(action: StringResource) {
  requireObject(By.desc(Res.string.menu.resolve())).click()
  requireObject(By.text(action.resolve())).click()
}

private fun StringResource.resolve(): String = runBlocking { getString(this@resolve) }

private fun UiDevice.requireObject(selector: BySelector, timeout: Duration = 2.seconds): UiObject2 {
  return checkNotNull(wait(Until.findObject(selector), timeout.inWholeMilliseconds)) {
    "Timed out waiting for $selector."
  }
}

private const val PackageName = "codes.chrishorner.reverserainbow"
