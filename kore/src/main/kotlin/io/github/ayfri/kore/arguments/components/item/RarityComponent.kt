package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
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
data class RarityComponent(val rarity: Rarities) : Component() {
	companion object {
		data object RarityComponentSerializer : InlineAutoSerializer<RarityComponent>(RarityComponent::class)
	}
}

fun ComponentsScope.rarity(rarity: Rarities) = apply {
	this[ItemComponentTypes.RARITY] = RarityComponent(rarity)
}
