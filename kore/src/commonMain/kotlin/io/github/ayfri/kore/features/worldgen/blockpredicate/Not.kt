package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

@Serializable
data class Not(
	var predicate: BlockPredicate,
) : BlockPredicate()

fun not(vararg predicates: BlockPredicate) = when (predicates.size) {
	1 -> Not(predicates.first())
	else -> Not(allOf(*predicates))
}

fun not(predicate: MutableList<BlockPredicate>.() -> Unit) {
	val list = buildList(predicate)
	when (list.size) {
		1 -> Not(list.first())
		else -> Not(allOf(list))
	}
}

fun MutableList<BlockPredicate>.not(vararg predicates: BlockPredicate) = when (predicates.size) {
	1 -> this += Not(predicates.first())
	else -> this += Not(allOf(*predicates))
}

fun MutableList<BlockPredicate>.not(predicate: MutableList<BlockPredicate>.() -> Unit) {
	val list = buildList(predicate)
	when (list.size) {
		1 -> this += Not(list.first())
		else -> this += Not(allOf(list))
	}
}
