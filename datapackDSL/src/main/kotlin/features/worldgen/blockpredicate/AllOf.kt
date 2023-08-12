package features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	var predicates: List<BlockPredicate> = emptyList(),
) : BlockPredicate()

fun allOf(predicates: List<BlockPredicate> = emptyList()) = AllOf(predicates)
fun allOf(vararg predicates: BlockPredicate) = AllOf(predicates.toList())
fun allOf(predicates: MutableList<BlockPredicate>.() -> Unit) = AllOf(buildList(predicates))

fun MutableList<BlockPredicate>.allOf(predicates: MutableList<BlockPredicate>.() -> Unit) {
	this += buildList(predicates)
}
