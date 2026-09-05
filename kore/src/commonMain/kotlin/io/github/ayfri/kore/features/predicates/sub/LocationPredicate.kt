package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.generated.arguments.FluidOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.ConfiguredStructureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Matches the light level of a position, computed as `max(sky - darkening, block)`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class LightPredicate(
	var light: IntRangeOrIntJson,
)

/**
 * Matches a position in the world: its biome, dimension, structure, the block and fluid there, and the light it gets.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class LocationPredicate(
	var biomes: InlinableList<BiomeOrTagArgument>? = null,
	var block: BlockPredicate? = null,
	/** Whether the location has the maximum possible level of sky light. */
	var canSeeSky: Boolean? = null,
	var dimension: DimensionArgument? = null,
	var fluid: FluidPredicate? = null,
	var light: LightPredicate? = null,
	var position: PositionPredicate? = null,
	/** Whether the block is at most 5 blocks above a campfire or a soul campfire. */
	var smokey: Boolean? = null,
	var structures: InlinableList<ConfiguredStructureOrTagArgument>? = null,
)

/** Creates a [LocationPredicate]. */
fun locationPredicate(init: LocationPredicate.() -> Unit = {}) = LocationPredicate().apply(init)

/** Matches any of [biomes]. */
fun LocationPredicate.biomes(vararg biomes: BiomeOrTagArgument) {
	this.biomes = biomes.toList()
}

/** Matches the block at this location against any of [blocks]. */
fun LocationPredicate.block(vararg blocks: BlockOrTagArgument, init: BlockPredicate.() -> Unit = {}) {
	block = blockPredicate(*blocks, init = init)
}

/** Matches the fluid at this location against any of [fluids]. */
fun LocationPredicate.fluids(vararg fluids: FluidOrTagArgument, state: Map<String, String>? = null, init: FluidPredicate.() -> Unit = {}) {
	fluid = FluidPredicate(fluids.toList().ifEmpty { null }, state).apply(init)
}

/** Matches a light level within [value]. */
fun LocationPredicate.light(value: IntRangeOrInt) {
	light = LightPredicate(value)
}

/** Matches a light level between [min] and [max]. */
fun LocationPredicate.light(min: Int, max: Int) {
	light = LightPredicate((min..max).asRangeOrInt())
}

/** Matches an exact light level. */
fun LocationPredicate.light(value: Int) {
	light = LightPredicate(value.asRangeOrInt())
}

/** Matches the coordinates of this location. */
fun LocationPredicate.position(init: PositionPredicate.() -> Unit = {}) {
	position = PositionPredicate().apply(init)
}

/** Matches any of [structures]. */
fun LocationPredicate.structures(vararg structures: ConfiguredStructureOrTagArgument) {
	this.structures = structures.toList()
}
