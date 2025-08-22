package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.StructureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Weather.Companion.WeatherSerializer::class)
enum class Weather {
	CLEAR,
	RAINING,
	THUNDER;

	companion object {
		data object WeatherSerializer : LowercaseSerializer<Weather>(entries)
	}
}

@Serializable
data class Location(
	var biomes: InlinableList<BiomeOrTagArgument>? = null,
	var block: Block? = null,
	var canSeeSky: Boolean? = null,
	var dimension: DimensionArgument? = null,
	var fluid: Fluid? = null,
	var light: IntRangeOrIntJson? = null,
	var position: Position? = null,
	var smokey: Boolean? = null,
	var structures: InlinableList<StructureOrTagArgument>? = null,
	var weather: Weather? = null,
)

fun location(init: Location.() -> Unit = {}) = Location().apply(init)

fun Location.biomes(vararg biomes: BiomeOrTagArgument) {
	this.biomes = biomes.toList()
}

fun Location.block(block: Block.() -> Unit = {}) {
	this.block = Block().apply(block)
}

fun Location.position(init: Position.() -> Unit = {}) {
	position = Position().apply(init)
}

fun Location.structures(vararg structures: StructureOrTagArgument) {
	this.structures = structures.toList()
}
