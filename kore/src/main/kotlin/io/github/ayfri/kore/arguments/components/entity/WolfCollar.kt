package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:wolf/collar` entity component, which sets the dye color of a wolf's collar.
 *
 * Exposed on wolf spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#wolf/collar
 */
@Serializable(with = WolfCollar.Companion.WolfCollarSerializer::class)
data class WolfCollar(
	var color: DyeColors
) : Component() {
	companion object {
		data object WolfCollarSerializer : InlineAutoSerializer<WolfCollar>(WolfCollar::class)
	}
}

/** Sets the dye color of a wolf's collar. */
fun ComponentsScope.wolfCollar(color: DyeColors) {
	this[EntityItemComponentTypes.WOLF_COLLAR] = WolfCollar(color)
}
