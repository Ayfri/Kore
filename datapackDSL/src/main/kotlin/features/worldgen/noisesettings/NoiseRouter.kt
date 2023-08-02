package features.worldgen.noisesettings

import arguments.types.resources.worldgen.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * A class representing a NoiseRouter.
 *
 * This class is used for configuring various density functions or doubles used in noise generation.
 *
 * @property barrier The density function or double for barrier.
 * @property fluidLevelFloodedness The density function or double for fluid level floodedness.
 * @property fluidLevelSpread The density function or double for fluid level spread.
 * @property lava The density function or double for lava.
 * @property temperature The density function or double for temperature.
 * @property vegetation The density function or double for vegetation.
 * @property continents The density function or double for continents.
 * @property erosion The density function or double for erosion.
 * @property depth The density function or double for depth.
 * @property ridges The density function or double for ridges.
 * @property initialDensityWithoutJaggedness The density function or double for initial density without jaggedness.
 * @property finalDensity The density function or double for final density.
 * @property veinToggle The density function or double for vein toggle.
 * @property veinRidged The density function or double for vein ridged.
 * @property veinGap The density function or double for vein gap.
 */
@Serializable
data class NoiseRouter(
	var barrier: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var fluidLevelFloodedness: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var fluidLevelSpread: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var lava: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var temperature: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var vegetation: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var continents: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var erosion: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var depth: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var ridges: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var initialDensityWithoutJaggedness: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var finalDensity: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var veinToggle: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var veinRidged: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var veinGap: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
)

/**
 * Sets the barrier for the NoiseRouter.
 */
fun NoiseRouter.barrier(barrier: DensityFunctionArgument) = run { this.barrier = densityFunctionOrDouble(barrier) }

/**
 * Sets the barrier value for the NoiseRouter.
 */
fun NoiseRouter.barrier(barrier: Double) = run { this.barrier = densityFunctionOrDouble(barrier) }

/**
 * Sets the fluid level floodedness for the NoiseRouter.
 */
fun NoiseRouter.fluidLevelFloodedness(fluidLevelFloodedness: DensityFunctionArgument) =
	run { this.fluidLevelFloodedness = densityFunctionOrDouble(fluidLevelFloodedness) }

/**
 * Sets the fluid level floodedness of the NoiseRouter.
 */
fun NoiseRouter.fluidLevelFloodedness(fluidLevelFloodedness: Double) =
	run { this.fluidLevelFloodedness = densityFunctionOrDouble(fluidLevelFloodedness) }

/**
 * Sets the fluid level spread of the NoiseRouter.
 */
fun NoiseRouter.fluidLevelSpread(fluidLevelSpread: DensityFunctionArgument) =
	run { this.fluidLevelSpread = densityFunctionOrDouble(fluidLevelSpread) }

/**
 * Sets the fluid level spread for the NoiseRouter instance.
 */
fun NoiseRouter.fluidLevelSpread(fluidLevelSpread: Double) = run { this.fluidLevelSpread = densityFunctionOrDouble(fluidLevelSpread) }

/**
 * Sets the lava property of the NoiseRouter.
 */
fun NoiseRouter.lava(lava: DensityFunctionArgument) = run { this.lava = densityFunctionOrDouble(lava) }

/**
 * Sets the lava density value for NoiseRouter.
 */
fun NoiseRouter.lava(lava: Double) = run { this.lava = densityFunctionOrDouble(lava) }

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

/**
 * Sets the continents density function for the NoiseRouter.
 */
fun NoiseRouter.continents(continents: DensityFunctionArgument) = run { this.continents = densityFunctionOrDouble(continents) }

/**
 * Sets the density function or double value for continents noise in the NoiseRouter.
 */
fun NoiseRouter.continents(continents: Double) = run { this.continents = densityFunctionOrDouble(continents) }

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
 * Sets the depth of the NoiseRouter.
 */
fun NoiseRouter.depth(depth: DensityFunctionArgument) = run { this.depth = densityFunctionOrDouble(depth) }

/**
 * Sets the depth of the Noise Router.
 */
fun NoiseRouter.depth(depth: Double) = run { this.depth = densityFunctionOrDouble(depth) }

/**
 * Sets the density function or double for generating ridges in the noise router.
 */
fun NoiseRouter.ridges(ridges: DensityFunctionArgument) = run { this.ridges = densityFunctionOrDouble(ridges) }

/**
 * Sets the value of `ridges` parameter in the NoiseRouter configuration.
 */
fun NoiseRouter.ridges(ridges: Double) = run { this.ridges = densityFunctionOrDouble(ridges) }

/**
 * Sets the initial density without jaggedness for the NoiseRouter.
 */
fun NoiseRouter.initialDensityWithoutJaggedness(initialDensityWithoutJaggedness: DensityFunctionArgument) =
	run { this.initialDensityWithoutJaggedness = densityFunctionOrDouble(initialDensityWithoutJaggedness) }

/**
 * Sets the initial density without jaggedness value.
 */
fun NoiseRouter.initialDensityWithoutJaggedness(initialDensityWithoutJaggedness: Double) =
	run { this.initialDensityWithoutJaggedness = densityFunctionOrDouble(initialDensityWithoutJaggedness) }

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
 * Toggles the vein density function for the current NoiseRouter instance.
 */
fun NoiseRouter.veinToggle(veinToggle: DensityFunctionArgument) = run { this.veinToggle = densityFunctionOrDouble(veinToggle) }

/**
 * Sets the vein toggle value for the NoiseRouter.
 */
fun NoiseRouter.veinToggle(veinToggle: Double) = run { this.veinToggle = densityFunctionOrDouble(veinToggle) }

/**
 * Sets the veinRidged parameter of the NoiseRouter.
 */
fun NoiseRouter.veinRidged(veinRidged: DensityFunctionArgument) = run { this.veinRidged = densityFunctionOrDouble(veinRidged) }

/**
 * Sets the vein ridged parameter of the NoiseRouter.
 */
fun NoiseRouter.veinRidged(veinRidged: Double) = run { this.veinRidged = densityFunctionOrDouble(veinRidged) }

/**
 * Sets the vein gap parameter of the NoiseRouter.
 */
fun NoiseRouter.veinGap(veinGap: DensityFunctionArgument) = run { this.veinGap = densityFunctionOrDouble(veinGap) }

/**
 * Sets the vein gap for the NoiseRouter.
 */
fun NoiseRouter.veinGap(veinGap: Double) = run { this.veinGap = densityFunctionOrDouble(veinGap) }
