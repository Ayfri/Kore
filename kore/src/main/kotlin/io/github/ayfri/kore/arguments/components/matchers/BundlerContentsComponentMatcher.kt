package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class BundlerContentsComponentMatcher(
	var items: CollectionMatcher<ItemStack>? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.bundlerContents(block: BundlerContentsComponentMatcher.() -> Unit) {
	matchers += BundlerContentsComponentMatcher().apply(block)
}

fun BundlerContentsComponentMatcher.items(block: CollectionMatcher<ItemStack>.() -> Unit) {
	items = CollectionMatcher<ItemStack>().apply(block)
}
