package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.loottables.entries.LootEntries
import io.github.ayfri.kore.features.loottables.entries.LootEntry
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ContentComponentTypes.Companion.ContentComponentTypesSerializer::class)
enum class ContentComponentTypes {
	BUNDLE_CONTENTS,
	CHARGED_PROJECTILES,
	CONTAINER;

	companion object {
		data object ContentComponentTypesSerializer : LowercaseSerializer<ContentComponentTypes>(entries)
	}
}

@Serializable
data class SetContents(
	override var conditions: PredicateAsList? = null,
	var component: ContentComponentTypes? = null,
	var entries: List<LootEntry> = emptyList(),
) : ItemFunction()

fun ItemModifier.setContents(component: ContentComponentTypes, block: SetContents.() -> Unit = {}) =
	SetContents(component = component).apply(block).also { modifiers += it }

fun SetContents.entries(entries: LootEntries.() -> Unit) = apply { this.entries = buildList(entries) }
