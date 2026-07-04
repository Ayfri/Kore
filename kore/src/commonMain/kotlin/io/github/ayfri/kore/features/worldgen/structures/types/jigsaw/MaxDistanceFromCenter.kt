package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = MaxDistanceFromCenter.Companion.MaxDistanceFromCenterSerializer::class)
data class MaxDistanceFromCenter(
	var horizontal: Int,
	var vertical: Int? = null,
) {
	companion object {
		data object MaxDistanceFromCenterSerializer :
			SinglePropertySimplifierSerializer<MaxDistanceFromCenter>(generatedSerializer(), "horizontal")
	}
}

fun maxDistanceFromCenter(value: Int) = MaxDistanceFromCenter(value)
fun maxDistanceFromCenter(horizontal: Int, vertical: Int? = null) = MaxDistanceFromCenter(horizontal, vertical)
