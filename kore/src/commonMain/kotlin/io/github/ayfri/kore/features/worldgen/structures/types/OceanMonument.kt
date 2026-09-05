package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places an ocean monument, the prismarine building guarded by elder guardians and hiding a block of gold.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class OceanMonument(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates an `ocean_monument` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     oceanMonument("my_monument") {
 *         biomes(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN)
 *
 *         spawnOverrides {
 *             monster(BoundingBox.PIECE) {
 *                 spawner(EntityTypes.GUARDIAN, weight = 20, minCount = 2, maxCount = 4)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.oceanMonument(
	fileName: String = "ocean_monument",
	init: OceanMonument.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, OceanMonument().apply(init))
