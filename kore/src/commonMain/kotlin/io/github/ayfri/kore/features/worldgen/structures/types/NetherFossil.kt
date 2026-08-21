package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProviderScope
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a fossil made of bone blocks, the structure used in the soul sand valleys.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property height The Y level the fossil is generated at.
 */
@Serializable
data class NetherFossil(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
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
 *         height = uniformHeightProvider(aboveBottom(32), belowTop(2))
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<filename>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresBuilder.netherFossil(
	filename: String = "nether_fossil",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	init: NetherFossil.() -> Unit = {},
): StructureArgument {
	val netherFossil = NetherFossil(step = step).apply(init)
	dp.structures += Structure(filename, netherFossil)
	return StructureArgument(filename, netherFossil.namespace ?: dp.name)
}
