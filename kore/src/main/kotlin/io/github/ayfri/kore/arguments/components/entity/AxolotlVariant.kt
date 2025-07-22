package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.AxolotlVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AxolotlVariant.Companion.AxolotlVariantSerializer::class)
data class AxolotlVariant(
	var variant: AxolotlVariants
) : Component() {
	companion object {
		data object AxolotlVariantSerializer : InlineAutoSerializer<AxolotlVariant>(AxolotlVariant::class)
	}
}

fun ComponentsScope.axolotlVariant(variant: AxolotlVariants) {
	this[EntityItemComponentTypes.AXOLOTL_VARIANT] = AxolotlVariant(variant)
}
