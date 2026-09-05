package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a desert pyramid, the sandstone temple hiding a TNT-trapped treasure room under its floor.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class DesertPyramid(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `desert_pyramid` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     desertPyramid("my_pyramid") {
 *         biomes(Biomes.DESERT, Biomes.BADLANDS)
 *         terrainAdaptation = TerrainAdaptation.BEARD_BOX
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.desertPyramid(
	fileName: String = "desert_pyramid",
	init: DesertPyramid.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, DesertPyramid().apply(init))
