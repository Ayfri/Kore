package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.ItemModifierOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.ItemModifierTagArgument
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.serializers.IdOrTagReferenceSerializer
import kotlinx.serialization.Serializable

/**
 * Applies the item modifier [id] points to, written as the bare string `"my_pack:my_modifier"` wherever an item
 * function is expected. A tag (`"#my_pack:my_tag"`) applies every modifier it holds. A cyclic reference makes the pack
 * fail to load.
 *
 * It has no [condition] of its own, so one to guard it must wrap it in a `sequence`. A list can mix IDs and inline
 * values, but a tag can't be one of its elements, only the whole list.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * Minecraft Wiki: [Item modifier](https://minecraft.wiki/w/Item_modifier)
 *
 * @property id The ID of the item modifier file, or of the item modifier tag.
 */
@Serializable(with = ItemModifierReference.Companion.ItemModifierReferenceSerializer::class)
data class ItemModifierReference(var id: ItemModifierOrTagArgument) : ItemFunction() {
	override var condition: PredicateCondition? = null

	companion object {
		data object ItemModifierReferenceSerializer :
			IdOrTagReferenceSerializer<ItemModifierReference, ItemModifierOrTagArgument>(
				ItemModifierReference::id,
				::ItemModifierReference,
				{ name, namespace -> ItemModifierArgument(name, namespace) },
				{ name, namespace -> ItemModifierTagArgument(name, namespace) },
				"item_modifier_reference",
			)
	}
}

/**
 * Adds an [ItemModifierReference] pointing at the item modifier or item modifier tag [itemModifier].
 *
 * ```
 * reference(ItemModifierArgument("enchant_sword", "my_pack"))  // "my_pack:enchant_sword"
 * reference(ItemModifierTagArgument("swords", "my_pack"))      // "#my_pack:swords"
 * ```
 */
fun ItemModifier.reference(itemModifier: ItemModifierOrTagArgument) {
	modifiers += ItemModifierReference(itemModifier)
}
