package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureSetArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

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
