package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import kotlinx.serialization.Serializable

/**
 * Loot entry that expands a tag into its item list.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Tag(
	var condition: PredicateCondition? = null,
	var expand: Boolean,
	var modifier: InlinableItemModifier? = null,
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationUnPrefixedSerializer::class)
	var name: ItemTagArgument,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a Tag entry. */
fun LootEntries.tag(name: ItemTagArgument, expand: Boolean, block: Tag.() -> Unit = {}) {
	add(Tag(name = name, expand = expand).apply(block))
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Tag.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate [predicate], written as its bare ID. */
fun Tag.condition(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Tag.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}
