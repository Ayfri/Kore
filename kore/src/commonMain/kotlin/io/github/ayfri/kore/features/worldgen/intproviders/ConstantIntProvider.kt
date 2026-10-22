package io.github.ayfri.kore.features.worldgen.intproviders

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Always returns the same integer.
 *
 * It is inlined when serialized, so it produces `5` rather than an object with a `type` field.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property value The value returned on every call.
 */
@Serializable(with = ConstantIntProvider.Companion.ConstantIntProviderSerializer::class)
data class ConstantIntProvider(val value: Int) : IntProvider {
	companion object {
		data object ConstantIntProviderSerializer : InlineAutoSerializer<ConstantIntProvider, Int>(
			serializer<Int>(),
			ConstantIntProvider::value,
			::ConstantIntProvider,
			"constant",
		)
	}
}
