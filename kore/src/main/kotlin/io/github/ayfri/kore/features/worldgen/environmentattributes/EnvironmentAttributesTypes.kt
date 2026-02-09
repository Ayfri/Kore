package io.github.ayfri.kore.features.worldgen.environmentattributes

import io.github.ayfri.kore.features.worldgen.environmentattributes.types.EnvironmentAttributesType
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

/**
 * Value container used in the `attributes` field of biomes and dimension types.
 *
 * Environment attributes can be expressed as:
 * - a raw JSON value (assumed `minecraft:override`)
 * - an expanded object with `modifier` and `argument`
 */
@Serializable(with = EnvironmentAttributeValue.Companion.EnvironmentAttributeValueSerializer::class)
data class EnvironmentAttributeValue(
	val argument: EnvironmentAttributesType,
	val modifier: EnvironmentAttributeModifier? = null,
) {
	companion object {
		data object EnvironmentAttributeValueSerializer :
			SinglePropertySimplifierSerializer<EnvironmentAttributeValue, EnvironmentAttributesType>(
				kClass = EnvironmentAttributeValue::class,
				property = EnvironmentAttributeValue::argument,
			)
	}
}

/** Creates an [EnvironmentAttributeValue] from any serializable value. */
fun environmentAttributeValue(
	argument: EnvironmentAttributesType,
	modifier: EnvironmentAttributeModifier? = null,
): EnvironmentAttributeValue = EnvironmentAttributeValue(
	argument = argument,
	modifier = modifier,
)
