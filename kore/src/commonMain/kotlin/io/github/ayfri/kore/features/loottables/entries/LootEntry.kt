package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.annotations.LootDsl
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

typealias LootEntries = MutableList<LootEntry>

/**
 * Base sealed class for all loot table entries.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@GeneratedSealedSerializer
@LootDsl
@Serializable(with = LootEntry.Companion.LootEntrySerializer::class)
sealed class LootEntry {
	/** Optional predicate condition guarding this entry. */
	abstract var condition: PredicateCondition?

	/** Optional item modifier applied to the items this entry yields. */
	abstract var modifier: InlinableItemModifier?

	companion object {
		data object LootEntrySerializer : NamespacedPolymorphicSerializer<LootEntry>(lootEntrySealedSerializer())
	}
}

/** Loot entry holding child entries, rolled according to its type. */
interface CompositeLootEntry {
	var children: List<LootEntry>
}

/** Configure child entries. */
fun CompositeLootEntry.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun LootEntry.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate [predicate], written as its bare ID. */
fun LootEntry.condition(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun LootEntry.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}
