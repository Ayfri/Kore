package features.worldgen.intproviders

import serializers.ProviderSerializer
import kotlinx.serialization.Serializable

typealias IntProvider = @Serializable(IntProviderSurrogate.Companion.IntProviderSerializer::class) IntProviderSurrogate

@Serializable
sealed interface IntProviderSurrogate {
	companion object {
		data object IntProviderSerializer : ProviderSerializer<IntProviderSurrogate>(serializer())
	}
}
