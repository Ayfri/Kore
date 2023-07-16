package features.worldgen.dimension.generator

import arguments.types.resources.BiomeArgument
import arguments.types.resources.BlockArgument
import arguments.types.resources.StructureSetArgument
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class FlatGeneratorSettings(
	var biome: BiomeArgument,
	var lakes: Boolean? = null,
	var features: Boolean? = null,
	var layers: List<Layer> = emptyList(),
	var structureOverrides: InlinableList<StructureSetArgument> = emptyList(),
)

@Serializable
data class Layer(
	var block: BlockArgument,
	var height: Int,
)

fun FlatGeneratorSettings.layer(block: BlockArgument, height: Int) = Layer(block, height).also { layers += it }

fun FlatGeneratorSettings.structureOverrides(vararg structures: StructureSetArgument) {
	structureOverrides += structures
}

fun FlatGeneratorSettings.structureOverrides(block: MutableList<StructureSetArgument>.() -> Unit) {
	structureOverrides += buildList(block)
}
