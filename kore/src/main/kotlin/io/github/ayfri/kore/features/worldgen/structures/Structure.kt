package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.structures.types.StructureType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

/**
 * Data-driven configured structure.
 *
 * Encapsulates a structure type configuration (biome tags, spawn overrides, generation step,
 * terrain adaptation, etc.). Referenced by structure sets to place instances in the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class Structure(
	@Transient
	override var fileName: String = "structure",
	var type: StructureType,
) : Generator("worldgen/structure") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

/** Entry point to declare configured structures via DSL. */
val DataPack.structuresBuilder get() = StructuresBuilder(this)

/**
 * Entry point to declare configured structures via DSL.
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Structure#Data-driven
 */
fun DataPack.structures(block: StructuresBuilder.() -> Unit = {}) = StructuresBuilder(this).apply(block)
