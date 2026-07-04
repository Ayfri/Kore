package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class ContainerComponentMatcher(
	var items: CollectionMatcher<ItemStack>? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.container(init: ContainerComponentMatcher.() -> Unit) =
	apply { matchers += ContainerComponentMatcher().apply(init) }

fun ContainerComponentMatcher.items(block: CollectionMatcher<ItemStack>.() -> Unit) {
	items = CollectionMatcher<ItemStack>().apply(block)
}
