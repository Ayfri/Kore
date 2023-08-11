package features.worldgen.configuredfeature.blockpredicate

import kotlinx.serialization.Serializable

@Serializable
data object Replaceable : BlockPredicate()

fun MutableList<BlockPredicate>.replaceable() {
	this += Replaceable
}
