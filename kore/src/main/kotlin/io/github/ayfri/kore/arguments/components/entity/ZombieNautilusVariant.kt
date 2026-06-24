package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ZombieNautilusVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:zombie_nautilus/variant` entity component, which sets the variant of a zombie nautilus.
 *
 * Exposed on zombie nautilus spawn eggs (and the entity itself). Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#zombie_nautilus/variant
 */
@Serializable(with = ZombieNautilusVariant.Companion.ZombieNautilusVariantSerializer::class)
data class ZombieNautilusVariant(
	var variant: ZombieNautilusVariantArgument,
) : Component() {
	companion object {
		data object ZombieNautilusVariantSerializer : InlineAutoSerializer<ZombieNautilusVariant>(ZombieNautilusVariant::class)
	}
}

/** Sets the variant of a zombie nautilus. */
fun ComponentsScope.zombieNautilusVariant(variant: ZombieNautilusVariantArgument) {
	this[EntityItemComponentTypes.ZOMBIE_NAUTILUS_VARIANT] = ZombieNautilusVariant(variant)
}
