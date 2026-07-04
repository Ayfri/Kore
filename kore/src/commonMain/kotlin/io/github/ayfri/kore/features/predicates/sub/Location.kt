package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.generated.arguments.FluidOrTagArgument
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
data class Light(
	var light: IntRangeOrIntJson,
)

@Serializable
data class Location(
	var biomes: InlinableList<BiomeOrTagArgument>? = null,
	var block: Block? = null,
	var canSeeSky: Boolean? = null,
	var dimension: DimensionArgument? = null,
	var fluid: Fluid? = null,
	var light: Light? = null,
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

fun Location.fluids(vararg fluids: FluidOrTagArgument, state: Map<String, String>? = null, init: Fluid.() -> Unit) {
	fluid = Fluid(fluids.toList(), state).apply(init)
}

fun Location.light(value: IntRangeOrInt, init: Light.() -> Unit = {}) {
	light = Light(value).apply(init)
}

fun Location.light(min: Int, max: Int, init: Light.() -> Unit = {}) {
	light = Light((min..max).asRangeOrInt()).apply(init)
}

fun Location.light(value: Int, init: Light.() -> Unit = {}) {
	light = Light(value.asRangeOrInt()).apply(init)
}

fun Location.position(init: Position.() -> Unit = {}) {
	position = Position().apply(init)
}

fun Location.structures(vararg structures: StructureOrTagArgument) {
	this.structures = structures.toList()
}
