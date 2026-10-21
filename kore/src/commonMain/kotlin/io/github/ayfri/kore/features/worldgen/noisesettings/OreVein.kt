package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * An ore vein of a [NoiseSettings]: a large vein of [oreBlock] mixed with [rawOreBlock], surrounded by [fillerBlock].
 *
 * @property density Where the vein generates, positive inside the vein.
 * @property fillerBlock The block filling the vein around the ore.
 * @property fillerGap The noise below which the filler is skipped.
 * @property oreBlock The ore of the vein.
 * @property rawOreBlock The block replacing the ore with the chance [rawOreChance].
 * @property rawOreChance The chance of [rawOreBlock] replacing [oreBlock], from `0.0` to `1.0`.
 * @property richness The chance of an ore block instead of a filler block inside the vein.
 */
@Serializable
data class OreVein(
	var density: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var fillerBlock: BlockState,
	var fillerGap: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var oreBlock: BlockState,
	var rawOreBlock: BlockState,
	var rawOreChance: Float = 0.02f,
	var richness: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
)

/**
 * Adds an ore vein to the noise settings.
 *
 * ```
 * oreVein(Blocks.COPPER_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.GRANITE) {
 *     density(DensityFunctions.Overworld.OreVein.COPPER_DENSITY)
 * }
 * ```
 */
fun NoiseSettings.oreVein(
	oreBlock: BlockArgument,
	rawOreBlock: BlockArgument,
	fillerBlock: BlockArgument,
	rawOreChance: Float = 0.02f,
	block: OreVein.() -> Unit = {},
) = OreVein(
	fillerBlock = blockState(fillerBlock),
	oreBlock = blockState(oreBlock),
	rawOreBlock = blockState(rawOreBlock),
	rawOreChance = rawOreChance,
).apply(block).also { oreVeins = (oreVeins ?: emptyList()) + it }

/** Sets the density of the ore vein. */
fun OreVein.density(density: DensityFunctionArgument) = run { this.density = densityFunctionOrDouble(density) }

/** Sets the density of the ore vein to a constant. */
fun OreVein.density(density: Double) = run { this.density = densityFunctionOrDouble(density) }

/** Sets the filler gap of the ore vein. */
fun OreVein.fillerGap(fillerGap: DensityFunctionArgument) = run { this.fillerGap = densityFunctionOrDouble(fillerGap) }

/** Sets the filler gap of the ore vein to a constant. */
fun OreVein.fillerGap(fillerGap: Double) = run { this.fillerGap = densityFunctionOrDouble(fillerGap) }

/** Sets the richness of the ore vein. */
fun OreVein.richness(richness: DensityFunctionArgument) = run { this.richness = densityFunctionOrDouble(richness) }

/** Sets the richness of the ore vein to a constant. */
fun OreVein.richness(richness: Double) = run { this.richness = densityFunctionOrDouble(richness) }
