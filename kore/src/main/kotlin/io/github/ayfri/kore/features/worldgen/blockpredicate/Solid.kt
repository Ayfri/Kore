package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

@Serializable
data object Solid : BlockPredicate()

fun MutableList<BlockPredicate>.solid() {
	this += Solid
}
