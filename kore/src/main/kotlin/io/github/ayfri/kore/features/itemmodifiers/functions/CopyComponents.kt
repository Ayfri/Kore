package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CopyComponentsSource.Companion.CopyComponentsSourceSerializer::class)
enum class CopyComponentsSource {
	BLOCK_ENTITY;

	companion object {
		object CopyComponentsSourceSerializer : LowercaseSerializer<CopyComponentsSource>(entries)
	}
}

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

fun ItemModifier.copyComponentsInclude(
	vararg components: ItemComponentTypes,
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	block: CopyComponents.() -> Unit = {},
) {
	modifiers += CopyComponents(source = source, include = components.map { "minecraft:${it.name.lowercase()}" }).apply(block)
}

fun ItemModifier.copyComponentsExclude(
	vararg components: ItemComponentTypes,
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	block: CopyComponents.() -> Unit = {},
) {
	modifiers += CopyComponents(source = source, exclude = components.map { "minecraft:${it.name.lowercase()}" }).apply(block)
}

fun CopyComponents.include(vararg components: ItemComponentTypes) {
	include = components.map { "minecraft:${it.name.lowercase()}" }
}

fun CopyComponents.exclude(vararg components: ItemComponentTypes) {
	exclude = components.map { "minecraft:${it.name.lowercase()}" }
}
