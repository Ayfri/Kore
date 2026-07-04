package io.github.ayfri.kore.features.worldgen.floatproviders

import io.github.ayfri.kore.serializers.ProviderSerializer
import kotlinx.serialization.Serializable

typealias FloatProvider = @Serializable(with = FloatProviderSurrogate.Companion.FloatProviderSerializer::class) FloatProviderSurrogate

@Serializable
sealed interface FloatProviderSurrogate {
	companion object {
		data object FloatProviderSerializer : ProviderSerializer<FloatProviderSurrogate>(serializer())
	}
}
