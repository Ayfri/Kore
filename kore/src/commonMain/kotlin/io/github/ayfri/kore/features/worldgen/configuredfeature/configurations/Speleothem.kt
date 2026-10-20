package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `speleothem` feature.
 *
 * Places a small pointed speleothem, such as a pointed dripstone, growing from a [baseBlock]. Vanilla places its
 * pointed dripstones and sulfur spikes with it, once a placement scan has found the floor or the ceiling.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#speleothem
 *
 * @property chanceOfTallerGeneration Chance, `0` to `1`, of a two-block tall speleothem, `0.2` in vanilla when `null`.
 * @property chanceOfDirectionalSpread Chance, `0` to `1`, that the speleothem spreads horizontally, `0.7` in vanilla
 * when `null`.
 * @property chanceOfSpreadRadius2 Chance, `0` to `1`, of spreading two blocks away, `0.5` in vanilla when `null`.
 * @property chanceOfSpreadRadius3 Chance, `0` to `1`, of spreading a third block away after two, `0.5` in vanilla when
 * `null`.
 * @property baseBlock The block state the speleothem grows from.
 * @property pointedBlock The block state of the pointed part of the speleothem.
 * @property replaceableBlocks A block, a block tag or a list of blocks the speleothem can replace.
 */
@Serializable
data class Speleothem(
	var chanceOfTallerGeneration: Double? = null,
	var chanceOfDirectionalSpread: Double? = null,
	var chanceOfSpreadRadius2: Double? = null,
	var chanceOfSpreadRadius3: Double? = null,
	var baseBlock: BlockState = blockStateStone(),
	var pointedBlock: BlockState = blockStateStone(),
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

/**
 * Creates a `speleothem` configured feature with the given spread chances, configured in [block].
 *
 * A `null` chance leaves the field out, so the game uses its default.
 *
 * ```kotlin
 * speleothem("pointed_dripstone", chanceOfTallerGeneration = 0.2) {
 *     baseBlock = blockState(Blocks.DRIPSTONE_BLOCK)
 *     pointedBlock = blockState(Blocks.POINTED_DRIPSTONE)
 *     replaceableBlocks = listOf(Tags.Block.DRIPSTONE_REPLACEABLE_BLOCKS)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#speleothem
 */
fun ConfiguredFeatures.speleothem(
	fileName: String,
	chanceOfTallerGeneration: Double? = null,
	chanceOfDirectionalSpread: Double? = null,
	chanceOfSpreadRadius2: Double? = null,
	chanceOfSpreadRadius3: Double? = null,
	block: Speleothem.() -> Unit = {},
) = dp.configuredFeature(
	fileName,
	Speleothem(chanceOfTallerGeneration, chanceOfDirectionalSpread, chanceOfSpreadRadius2, chanceOfSpreadRadius3).apply(block),
)
