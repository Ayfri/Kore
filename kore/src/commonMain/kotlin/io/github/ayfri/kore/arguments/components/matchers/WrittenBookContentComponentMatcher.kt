package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class WrittenBookContentComponentMatcher(
	var pages: CollectionMatcher<ChatComponents> = CollectionMatcher(),
	var author: String? = null,
	var title: String? = null,
	var generation: IntRangeOrIntJson? = null,
	var resolved: Boolean? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.writtenBookContent(init: WrittenBookContentComponentMatcher.() -> Unit) =
	apply { matchers += WrittenBookContentComponentMatcher().apply(init) }

fun WrittenBookContentComponentMatcher.pages(block: CollectionMatcher<ChatComponents>.() -> Unit) {
	pages = CollectionMatcher<ChatComponents>().apply(block)
}

fun WrittenBookContentComponentMatcher.generation(value: Int) {
	generation = rangeOrInt(value)
}
