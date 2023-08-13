package features.itemmodifiers.functions

import arguments.types.resources.PotionArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetStewEffect(
	override var conditions: PredicateAsList? = null,
	val effects: List<PotionEffect>,
) : ItemFunction()

@Serializable
data class PotionEffect(
	var type: PotionArgument,
	var duration: NumberProvider = constant(0f),
)

fun ItemModifier.setStewEffect(effects: MutableList<PotionEffect>.() -> Unit) =
	SetStewEffect(effects = buildList(effects)).also { modifiers += it }

fun ItemModifier.setStewEffect(type: PotionArgument, duration: NumberProvider = constant(0f), block: SetStewEffect.() -> Unit = {}) {
	modifiers += SetStewEffect(effects = listOf(PotionEffect(type, duration))).apply(block)
}

fun MutableList<PotionEffect>.potionEffect(type: PotionArgument, duration: NumberProvider = constant(0f)) {
	add(PotionEffect(type, duration))
}
