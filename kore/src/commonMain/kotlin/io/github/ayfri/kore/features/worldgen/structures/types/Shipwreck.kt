package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a shipwreck, either sunk on the ocean floor or run aground on a beach.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property isBeached Whether the ship is placed upright on the surface instead of sunk and tilted underwater.
 */
@Serializable
data class Shipwreck(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var isBeached: Boolean? = null,
) : StructureType()

/**
 * Creates a `shipwreck` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     shipwreck("my_beached_shipwreck") {
 *         biomes(Tags.Worldgen.Biome.IS_BEACH)
 *         isBeached = true
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.shipwreck(
	fileName: String = "shipwreck",
	init: Shipwreck.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, Shipwreck().apply(init))
