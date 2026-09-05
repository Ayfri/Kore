package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a woodland mansion, the dark oak manor whose rooms are drawn from a hardcoded list and which houses the
 * evokers and the vindicators.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class WoodlandMansion(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

/**
 * Creates a `woodland_mansion` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     woodlandMansion("my_mansion") {
 *         biomes(Biomes.DARK_FOREST)
 *
 *         spawnOverrides {
 *             monster(BoundingBox.PIECE)
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
fun StructuresScope.woodlandMansion(
	fileName: String = "woodland_mansion",
	init: WoodlandMansion.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, WoodlandMansion().apply(init))
