package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:lock` item component, which locks a container so only players holding a matching item can open it.
 *
 * The value is an item predicate: specify [items] and optionally [count], [components], and [predicates] to define the required key item.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#lock
 */
@Serializable
data class LockComponent(
	var items: InlinableList<ItemOrTagArgument>? = null,
	var count: Int? = null,
	var components: ComponentsPatch? = null,
	var predicates: ItemStackSubPredicates? = null,
) : Component()

/** Locks a container so only players holding an item matching the predicate can open it. */
fun ComponentsScope.lock(block: LockComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.LOCK] = LockComponent().apply(block)
}

fun ComponentsScope.lock(vararg items: ItemOrTagArgument, block: LockComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.LOCK] = LockComponent(items = items.toList()).apply(block)
}

fun LockComponent.components(block: ComponentsPatch.() -> Unit) {
	components = (components ?: ComponentsPatch()).apply(block)
}

fun LockComponent.predicates(block: ItemStackSubPredicates.() -> Unit) {
	predicates = (predicates ?: ItemStackSubPredicates()).apply(block)
}
