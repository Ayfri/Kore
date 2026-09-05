package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** The item rarity tier, which controls the default name color. */
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

/**
 * Represents the `minecraft:rarity` item component, which sets the item name color tier (common, uncommon, rare, epic).
 *
 * Serializes as the rarity string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#rarity
 */
@Serializable(with = RarityComponent.Companion.RarityComponentSerializer::class)
data class RarityComponent(val rarity: Rarities) : Component() {
	companion object {
		data object RarityComponentSerializer : InlineAutoSerializer<RarityComponent, Rarities>(
			serializer<Rarities>(),
			RarityComponent::rarity,
			::RarityComponent
		)
	}
}

/** Sets the item name color tier (common, uncommon, rare, epic). */
fun ComponentsScope.rarity(rarity: Rarities) = apply {
	this[ItemComponentTypes.RARITY] = RarityComponent(rarity)
}
