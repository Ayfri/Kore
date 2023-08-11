package features.worldgen.configuredfeature.blockpredicate

import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class InsideWorldBounds(
	var offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
) : BlockPredicate()

fun insideWorldBounds(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0)) = InsideWorldBounds(offset)

fun MutableList<BlockPredicate>.insideWorldBounds(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0)) {
	this += InsideWorldBounds(offset)
}
