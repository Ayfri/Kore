package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = DensityFunctionType.Companion.DensityFunctionTypeSerializer::class)
sealed class DensityFunctionType {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object DensityFunctionTypeSerializer :
			NamespacedPolymorphicSerializer<DensityFunctionType>(densityFunctionTypeSealedSerializer())
	}
}
