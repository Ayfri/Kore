package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * A material rule placing a large vein of [oreBlock] mixed with [rawOreBlock], surrounded by [fillerBlock].
 *
 * @property density The chance, from `0.0` to `1.0`, that the vein replaces a block. `0.0` or lower replaces nothing.
 * @property fillerBlock The block filling the vein around the ore.
 * @property fillerGap Overrides [richness] when positive: [fillerBlock] is then always placed instead of [oreBlock].
 * @property oreBlock The ore of the vein.
 * @property rawOreBlock The block replacing the ore with the chance [rawOreChance].
 * @property rawOreChance The chance of [rawOreBlock] replacing [oreBlock], from `0.0` to `1.0`.
 * @property richness The chance of an ore block instead of a filler block inside the vein.
 */
@Serializable
data class OreVein(
	var density: DensityFunctionOrDouble,
	var fillerBlock: BlockState,
	var fillerGap: DensityFunctionOrDouble,
	var oreBlock: BlockState,
	var rawOreBlock: BlockState,
	var rawOreChance: Float,
	var richness: DensityFunctionOrDouble,
) : MaterialRule()

/**
 * Appends an ore vein material rule replacing a block with the chance [density], the other density functions being set
 * in [block], unset ones being `0.0`.
 *
 * ```
 * oreVein(Blocks.COPPER_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.GRANITE, DensityFunctions.Overworld.OreVein.COPPER_DENSITY) {
 *     richness(DensityFunctions.Overworld.OreVein.RICHNESS)
 * }
 * ```
 */
fun MaterialRulesScope.oreVein(
	oreBlock: BlockArgument,
	rawOreBlock: BlockArgument,
	fillerBlock: BlockArgument,
	density: DensityFunctionArgument,
	rawOreChance: Float = 0.02f,
	block: OreVein.() -> Unit = {},
) = oreVein(oreBlock, rawOreBlock, fillerBlock, densityFunctionOrDouble(density), rawOreChance, block)

/** Appends an ore vein material rule replacing a block with the constant chance [density], see the other [oreVein]. */
fun MaterialRulesScope.oreVein(
	oreBlock: BlockArgument,
	rawOreBlock: BlockArgument,
	fillerBlock: BlockArgument,
	density: Double,
	rawOreChance: Float = 0.02f,
	block: OreVein.() -> Unit = {},
) = oreVein(oreBlock, rawOreBlock, fillerBlock, densityFunctionOrDouble(density), rawOreChance, block)

private fun MaterialRulesScope.oreVein(
	oreBlock: BlockArgument,
	rawOreBlock: BlockArgument,
	fillerBlock: BlockArgument,
	density: DensityFunctionOrDouble,
	rawOreChance: Float,
	block: OreVein.() -> Unit,
) = apply {
	rules += OreVein(
		density = density,
		fillerBlock = blockState(fillerBlock),
		fillerGap = densityFunctionOrDouble(0.0),
		oreBlock = blockState(oreBlock),
		rawOreBlock = blockState(rawOreBlock),
		rawOreChance = rawOreChance,
		richness = densityFunctionOrDouble(0.0),
	).apply(block)
}

/** Sets the filler gap of the ore vein. */
fun OreVein.fillerGap(fillerGap: DensityFunctionArgument) = run { this.fillerGap = densityFunctionOrDouble(fillerGap) }

/** Sets the filler gap of the ore vein to a constant. */
fun OreVein.fillerGap(fillerGap: Double) = run { this.fillerGap = densityFunctionOrDouble(fillerGap) }

/** Sets the richness of the ore vein. */
fun OreVein.richness(richness: DensityFunctionArgument) = run { this.richness = densityFunctionOrDouble(richness) }

/** Sets the richness of the ore vein to a constant. */
fun OreVein.richness(richness: Double) = run { this.richness = densityFunctionOrDouble(richness) }
