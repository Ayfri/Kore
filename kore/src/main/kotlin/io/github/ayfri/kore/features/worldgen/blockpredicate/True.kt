package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

@Serializable
data object True : BlockPredicate()

fun MutableList<BlockPredicate>.trueBlockPredicate() {
	this += True
}
