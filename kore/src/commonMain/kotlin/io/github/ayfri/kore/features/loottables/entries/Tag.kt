package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Loot entry that expands a tag into its item list.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Tag(
	var conditions: PredicateAsList? = null,
	var expand: Boolean,
	var functions: ItemModifierAsList? = null,
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationUnPrefixedSerializer::class)
	var name: ItemTagArgument,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a Tag entry. */
fun LootEntries.tag(name: ItemTagArgument, expand: Boolean, block: Tag.() -> Unit = {}) {
	add(Tag(name = name, expand = expand).apply(block))
}

/** Set conditions, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Tag.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

/** Set item modifier functions, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Tag.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
