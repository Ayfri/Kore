package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.AxolotlVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:axolotl/variant` entity component, which sets the color variant of an axolotl.
 *
 * Exposed on axolotl buckets and spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#axolotl/variant
 */
@Serializable(with = AxolotlVariant.Companion.AxolotlVariantSerializer::class)
data class AxolotlVariant(
	var variant: AxolotlVariants
) : Component() {
	companion object {
		data object AxolotlVariantSerializer : InlineAutoSerializer<AxolotlVariant>(AxolotlVariant::class)
	}
}

/** Sets the axolotl color variant on an axolotl bucket, spawn egg or entity. */
fun ComponentsScope.axolotlVariant(variant: AxolotlVariants) {
	this[EntityItemComponentTypes.AXOLOTL_VARIANT] = AxolotlVariant(variant)
}
