package features.worldgen.heightproviders

import serializers.ProviderSerializer
import kotlinx.serialization.Serializable

typealias HeightProvider = @Serializable(with = HeightProviderSurrogate.Companion.HeightProviderSerializer::class) HeightProviderSurrogate

@Serializable
sealed interface HeightProviderSurrogate {
	companion object {
		data object HeightProviderSerializer : ProviderSerializer<HeightProvider>(serializer())
	}
}
