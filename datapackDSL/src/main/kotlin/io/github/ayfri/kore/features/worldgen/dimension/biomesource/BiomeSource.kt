package io.github.ayfri.kore.features.worldgen.dimension.biomesource

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BiomeSource.Companion.BiomeSourceSerializer::class)
sealed class BiomeSource {
	companion object {
		data object BiomeSourceSerializer : NamespacedPolymorphicSerializer<BiomeSource>(BiomeSource::class)
	}
}
