package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

@Serializable
data class BundlerContentsComponentMatcher(
	var items: CollectionMatcher<ItemStackPredicate>? = null,
) : ComponentMatcher()

fun DataComponentPredicate.bundlerContents(block: BundlerContentsComponentMatcher.() -> Unit) {
	matchers += BundlerContentsComponentMatcher().apply(block)
}

fun BundlerContentsComponentMatcher.items(block: CollectionMatcher<ItemStackPredicate>.() -> Unit) {
	items = CollectionMatcher<ItemStackPredicate>().apply(block)
}
