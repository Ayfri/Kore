package io.github.ayfri.kore.website.components.layouts

import io.github.ayfri.kore.website.components.doc.DocArticle
import io.github.ayfri.kore.website.components.doc.getOrderedDocEntries
import io.github.ayfri.kore.website.externals.PageSwapEvent
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL

/**
 * Cross-document view transitions between the site's pages, animated by `public/view-transitions.css`.
 *
 * On `pageswap`, the old page hands the doc-order direction to the new one through [HANDOFF_KEY], which the `pagereveal`
 * script of `build.gradle.kts` turns into a view transition type.
 */
object PageTransitions {
	const val CHROME_CLASS = "chrome"
	const val DOC_SIDEBAR = "doc-sidebar"
	const val HANDOFF_KEY = "kore-view-transition"
	const val MORPH_CLASS = "morph"
	const val NAV_INDICATOR = "nav-indicator"
	const val SITE_HEADER = "site-header"

	fun install() = window.addEventListener("pageswap", ::onPageSwap)

	private fun onPageSwap(event: Event) {
		val swap = event.unsafeCast<PageSwapEvent>()
		if (swap.viewTransition == null) return
		val destination = swap.activation?.entry?.url?.let { URL(it).pathname } ?: return

		val docPaths = getOrderedDocEntries().map(DocArticle::path)
		val from = docPaths.indexOf(window.location.pathname)
		val to = docPaths.indexOf(destination)
		if (from >= 0 && to >= 0 && from != to) {
			sessionStorage.setItem(HANDOFF_KEY, "${if (to > from) "doc-forward" else "doc-backward"} $destination")
		}
	}
}
