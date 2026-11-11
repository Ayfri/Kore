package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.cursor
import io.github.ayfri.kore.website.components.mc.McChat
import io.github.ayfri.kore.website.components.mc.McChatLine
import io.github.ayfri.kore.website.components.mc.McScene
import io.github.ayfri.kore.website.components.mc.McSpan
import io.github.ayfri.kore.website.components.mc.McUiStyle
import io.github.ayfri.kore.website.components.mc.SceneKind
import io.github.ayfri.kore.website.components.mc.chatColors
import kotlin.time.Duration.Companion.seconds
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Every `FormattingColor` as a chat line over the world, a click or Enter copying its `Color.X` name, which replaces the hex for a moment. */
@Composable
fun ChatColorPalette() {
	val scope = rememberCoroutineScope()
	var copied by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(copied) {
		if (copied == null) return@LaunchedEffect
		delay(2.seconds)
		copied = null
	}

	fun copy(name: String) {
		scope.launch {
			runCatching { window.navigator.clipboard.writeText("Color.$name").await() }
				.onSuccess { copied = name }
				.onFailure { console.error(it) }
		}
	}

	McScene(
		"In game: the 16 chat colors. Color.PINK and Color.PURPLE are boss bar colors, so they aren't in this list.",
		SceneKind.HUD,
		panorama = 4,
		/** A chat line is 9 GUI pixels tall, the chat sits 8 above the bottom of the screen and gets the same room above it. */
		height = chatColors.size * 9 + 16,
	) {
		McChat(McUiStyle.hudChat) {
			chatColors.forEach { (name, hex) ->
				McChatLine(McSpan("Color.$name  ${if (copied == name) "Copied" else hex}", hex), attrs = {
					attr("aria-label", "Copy Color.$name")
					attr("role", "button")
					attr("tabindex", "0")
					onClick { copy(name) }
					onKeyDown { event ->
						if (event.key == "Enter" || event.key == " ") {
							event.preventDefault()
							copy(name)
						}
					}
					style { cursor(Cursor.Pointer) }
				})
			}
		}
	}
}
