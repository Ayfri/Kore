package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.WolfVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:wolf/variant` entity component, which sets the fur variant of a wolf.
 *
 * Exposed on wolf spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#wolf/variant
 */
@Serializable(with = WolfVariant.Companion.WolfVariantSerializer::class)
data class WolfVariant(
	var variant: WolfVariantArgument
) : Component() {
	companion object {
		data object WolfVariantSerializer : InlineAutoSerializer<WolfVariant, WolfVariantArgument>(
			serializer<WolfVariantArgument>(),
			WolfVariant::variant,
			::WolfVariant
		)
	}
}

/** Sets the fur variant of a wolf. */
fun ComponentsScope.wolfVariant(variant: WolfVariantArgument) {
	this[EntityItemComponentTypes.WOLF_VARIANT] = WolfVariant(variant)
}
