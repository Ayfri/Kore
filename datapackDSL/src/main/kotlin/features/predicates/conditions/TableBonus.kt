package features.predicates.conditions

import arguments.Argument
import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class TableBonus(
	var enchantment: Argument.Enchantment,
	var chances: List<Float>? = null,
) : PredicateCondition

fun Predicate.tableBonus(enchantment: Argument.Enchantment, chances: List<Float>) {
	predicateConditions += TableBonus(enchantment, chances)
}

fun Predicate.tableBonus(enchantment: Argument.Enchantment, vararg chances: Float) {
	predicateConditions += TableBonus(enchantment, chances.toList())
}

fun Predicate.tableBonus(enchantment: Argument.Enchantment, chances: List<Float>.() -> Unit = {}) {
	predicateConditions += TableBonus(enchantment, buildList(chances))
}
