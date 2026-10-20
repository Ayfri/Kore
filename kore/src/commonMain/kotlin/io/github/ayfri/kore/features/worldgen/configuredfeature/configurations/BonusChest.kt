package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `bonus_chest` feature, which has no field.
 *
 * Places the bonus chest next to the world spawn, filled from the `minecraft:chests/spawn_bonus_chest` loot table.
 * No vanilla data references it, the game places it itself when a world is created with the bonus chest enabled.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#bonus_chest
 */
@Serializable
data object BonusChest : FeatureConfig()

/** Creates a `bonus_chest` configured feature. */
fun ConfiguredFeatures.bonusChest(fileName: String) = dp.configuredFeature(fileName, BonusChest)
