package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class Offset(
	var offset: TripleAsArray<Int, Int, Int>? = null,
) : BlockPredicate()

fun offset(x: Int, y: Int, z: Int) = Offset(Triple(x, y, z))

fun MutableList<BlockPredicate>.offset(x: Int, y: Int, z: Int) {
	this += Offset(Triple(x, y, z))
}
