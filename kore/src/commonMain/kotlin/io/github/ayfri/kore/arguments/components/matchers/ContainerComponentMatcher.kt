package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

@Serializable
data class ContainerComponentMatcher(
	var items: CollectionMatcher<ItemStackPredicate>? = null,
) : ComponentMatcher()

fun DataComponentPredicate.container(init: ContainerComponentMatcher.() -> Unit) =
	apply { matchers += ContainerComponentMatcher().apply(init) }

fun ContainerComponentMatcher.items(block: CollectionMatcher<ItemStackPredicate>.() -> Unit) {
	items = CollectionMatcher<ItemStackPredicate>().apply(block)
}
