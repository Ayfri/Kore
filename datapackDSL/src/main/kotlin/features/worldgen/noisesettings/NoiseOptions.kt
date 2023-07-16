package features.worldgen.noisesettings

import kotlinx.serialization.Serializable

@Serializable
data class NoiseOptions(
	var minY: Int,
	var height: Int,
	var sizeHorizontal: Int,
	var sizeVertical: Int,
)
