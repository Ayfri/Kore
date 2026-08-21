package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places an ocean ruin, a small sunken building that may be surrounded by a cluster of smaller ones.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property biomeTemp The temperature variant, picking the templates and the loot the ruin is built from.
 * @property largeProbability Between `0.0` and `1.0`, the chance the main building is a large one.
 * @property clusterProbability Between `0.0` and `1.0`, the chance smaller ruins are scattered around it.
 */
@Serializable
data class OceanRuin(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var biomeTemp: BiomeTemperature = BiomeTemperature.COLD,
	var largeProbability: Float = 0.3f,
	var clusterProbability: Float = 0.9f,
) : StructureType()

/**
 * Creates an `ocean_ruin` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     oceanRuin("my_ocean_ruin") {
 *         biomes(Biomes.WARM_OCEAN)
 *         biomeTemp = BiomeTemperature.WARM
 *         largeProbability = 0.4f
 *         clusterProbability = 0.8f
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.oceanRuin(
	fileName: String = "ocean_ruin",
	init: OceanRuin.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, OceanRuin().apply(init))
