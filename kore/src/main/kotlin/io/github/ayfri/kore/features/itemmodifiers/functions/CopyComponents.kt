package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** Source of components to copy for `copy_components`. */
@Serializable(with = CopyComponentsSource.Companion.CopyComponentsSourceSerializer::class)
enum class CopyComponentsSource {
	ATTACKER,
	ATTACKING_PLAYER,
	BLOCK_ENTITY,
	DIRECT_ATTACKER,
	INTERACTING_ENTITY,
	TARGET_ENTITY,
	THIS,
	TOOL,
	;

	companion object {
		data object CopyComponentsSourceSerializer : LowercaseSerializer<CopyComponentsSource>(entries)
	}
}

/**
 * Copies components from a source (currently block entity) onto the target item.
 * Mirrors vanilla `minecraft:copy_components`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class CopyComponents(
	override var conditions: PredicateAsList? = null,
	var source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	var include: List<String> = emptyList(),
	var exclude: List<String> = emptyList(),
) : ItemFunction()

fun ItemModifier.copyComponents(
	include: List<ItemComponentTypes> = emptyList(),
	exclude: List<ItemComponentTypes> = emptyList(),
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	block: CopyComponents.() -> Unit = {},
) {
	modifiers += CopyComponents(
		source = source,
		include = include.map { "minecraft:${it.name.lowercase()}" },
		exclude = exclude.map { "minecraft:${it.name.lowercase()}" }
	).apply(block)
}

/** Convenience builder for `copy_components` with explicit includes. */
fun ItemModifier.copyComponentsInclude(
	vararg components: ItemComponentTypes,
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	block: CopyComponents.() -> Unit = {},
) {
	modifiers += CopyComponents(source = source, include = components.map { "minecraft:${it.name.lowercase()}" }).apply(block)
}

/** Convenience builder for `copy_components` with explicit excludes. */
fun ItemModifier.copyComponentsExclude(
	vararg components: ItemComponentTypes,
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	block: CopyComponents.() -> Unit = {},
) {
	modifiers += CopyComponents(source = source, exclude = components.map { "minecraft:${it.name.lowercase()}" }).apply(block)
}

/** Add components to the include list. */
fun CopyComponents.include(vararg components: ItemComponentTypes) {
	include = components.map { "minecraft:${it.name.lowercase()}" }
}

/** Add components to the exclude list. */
fun CopyComponents.exclude(vararg components: ItemComponentTypes) {
	exclude = components.map { "minecraft:${it.name.lowercase()}" }
}
