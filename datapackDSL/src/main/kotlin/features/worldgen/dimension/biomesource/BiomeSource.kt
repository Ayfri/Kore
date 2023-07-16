package features.worldgen.dimension.biomesource

import kotlinx.serialization.Serializable
import serializers.NamespacedPolymorphicSerializer

@Serializable(with = BiomeSource.Companion.BiomeSourceSerializer::class)
sealed class BiomeSource {
	companion object {
		data object BiomeSourceSerializer : NamespacedPolymorphicSerializer<BiomeSource>(BiomeSource::class)
	}
}
