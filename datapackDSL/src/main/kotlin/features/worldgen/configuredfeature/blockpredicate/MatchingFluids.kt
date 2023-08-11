package features.worldgen.configuredfeature.blockpredicate

import arguments.types.FluidOrTagArgument
import serializers.InlinableList
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class MatchingFluids(
	var offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	var fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) : BlockPredicate()

fun matchingFluids(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) = MatchingFluids(offset, fluids)

fun matchingFluids(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	vararg fluids: FluidOrTagArgument,
) = MatchingFluids(offset, fluids.toList())

fun MutableList<BlockPredicate>.matchingFluids(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) {
	this += MatchingFluids(offset, fluids)
}

fun MutableList<BlockPredicate>.matchingFluids(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	vararg fluids: FluidOrTagArgument,
) {
	this += MatchingFluids(offset, fluids.toList())
}
