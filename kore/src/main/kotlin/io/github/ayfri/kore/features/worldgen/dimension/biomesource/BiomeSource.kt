package io.github.ayfri.kore.features.worldgen.dimension.biomesource

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = BiomeSource.Companion.BiomeSourceSerializer::class)
sealed class BiomeSource {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BiomeSourceSerializer : NamespacedPolymorphicSerializer<BiomeSource>(biomeSourceSealedSerializer())
	}
}
