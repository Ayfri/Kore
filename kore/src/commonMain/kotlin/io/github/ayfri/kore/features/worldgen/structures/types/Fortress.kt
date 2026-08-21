package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a nether fortress, the nether brick bridge network carrying blaze spawners and nether wart.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class Fortress(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `fortress` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     fortress("my_fortress") {
 *         biomes(Tags.Worldgen.Biome.IS_NETHER)
 *
 *         spawnOverrides {
 *             monster(BoundingBox.PIECE) {
 *                 spawner(EntityTypes.BLAZE, weight = 10, minCount = 2, maxCount = 3)
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
fun StructuresScope.fortress(
	fileName: String = "fortress",
	init: Fortress.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, Fortress().apply(init))
