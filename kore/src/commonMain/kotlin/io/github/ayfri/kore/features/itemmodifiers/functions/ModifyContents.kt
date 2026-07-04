package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Applies nested item functions to items contained within a specific content component
 * (e.g., bundle contents, container, charged projectiles). Equivalent to vanilla
 * `minecraft:modify_contents`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class ModifyContents(
	override var conditions: PredicateAsList? = null,
	var component: ContentComponentTypes? = null,
	var modifier: InlinableList<ItemFunction> = emptyList(),
) : ItemFunction()

fun ItemModifier.modifyContents(component: ContentComponentTypes, block: ModifyContents.() -> Unit = {}) =
	ModifyContents(component = component).apply(block).also { modifiers += it }

/** Add nested item functions to be applied to the targeted component's entries. */
fun ModifyContents.modifiers(block: ItemModifier.() -> Unit) {
	modifier += ItemModifier().apply(block).modifiers
}
