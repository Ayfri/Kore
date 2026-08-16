package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Base type for every density function node (e.g. [Abs], [Add], [Noise]).
 *
 * Each subtype is created through an extension function on [io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope]
 * (e.g. [abs], [add], [noise]) called inside a [io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctions] block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@GeneratedSealedSerializer
@Serializable(with = DensityFunctionType.Companion.DensityFunctionTypeSerializer::class)
sealed class DensityFunctionType {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object DensityFunctionTypeSerializer :
			NamespacedPolymorphicSerializer<DensityFunctionType>(densityFunctionTypeSealedSerializer())
	}
}
