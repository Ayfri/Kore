package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.window
import kotlin.js.json

/**
 * Google Analytics events of the playground, registered as custom dimensions in GA4 to be filtered on.
 *
 * `gtag` is missing when an ad blocker drops the Google tag, so every call is then a silent no-op.
 */
object PlaygroundAnalytics {
	fun track(event: String, vararg params: Pair<String, Any>) {
		val gtag = window.asDynamic().gtag ?: return
		gtag("event", event, json(*params))
	}
}
