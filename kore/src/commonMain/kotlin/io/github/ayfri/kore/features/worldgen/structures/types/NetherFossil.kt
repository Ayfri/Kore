package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProviderScope
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a fossil made of bone blocks, the structure found in the soul sand valleys.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property height The Y level the fossil is generated at, drawn again for every instance.
 */
@Serializable
data class NetherFossil(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var height: HeightProvider = ConstantHeightProvider(Absolute(0)),
) : StructureType(), HeightProviderScope

/**
 * Creates a `nether_fossil` structure, configured in [init].
 *
 * The height provider builders are scoped to [init].
 *
 * ```kotlin
 * structures {
 *     netherFossil("my_fossil") {
 *         biomes(Biomes.SOUL_SAND_VALLEY)
 *         height = uniformHeightProvider(aboveBottom(32), belowTop(2))
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.netherFossil(
	fileName: String = "nether_fossil",
	init: NetherFossil.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, NetherFossil().apply(init))
