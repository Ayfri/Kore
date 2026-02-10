package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ZombieNautilusVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ZombieNautilusVariant.Companion.ZombieNautilusVariantSerializer::class)
data class ZombieNautilusVariant(
	var variant: ZombieNautilusVariantArgument,
) : Component() {
	companion object {
		data object ZombieNautilusVariantSerializer : InlineAutoSerializer<ZombieNautilusVariant>(ZombieNautilusVariant::class)
	}
}

fun ComponentsScope.zombieNautilusVariant(variant: ZombieNautilusVariantArgument) {
	this[EntityItemComponentTypes.ZOMBIE_NAUTILUS_VARIANT] = ZombieNautilusVariant(variant)
}
