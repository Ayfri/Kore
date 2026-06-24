package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.MooshroomVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:mooshroom/variant` entity component, which sets the variant of a mooshroom (red or brown).
 *
 * Exposed on mooshroom spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#mooshroom/variant
 */
@Serializable(with = MooshroomVariant.Companion.MooshroomVariantSerializer::class)
data class MooshroomVariant(
	var variant: MooshroomVariants
) : Component() {
	companion object {
		data object MooshroomVariantSerializer : InlineAutoSerializer<MooshroomVariant>(MooshroomVariant::class)
	}
}

/** Sets the variant of a mooshroom (red or brown). */
fun ComponentsScope.mooshroomVariant(variant: MooshroomVariants) {
	this[EntityItemComponentTypes.MOOSHROOM_VARIANT] = MooshroomVariant(variant)
}
