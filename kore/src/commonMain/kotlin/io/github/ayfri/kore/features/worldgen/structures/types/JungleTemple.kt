package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a jungle temple, the mossy cobblestone pyramid holding the arrow trap and the lever puzzle.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class JungleTemple(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `jungle_temple` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     jungleTemple("my_jungle_temple") {
 *         biomes(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.jungleTemple(
	fileName: String = "jungle_temple",
	init: JungleTemple.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, JungleTemple().apply(init))
