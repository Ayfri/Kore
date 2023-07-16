package features.worldgen.dimension.generator

import arguments.Argument
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class FlatGeneratorSettings(
	var biome: Argument.Biome,
	var lakes: Boolean? = null,
	var features: Boolean? = null,
	var layers: List<Layer> = emptyList(),
	var structureOverrides: InlinableList<Argument.StructureSet> = emptyList(),
)

@Serializable
data class Layer(
	var block: Argument.Block,
	var height: Int,
)

fun FlatGeneratorSettings.layer(block: Argument.Block, height: Int) = Layer(block, height).also { layers += it }

fun FlatGeneratorSettings.structureOverrides(vararg structures: Argument.StructureSet) {
	structureOverrides += structures
}

fun FlatGeneratorSettings.structureOverrides(block: MutableList<Argument.StructureSet>.() -> Unit) {
	structureOverrides += buildList(block)
}
