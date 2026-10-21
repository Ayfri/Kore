package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * A class representing a NoiseRouter.
 *
 * This class is used for configuring various density functions or doubles used in noise generation.
 *
 * @property continents The density function or double for continents.
 * @property depth The density function or double for depth.
 * @property erosion The density function or double for erosion.
 * @property finalDensity The density function or double for final density.
 * @property preliminarySurfaceLevel The density function or double for preliminary surface level.
 * @property ridges The density function or double for ridges.
 * @property temperature The density function or double for temperature.
 * @property vegetation The density function or double for vegetation.
 */
@Serializable
data class NoiseRouter(
	var continents: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var depth: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var erosion: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var finalDensity: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var preliminarySurfaceLevel: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var ridges: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var temperature: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var vegetation: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
)

/**
 * Sets the continents density function for the NoiseRouter.
 */
fun NoiseRouter.continents(continents: DensityFunctionArgument) = run { this.continents = densityFunctionOrDouble(continents) }

/**
 * Sets the density function or double value for continents noise in the NoiseRouter.
 */
fun NoiseRouter.continents(continents: Double) = run { this.continents = densityFunctionOrDouble(continents) }

/**
 * Sets the depth of the NoiseRouter.
 */
fun NoiseRouter.depth(depth: DensityFunctionArgument) = run { this.depth = densityFunctionOrDouble(depth) }

/**
 * Sets the depth of the Noise Router.
 */
fun NoiseRouter.depth(depth: Double) = run { this.depth = densityFunctionOrDouble(depth) }

/**
 * Sets the erosion density function for the NoiseRouter.
 */
fun NoiseRouter.erosion(erosion: DensityFunctionArgument) = run { this.erosion = densityFunctionOrDouble(erosion) }

/**
 * Sets the erosion value for the NoiseRouter.
 * @return The updated instance of the NoiseRouter after setting the erosion value.
 */
fun NoiseRouter.erosion(erosion: Double) = run { this.erosion = densityFunctionOrDouble(erosion) }

/**
 * Sets the final density for the NoiseRouter.
 */
fun NoiseRouter.finalDensity(finalDensity: DensityFunctionArgument) = run { this.finalDensity = densityFunctionOrDouble(finalDensity) }

/**
 * Sets the final density for the NoiseRouter.
 * @return The modified NoiseRouter instance.
 */
fun NoiseRouter.finalDensity(finalDensity: Double) = run { this.finalDensity = densityFunctionOrDouble(finalDensity) }

/**
 * Sets the preliminary surface level for the NoiseRouter.
 */
fun NoiseRouter.preliminarySurfaceLevel(preliminarySurfaceLevel: DensityFunctionArgument) =
	run { this.preliminarySurfaceLevel = densityFunctionOrDouble(preliminarySurfaceLevel) }

/**
 * Sets the preliminary surface level value.
 */
fun NoiseRouter.preliminarySurfaceLevel(preliminarySurfaceLevel: Double) =
	run { this.preliminarySurfaceLevel = densityFunctionOrDouble(preliminarySurfaceLevel) }

/**
 * Sets the density function or double for generating ridges in the noise router.
 */
fun NoiseRouter.ridges(ridges: DensityFunctionArgument) = run { this.ridges = densityFunctionOrDouble(ridges) }

/**
 * Sets the value of `ridges` parameter in the NoiseRouter configuration.
 */
fun NoiseRouter.ridges(ridges: Double) = run { this.ridges = densityFunctionOrDouble(ridges) }

/**
 * Sets the temperature density function or double value for the NoiseRouter.
 */
fun NoiseRouter.temperature(temperature: DensityFunctionArgument) = run { this.temperature = densityFunctionOrDouble(temperature) }

/**
 * Sets the temperature of the NoiseRouter.
 */
fun NoiseRouter.temperature(temperature: Double) = run { this.temperature = densityFunctionOrDouble(temperature) }

/**
 * Sets the vegetation density for the Noise Router.
 */
fun NoiseRouter.vegetation(vegetation: DensityFunctionArgument) = run { this.vegetation = densityFunctionOrDouble(vegetation) }

/**
 * Sets the vegetation value of the NoiseRouter.
 */
fun NoiseRouter.vegetation(vegetation: Double) = run { this.vegetation = densityFunctionOrDouble(vegetation) }
