package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.ComponentTypes
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
	val source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
	val components: List<String> = emptyList(),
) : ItemFunction()

fun ItemModifier.copyComponents(
	components: List<String> = emptyList(),
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
) {
	modifiers += CopyComponents(source = source, components = components)
}

fun ItemModifier.copyComponents(
	components: List<ComponentTypes>,
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
) {
	modifiers += CopyComponents(source = source, components = components.map { it.name.lowercase() })
}

fun ItemModifier.copyComponents(
	vararg components: ComponentTypes,
	source: CopyComponentsSource = CopyComponentsSource.BLOCK_ENTITY,
) {
	modifiers += CopyComponents(source = source, components = components.map { it.name.lowercase() })
}
