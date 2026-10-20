package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `monster_room` feature, which has no field.
 *
 * Builds a dungeon, a cobblestone room around a monster spawner with chests, in solid rock that opens onto a cave.
 * Vanilla places it underground in every Overworld biome.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#monster_room
 */
@Serializable
data object MonsterRoom : FeatureConfig()

/** Creates a `monster_room` configured feature, which has no configuration. */
fun ConfiguredFeatures.monsterRoom(fileName: String) = dp.configuredFeature(fileName, MonsterRoom)
