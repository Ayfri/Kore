package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a stronghold, the underground maze of stone brick rooms holding the end portal.
 *
 * Its instances are spread by a
 * [concentric rings placement][io.github.ayfri.kore.features.worldgen.structureset.concentricRingsPlacement] rather
 * than a random spread, which is what makes the eyes of ender converge on rings around the world origin.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class Stronghold(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `stronghold` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     stronghold("my_stronghold") {
 *         biomes(Tags.Worldgen.Biome.IS_OVERWORLD)
 *         terrainAdaptation = TerrainAdaptation.BURY
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.stronghold(
	fileName: String = "stronghold",
	init: Stronghold.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, Stronghold().apply(init))
