package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.StructureOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Location(
	var biomes: InlinableList<BiomeOrTagArgument>? = null,
	var block: Block? = null,
	var dimension: DimensionArgument? = null,
	var fluid: Fluid? = null,
	var light: IntRangeOrIntJson? = null,
	var position: Position? = null,
	var smokey: Boolean? = null,
	var structures: InlinableList<StructureOrTagArgument>? = null,
)

fun location(init: Location.() -> Unit = {}) = Location().apply(init)

fun Location.biomes(vararg biomes: BiomeOrTagArgument) {
	this.biomes = biomes.toList()
}

fun Location.structures(vararg structures: StructureOrTagArgument) {
	this.structures = structures.toList()
}
