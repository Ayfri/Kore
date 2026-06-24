package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.WolfSoundVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:wolf/sound_variant` entity component, which sets the sound variant of a wolf.
 *
 * Exposed on wolf spawn eggs (and the entity itself). Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#wolf/sound_variant
 */
@Serializable(with = WolfSoundVariant.Companion.WolfSoundVariantSerializer::class)
data class WolfSoundVariant(
	var variant: WolfSoundVariantArgument
) : Component() {
	companion object {
		data object WolfSoundVariantSerializer : InlineAutoSerializer<WolfSoundVariant>(WolfSoundVariant::class)
	}
}

/** Sets the sound variant of a wolf. */
fun ComponentsScope.wolfSoundVariant(variant: WolfSoundVariantArgument) {
	this[EntityItemComponentTypes.WOLF_SOUND_VARIANT] = WolfSoundVariant(variant)
}
