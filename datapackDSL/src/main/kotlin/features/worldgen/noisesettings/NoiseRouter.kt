package features.worldgen.noisesettings

import arguments.types.resources.DensityFunctionArgument
import kotlinx.serialization.Serializable

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

fun NoiseRouter.barrier(barrier: DensityFunctionArgument) = apply { this.barrier = densityFunctionOrDouble(barrier) }
fun NoiseRouter.barrier(barrier: Double) = apply { this.barrier = densityFunctionOrDouble(barrier) }

fun NoiseRouter.fluidLevelFloodedness(fluidLevelFloodedness: DensityFunctionArgument) =
	apply { this.fluidLevelFloodedness = densityFunctionOrDouble(fluidLevelFloodedness) }

fun NoiseRouter.fluidLevelFloodedness(fluidLevelFloodedness: Double) =
	apply { this.fluidLevelFloodedness = densityFunctionOrDouble(fluidLevelFloodedness) }

fun NoiseRouter.fluidLevelSpread(fluidLevelSpread: DensityFunctionArgument) =
	apply { this.fluidLevelSpread = densityFunctionOrDouble(fluidLevelSpread) }

fun NoiseRouter.fluidLevelSpread(fluidLevelSpread: Double) = apply { this.fluidLevelSpread = densityFunctionOrDouble(fluidLevelSpread) }

fun NoiseRouter.lava(lava: DensityFunctionArgument) = apply { this.lava = densityFunctionOrDouble(lava) }
fun NoiseRouter.lava(lava: Double) = apply { this.lava = densityFunctionOrDouble(lava) }

fun NoiseRouter.temperature(temperature: DensityFunctionArgument) = apply { this.temperature = densityFunctionOrDouble(temperature) }
fun NoiseRouter.temperature(temperature: Double) = apply { this.temperature = densityFunctionOrDouble(temperature) }

fun NoiseRouter.vegetation(vegetation: DensityFunctionArgument) = apply { this.vegetation = densityFunctionOrDouble(vegetation) }
fun NoiseRouter.vegetation(vegetation: Double) = apply { this.vegetation = densityFunctionOrDouble(vegetation) }

fun NoiseRouter.continents(continents: DensityFunctionArgument) = apply { this.continents = densityFunctionOrDouble(continents) }
fun NoiseRouter.continents(continents: Double) = apply { this.continents = densityFunctionOrDouble(continents) }

fun NoiseRouter.erosion(erosion: DensityFunctionArgument) = apply { this.erosion = densityFunctionOrDouble(erosion) }
fun NoiseRouter.erosion(erosion: Double) = apply { this.erosion = densityFunctionOrDouble(erosion) }

fun NoiseRouter.depth(depth: DensityFunctionArgument) = apply { this.depth = densityFunctionOrDouble(depth) }
fun NoiseRouter.depth(depth: Double) = apply { this.depth = densityFunctionOrDouble(depth) }

fun NoiseRouter.ridges(ridges: DensityFunctionArgument) = apply { this.ridges = densityFunctionOrDouble(ridges) }
fun NoiseRouter.ridges(ridges: Double) = apply { this.ridges = densityFunctionOrDouble(ridges) }

fun NoiseRouter.initialDensityWithoutJaggedness(initialDensityWithoutJaggedness: DensityFunctionArgument) =
	apply { this.initialDensityWithoutJaggedness = densityFunctionOrDouble(initialDensityWithoutJaggedness) }

fun NoiseRouter.initialDensityWithoutJaggedness(initialDensityWithoutJaggedness: Double) =
	apply { this.initialDensityWithoutJaggedness = densityFunctionOrDouble(initialDensityWithoutJaggedness) }

fun NoiseRouter.finalDensity(finalDensity: DensityFunctionArgument) = apply { this.finalDensity = densityFunctionOrDouble(finalDensity) }
fun NoiseRouter.finalDensity(finalDensity: Double) = apply { this.finalDensity = densityFunctionOrDouble(finalDensity) }

fun NoiseRouter.veinToggle(veinToggle: DensityFunctionArgument) = apply { this.veinToggle = densityFunctionOrDouble(veinToggle) }
fun NoiseRouter.veinToggle(veinToggle: Double) = apply { this.veinToggle = densityFunctionOrDouble(veinToggle) }

fun NoiseRouter.veinRidged(veinRidged: DensityFunctionArgument) = apply { this.veinRidged = densityFunctionOrDouble(veinRidged) }
fun NoiseRouter.veinRidged(veinRidged: Double) = apply { this.veinRidged = densityFunctionOrDouble(veinRidged) }

fun NoiseRouter.veinGap(veinGap: DensityFunctionArgument) = apply { this.veinGap = densityFunctionOrDouble(veinGap) }
fun NoiseRouter.veinGap(veinGap: Double) = apply { this.veinGap = densityFunctionOrDouble(veinGap) }
