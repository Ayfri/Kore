package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import kotlinx.serialization.Serializable

@Serializable
data class WritableBookContentComponentMatcher(
	var pages: CollectionMatcher<String> = CollectionMatcher(),
) : ComponentMatcher()

fun DataComponentPredicate.writableBookContent(init: WritableBookContentComponentMatcher.() -> Unit) =
	apply { matchers += WritableBookContentComponentMatcher().apply(init) }

fun WritableBookContentComponentMatcher.pages(block: CollectionMatcher<String>.() -> Unit) {
	pages = CollectionMatcher<String>().apply(block)
}
