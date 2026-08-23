package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

/**
 * Keeps [handlers] attached to this target for as long as the calling composable is alive, and detaches them on
 * disposal so navigating between pages never stacks listeners.
 *
 * Pass [key] whenever a handler closes over a value that must stay up to date.
 */
@Composable
fun EventTarget.onEvents(vararg handlers: Pair<String, (Event) -> Unit>, key: Any? = Unit) =
	DisposableEffect(this, key) {
		handlers.forEach { (type, handler) -> addEventListener(type, handler) }
		onDispose { handlers.forEach { (type, handler) -> removeEventListener(type, handler) } }
	}
