package codes.chrishorner.planner

import androidx.compose.material3.Text
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

fun main() {
  ComposeViewport(document.body!!) {
    Text("Sup web?")
  }
}