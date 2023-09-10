package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DensityFunctionType.Companion.DensityFunctionTypeSerializer::class)
sealed class DensityFunctionType {
	companion object {
		data object DensityFunctionTypeSerializer : NamespacedPolymorphicSerializer<DensityFunctionType>(DensityFunctionType::class)
	}
}
