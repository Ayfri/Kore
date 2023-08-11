package features.worldgen.configuredfeature.blockpredicate

import kotlinx.serialization.Serializable

@Serializable
data class AnyOf(
	var predicates: List<BlockPredicate> = emptyList(),
) : BlockPredicate()

fun anyOf(predicates: List<BlockPredicate> = emptyList()) = AnyOf(predicates)
fun anyOf(vararg predicates: BlockPredicate) = AnyOf(predicates.toList())
fun anyOf(predicates: MutableList<BlockPredicate>.() -> Unit) = AnyOf(buildList(predicates))

fun MutableList<BlockPredicate>.anyOf(predicates: MutableList<BlockPredicate>.() -> Unit) {
	this += buildList(predicates)
}
