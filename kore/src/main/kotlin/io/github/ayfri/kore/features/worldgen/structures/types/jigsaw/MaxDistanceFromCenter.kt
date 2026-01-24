package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MaxDistanceFromCenter.MaxDistanceFromCenterSerializer::class)
data class MaxDistanceFromCenter(
	var horizontal: Int,
	var vertical: Int? = null,
) {
	data object MaxDistanceFromCenterSerializer : SinglePropertySimplifierSerializer<MaxDistanceFromCenter, Int>(
		MaxDistanceFromCenter::class,
		MaxDistanceFromCenter::horizontal
	)
}

fun maxDistanceFromCenter(value: Int) = MaxDistanceFromCenter(value)
fun maxDistanceFromCenter(horizontal: Int, vertical: Int? = null) = MaxDistanceFromCenter(horizontal, vertical)
