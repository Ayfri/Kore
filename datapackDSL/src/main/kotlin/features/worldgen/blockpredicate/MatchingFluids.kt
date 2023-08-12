package features.worldgen.blockpredicate

import arguments.types.FluidOrTagArgument
import serializers.InlinableList
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class MatchingFluids(
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) : BlockPredicate()

fun matchingFluids(
	offset: TripleAsArray<Int, Int, Int>? = null,
	fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) = MatchingFluids(offset, fluids)

fun matchingFluids(
	offset: TripleAsArray<Int, Int, Int>? = null,
	vararg fluids: FluidOrTagArgument,
) = MatchingFluids(offset, fluids.toList())

fun MutableList<BlockPredicate>.matchingFluids(
	offset: TripleAsArray<Int, Int, Int>? = null,
	fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) {
	this += MatchingFluids(offset, fluids)
}

fun MutableList<BlockPredicate>.matchingFluids(
	offset: TripleAsArray<Int, Int, Int>? = null,
	vararg fluids: FluidOrTagArgument,
) {
	this += MatchingFluids(offset, fluids.toList())
}
