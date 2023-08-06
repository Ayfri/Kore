package features.worldgen.floatproviders

import serializers.ProviderSerializer
import kotlinx.serialization.Serializable

typealias FloatProvider = @Serializable(with = FloatProviderSurrogate.Companion.FloatProviderSerializer::class) FloatProviderSurrogate

@Serializable
sealed interface FloatProviderSurrogate {
	companion object {
		data object FloatProviderSerializer : ProviderSerializer<FloatProviderSurrogate>(serializer())
	}
}
