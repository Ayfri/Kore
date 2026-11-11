package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.MouseEvent

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

/**
 * Turns a pointer drag on this element into [onDrag] calls: [start] is read once when the pointer goes down, and every move until it goes up
 * or is cancelled reports it with the distance in pixels from where it went down. The element captures the pointer, so the drag goes on outside it.
 * The lambdas are captured once, so they must read state objects and not values.
 */
fun <E : HTMLElement, T> AttrsScope<E>.onPointerDrag(start: () -> T, onDrag: (start: T, dx: Int, dy: Int) -> Unit) {
	ref { element ->
		var press: Triple<T, Int, Int>? = null
		val onDown: (Event) -> Unit = { event ->
			val pointer = event.unsafeCast<MouseEvent>()
			press = Triple(start(), pointer.clientX, pointer.clientY)
			element.asDynamic().setPointerCapture(event.asDynamic().pointerId)
		}
		val onMove: (Event) -> Unit = { event ->
			val pointer = event.unsafeCast<MouseEvent>()
			press?.let { (origin, x, y) -> onDrag(origin, pointer.clientX - x, pointer.clientY - y) }
		}
		val onEnd: (Event) -> Unit = { press = null }
		element.addEventListener("pointerdown", onDown)
		element.addEventListener("pointermove", onMove)
		element.addEventListener("pointerup", onEnd)
		element.addEventListener("pointercancel", onEnd)
		onDispose {
			element.removeEventListener("pointerdown", onDown)
			element.removeEventListener("pointermove", onMove)
			element.removeEventListener("pointerup", onEnd)
			element.removeEventListener("pointercancel", onEnd)
		}
	}
}
