package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = DimensionPadding.Companion.DimensionPaddingSerializer::class)
data class DimensionPadding(
	var top: Int,
	var bottom: Int? = null,
) {
	companion object {
		data object DimensionPaddingSerializer :
			SinglePropertySimplifierSerializer<DimensionPadding>(generatedSerializer(), "top")
	}
}
