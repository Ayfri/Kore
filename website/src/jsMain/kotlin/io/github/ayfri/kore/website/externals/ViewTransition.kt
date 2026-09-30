package io.github.ayfri.kore.website.externals

/** Bindings for the parts of the Navigation and View Transitions APIs the cross-document page transitions read. */
external interface NavigationActivation {
	val entry: NavigationHistoryEntry
}

external interface NavigationHistoryEntry {
	val url: String?
}

/** Fired on the old page right before its last frame is captured, [activation] is missing in browsers without the Navigation API. */
external interface PageSwapEvent {
	val activation: NavigationActivation?
	val viewTransition: ViewTransition?
}

external interface ViewTransition
