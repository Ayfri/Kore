package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * The aquifers of a [NoiseSettings]: the local water and lava pockets carved in the terrain.
 *
 * Without aquifers, the terrain is only flooded up to the sea level.
 *
 * @property barrier The noise separating the fluid pockets from the stone.
 * @property exclusion The density below which no aquifer generates.
 * @property fluidLevelFloodedness How often the aquifers are filled.
 * @property fluidLevelSpread How much the fluid level of the aquifers varies.
 * @property lava Whether an aquifer holds lava instead of water.
 * @property surfaceLevel The surface height the aquifers are placed relative to.
 */
@Serializable
data class Aquifers(
	var barrier: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var exclusion: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var fluidLevelFloodedness: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var fluidLevelSpread: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var lava: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var surfaceLevel: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
)

/**
 * Sets the aquifers of the noise settings.
 *
 * ```
 * aquifers {
 *     barrier(DensityFunctions.Overworld.BASE_3D_NOISE)
 *     surfaceLevel(DensityFunctions.Overworld.PRELIMINARY_SURFACE_LEVEL)
 * }
 * ```
 */
fun NoiseSettings.aquifers(block: Aquifers.() -> Unit = {}) = Aquifers().apply(block).also { aquifers = it }

/** Sets the barrier of the aquifers. */
fun Aquifers.barrier(barrier: DensityFunctionArgument) = run { this.barrier = densityFunctionOrDouble(barrier) }

/** Sets the barrier of the aquifers to a constant. */
fun Aquifers.barrier(barrier: Double) = run { this.barrier = densityFunctionOrDouble(barrier) }

/** Sets the exclusion of the aquifers. */
fun Aquifers.exclusion(exclusion: DensityFunctionArgument) = run { this.exclusion = densityFunctionOrDouble(exclusion) }

/** Sets the exclusion of the aquifers to a constant. */
fun Aquifers.exclusion(exclusion: Double) = run { this.exclusion = densityFunctionOrDouble(exclusion) }

/** Sets the fluid level floodedness of the aquifers. */
fun Aquifers.fluidLevelFloodedness(fluidLevelFloodedness: DensityFunctionArgument) =
	run { this.fluidLevelFloodedness = densityFunctionOrDouble(fluidLevelFloodedness) }

/** Sets the fluid level floodedness of the aquifers to a constant. */
fun Aquifers.fluidLevelFloodedness(fluidLevelFloodedness: Double) =
	run { this.fluidLevelFloodedness = densityFunctionOrDouble(fluidLevelFloodedness) }

/** Sets the fluid level spread of the aquifers. */
fun Aquifers.fluidLevelSpread(fluidLevelSpread: DensityFunctionArgument) =
	run { this.fluidLevelSpread = densityFunctionOrDouble(fluidLevelSpread) }

/** Sets the fluid level spread of the aquifers to a constant. */
fun Aquifers.fluidLevelSpread(fluidLevelSpread: Double) = run { this.fluidLevelSpread = densityFunctionOrDouble(fluidLevelSpread) }

/** Sets the lava of the aquifers. */
fun Aquifers.lava(lava: DensityFunctionArgument) = run { this.lava = densityFunctionOrDouble(lava) }

/** Sets the lava of the aquifers to a constant. */
fun Aquifers.lava(lava: Double) = run { this.lava = densityFunctionOrDouble(lava) }

/** Sets the surface level of the aquifers. */
fun Aquifers.surfaceLevel(surfaceLevel: DensityFunctionArgument) = run { this.surfaceLevel = densityFunctionOrDouble(surfaceLevel) }

/** Sets the surface level of the aquifers to a constant. */
fun Aquifers.surfaceLevel(surfaceLevel: Double) = run { this.surfaceLevel = densityFunctionOrDouble(surfaceLevel) }
