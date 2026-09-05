package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Buries a single loot chest one block under the surface, at the center of the chunk it lands in.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class BuriedTreasure(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.UNDERGROUND_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `buried_treasure` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     buriedTreasure("my_treasure") {
 *         biomes(Tags.Worldgen.Biome.IS_BEACH)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.buriedTreasure(
	fileName: String = "buried_treasure",
	init: BuriedTreasure.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, BuriedTreasure().apply(init))
