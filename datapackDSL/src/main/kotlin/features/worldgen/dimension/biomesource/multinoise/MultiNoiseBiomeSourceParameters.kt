package features.worldgen.dimension.biomesource.multinoise

import kotlinx.serialization.Serializable

@Serializable
data class MultiNoiseBiomeSourceParameters(
	var temperature: DoubleOrDoublePair,
	var humidity: DoubleOrDoublePair,
	var continentalness: DoubleOrDoublePair,
	var erosion: DoubleOrDoublePair,
	var weirdness: DoubleOrDoublePair,
	var depth: DoubleOrDoublePair,
	var offset: Double
)

fun multiNoiseBiomeSourceParameters(
	temperature: Double,
	humidity: Double,
	continentalness: Double,
	erosion: Double,
	weirdness: Double,
	depth: Double,
	offset: Double
) = MultiNoiseBiomeSourceParameters(
	doubleOrPair(temperature),
	doubleOrPair(humidity),
	doubleOrPair(continentalness),
	doubleOrPair(erosion),
	doubleOrPair(weirdness),
	doubleOrPair(depth),
	offset
)

fun multiNoiseBiomeSourceParameters(
	temperature: Pair<Double, Double>,
	humidity: Pair<Double, Double>,
	continentalness: Pair<Double, Double>,
	erosion: Pair<Double, Double>,
	weirdness: Pair<Double, Double>,
	depth: Pair<Double, Double>,
	offset: Double
) = MultiNoiseBiomeSourceParameters(
	doubleOrPair(temperature),
	doubleOrPair(humidity),
	doubleOrPair(continentalness),
	doubleOrPair(erosion),
	doubleOrPair(weirdness),
	doubleOrPair(depth),
	offset
)

/**
 * Creates a [MultiNoiseBiomeSourceParameters] with all values set to `0.0` by default.
 */
fun multiNoiseBiomeSourceParameters(
	block: MultiNoiseBiomeSourceParameters.() -> Unit = {}
) = multiNoiseBiomeSourceParameters(
	0.0,
	0.0,
	0.0,
	0.0,
	0.0,
	0.0,
	0.0
).apply(block)
