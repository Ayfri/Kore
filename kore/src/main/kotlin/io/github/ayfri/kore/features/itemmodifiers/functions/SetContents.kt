package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.loottables.entries.LootEntries
import io.github.ayfri.kore.features.loottables.entries.LootEntry
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** Target content component for `set_contents` and `modify_contents`. */
@Serializable(with = ContentComponentTypes.Companion.ContentComponentTypesSerializer::class)
enum class ContentComponentTypes {
	BUNDLE_CONTENTS,
	CHARGED_PROJECTILES,
	CONTAINER;

	companion object {
		data object ContentComponentTypesSerializer : LowercaseSerializer<ContentComponentTypes>(entries)
	}
}

/**
 * Replaces the contents of a component (bundle/container/charged projectiles) with loot entries.
 * Mirrors vanilla `minecraft:set_contents`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetContents(
	override var conditions: PredicateAsList? = null,
	var component: ContentComponentTypes? = null,
	var entries: List<LootEntry> = emptyList(),
) : ItemFunction()

/** Add a `set_contents` step. */
fun ItemModifier.setContents(component: ContentComponentTypes, block: SetContents.() -> Unit = {}) =
	SetContents(component = component).apply(block).also { modifiers += it }

/** Configure the loot entries to insert into the targeted component. */
fun SetContents.entries(entries: LootEntries.() -> Unit) = apply { this.entries = buildList(entries) }
