package features.worldgen.blockpredicate

import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class InsideWorldBounds(
	var offset: TripleAsArray<Int, Int, Int>? = null,
) : BlockPredicate()

fun insideWorldBounds(offset: TripleAsArray<Int, Int, Int>? = null) = InsideWorldBounds(offset)

fun MutableList<BlockPredicate>.insideWorldBounds(offset: TripleAsArray<Int, Int, Int>? = null) {
	this += InsideWorldBounds(offset)
}
