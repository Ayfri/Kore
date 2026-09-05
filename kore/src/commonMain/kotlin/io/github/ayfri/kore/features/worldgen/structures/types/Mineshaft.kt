package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Carves a mineshaft, the branching network of wooden tunnels holding rails, cobwebs and cave spider spawners.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property mineshaftType The variant, changing the wood used and how deep the tunnels run.
 */
@Serializable
data class Mineshaft(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.UNDERGROUND_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var mineshaftType: MineshaftType = MineshaftType.NORMAL,
) : StructureType()

/**
 * Creates a `mineshaft` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     mineshaft("badlands_mineshaft") {
 *         biomes(Tags.Worldgen.Biome.IS_BADLANDS)
 *         mineshaftType = MineshaftType.MESA
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.mineshaft(
	fileName: String = "mineshaft",
	init: Mineshaft.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, Mineshaft().apply(init))
