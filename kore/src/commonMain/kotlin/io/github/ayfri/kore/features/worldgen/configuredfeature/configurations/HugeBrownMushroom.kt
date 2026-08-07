package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `huge_brown_mushroom` feature.
 *
 * [canPlaceOn] controls which ground blocks the mushroom may grow on.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_brown_mushroom
 */
@Serializable
data class HugeBrownMushroom(
	var canPlaceOn: BlockPredicate = True,
	var capProvider: BlockStateProvider = simpleStateProvider(),
	var stemProvider: BlockStateProvider = simpleStateProvider(),
	var foliageRadius: Int? = null,
) : FeatureConfig()

/** Creates a [HugeBrownMushroom] feature configuration. */
fun ConfiguredFeatures.hugeBrownMushroom(
	fileName: String,
	canPlaceOn: BlockPredicate = True,
	capProvider: BlockStateProvider = simpleStateProvider(),
	stemProvider: BlockStateProvider = simpleStateProvider(),
	foliageRadius: Int? = null,
	block: HugeBrownMushroom.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, HugeBrownMushroom(canPlaceOn, capProvider, stemProvider, foliageRadius).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
