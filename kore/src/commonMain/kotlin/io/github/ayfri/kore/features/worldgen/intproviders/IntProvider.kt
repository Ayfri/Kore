package io.github.ayfri.kore.features.worldgen.intproviders

import io.github.ayfri.kore.serializers.ProviderSerializer
import kotlinx.serialization.Serializable

typealias IntProvider = @Serializable(IntProviderSurrogate.Companion.IntProviderSerializer::class) IntProviderSurrogate

@Serializable
sealed interface IntProviderSurrogate {
	companion object {
		data object IntProviderSerializer : ProviderSerializer<IntProviderSurrogate>(serializer())
	}
}
