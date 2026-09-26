package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

@Serializable
data class BundleContentsComponentMatcher(
	var items: CollectionMatcher<ItemStackPredicate>? = null,
) : ComponentMatcher()

fun DataComponentPredicate.bundleContents(block: BundleContentsComponentMatcher.() -> Unit) {
	matchers += BundleContentsComponentMatcher().apply(block)
}

fun BundleContentsComponentMatcher.items(block: CollectionMatcher<ItemStackPredicate>.() -> Unit) {
	items = CollectionMatcher<ItemStackPredicate>().apply(block)
}
