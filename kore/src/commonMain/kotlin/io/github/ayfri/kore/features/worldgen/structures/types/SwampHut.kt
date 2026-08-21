package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a swamp hut, the witch hut on stilts, spawning a witch and a cat with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class SwampHut(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `swamp_hut` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     swampHut("my_swamp_hut") {
 *         biomes(Biomes.SWAMP)
 *
 *         spawnOverrides {
 *             monster(BoundingBox.PIECE) {
 *                 spawner(EntityTypes.WITCH, weight = 1, minCount = 1, maxCount = 1)
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
fun StructuresScope.swampHut(
	fileName: String = "swamp_hut",
	init: SwampHut.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, SwampHut().apply(init))
