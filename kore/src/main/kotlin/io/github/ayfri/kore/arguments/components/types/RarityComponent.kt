package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Rarities.Companion.RaritySerializer::class)
enum class Rarities {
	COMMON,
	UNCOMMON,
	RARE,
	EPIC;

	companion object {
		data object RaritySerializer : LowercaseSerializer<Rarities>(entries)
	}
}

@Serializable(with = RarityComponent.Companion.RarityComponentSerializer::class)
data class RarityComponent(
	val rarity: Rarities,
) : Component() {
	companion object {
		object RarityComponentSerializer : InlineSerializer<RarityComponent, Rarities>(
			Rarities.serializer(),
			RarityComponent::rarity
		)
	}
}

fun ComponentsScope.rarity(rarity: Rarities) = apply {
	this[ItemComponentTypes.RARITY] = RarityComponent(rarity)
}
