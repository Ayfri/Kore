package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.WolfSoundVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = WolfSoundVariant.Companion.WolfSoundVariantSerializer::class)
data class WolfSoundVariant(
	var variant: WolfSoundVariantArgument
) : Component() {
	companion object {
		data object WolfSoundVariantSerializer : InlineAutoSerializer<WolfSoundVariant>(WolfSoundVariant::class)
	}
}

fun ComponentsScope.wolfSoundVariant(variant: WolfSoundVariantArgument) {
	this[EntityItemComponentTypes.WOLF_SOUND_VARIANT] = WolfSoundVariant(variant)
}
