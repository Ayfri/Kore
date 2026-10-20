package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `random_boolean_selector` feature.
 *
 * Runs [featureTrue] or [featureFalse], each with the same chance. Vanilla uses it to alternate between the two clay
 * pools of the lush caves and between the red and brown huge mushrooms of the mushroom fields.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_boolean_selector
 *
 * @property featureFalse The placed feature run on a false pick.
 * @property featureTrue The placed feature run on a true pick.
 */
@Serializable
data class RandomBooleanSelector(
	var featureFalse: PlacedFeatureArgument,
	var featureTrue: PlacedFeatureArgument,
) : FeatureConfig()

/**
 * Creates a `random_boolean_selector` configured feature running [featureTrue] or [featureFalse] at random.
 *
 * ```kotlin
 * randomBooleanSelector(
 *     "oak_or_birch",
 *     featureFalse = PlacedFeatures.OAK_CHECKED,
 *     featureTrue = PlacedFeatures.BIRCH_CHECKED,
 * )
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_boolean_selector
 */
fun ConfiguredFeatures.randomBooleanSelector(
	fileName: String,
	featureFalse: PlacedFeatureArgument,
	featureTrue: PlacedFeatureArgument,
) = dp.configuredFeature(fileName, RandomBooleanSelector(featureFalse, featureTrue))
