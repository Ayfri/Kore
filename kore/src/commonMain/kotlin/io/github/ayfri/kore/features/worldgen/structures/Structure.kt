package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.structures.types.StructureType
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A configured structure, a structure type paired with its configuration.
 *
 * It says which algorithm builds the structure, which biomes it may start in, and which generation step stamps it, but
 * not how far apart its instances stand: that is the job of a
 * [structure set][io.github.ayfri.kore.features.worldgen.structureset.StructureSet] referencing it.
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property type The structure type and its configuration, see [StructureType].
 */
@Serializable
data class Structure(
	@Transient
	override var fileName: String = "structure",
	var type: StructureType,
) : Generator("worldgen/structure") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

/**
 * Builder scope for declaring configured structures via [structures].
 *
 * Each structure type (e.g. [io.github.ayfri.kore.features.worldgen.structures.types.jigsaw],
 * [io.github.ayfri.kore.features.worldgen.structures.types.desertPyramid]) exposes a function on this class that
 * creates one [Structure] file, so a configured structure can only ever hold the single type it was declared with.
 */
data class StructuresScope(val dp: DataPack)

val DataPack.structuresBuilder get() = StructuresScope(this)

/**
 * Declares configured structures using Kore's DSL builder, one call per structure type.
 *
 * Every builder returns the [ConfiguredStructureArgument] pointing at the file it wrote, which is what
 * [io.github.ayfri.kore.features.worldgen.structureset.structure] takes.
 *
 * ```kotlin
 * dp.structures {
 *     desertPyramid("my_pyramid") {
 *         biomes(Biomes.DESERT, Biomes.BADLANDS)
 *         terrainAdaptation = TerrainAdaptation.BEARD_BOX
 *     }
 * }
 * ```
 *
 * Produces one `data/<namespace>/worldgen/structure/<fileName>.json` per call inside [init].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun DataPack.structures(init: StructuresScope.() -> Unit = {}) = structuresBuilder.apply(init)

/**
 * Creates a configured structure from an already built [type], adjusting the generator itself in [init].
 *
 * Prefer [structures] and its per-type functions; this entry point exists for the cases where the [Structure] itself
 * needs tweaking, such as overriding its namespace.
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun DataPack.structure(
	fileName: String = "structure",
	type: StructureType,
	init: Structure.() -> Unit = {},
): ConfiguredStructureArgument {
	val structure = Structure(fileName, type).apply(init)
	structures += structure
	return ConfiguredStructureArgument(structure.fileName, structure.namespace ?: name)
}
