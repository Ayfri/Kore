package io.github.ayfri.kore.features.worldgen.floatproviders

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Always returns the same float.
 *
 * It is inlined when serialized, so it produces `1.5` rather than an object with a `type` field.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 *
 * @property value The value returned on every call.
 */
@Serializable(with = ConstantFloatProvider.Companion.ConstantFloatProviderSerializer::class)
data class ConstantFloatProvider(val value: Float) : FloatProvider {
	companion object {
		data object ConstantFloatProviderSerializer : InlineAutoSerializer<ConstantFloatProvider, Float>(
			serializer<Float>(),
			ConstantFloatProvider::value,
			::ConstantFloatProvider,
			"constant",
		)
	}
}
