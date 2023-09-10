package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.serializers.ProviderSerializer
import kotlinx.serialization.Serializable

typealias HeightProvider = @Serializable(with = HeightProviderSurrogate.Companion.HeightProviderSerializer::class) HeightProviderSurrogate

@Serializable
sealed interface HeightProviderSurrogate {
	companion object {
		data object HeightProviderSerializer : ProviderSerializer<HeightProvider>(serializer())
	}
}
