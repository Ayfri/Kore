package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.structures.types.StructureType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class Structure(
	@Transient
	override var fileName: String = "structure",
	var type: StructureType,
) : Generator("worldgen/structure") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

val DataPack.structuresBuilder get() = StructuresBuilder(this)
fun DataPack.structures(block: StructuresBuilder.() -> Unit = {}) = StructuresBuilder(this).apply(block)
