package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetStewEffect(
	val effects: List<PotionEffect>
) : ItemFunction()

@Serializable
data class PotionEffect(
	var type: Argument.Potion,
	var duration: NumberProvider = constant(0f)
)

fun ItemModifier.setStewEffect(effects: MutableList<PotionEffect>.() -> Unit) =
	SetStewEffect(buildList(effects)).also { modifiers += it }

fun ItemModifier.setStewEffect(type: Argument.Potion, duration: NumberProvider = constant(0f), block: SetStewEffect.() -> Unit = {}) {
	modifiers += SetStewEffect(listOf(PotionEffect(type, duration))).apply(block)
}

fun MutableList<PotionEffect>.potionEffect(type: Argument.Potion, duration: NumberProvider = constant(0f)) {
	add(PotionEffect(type, duration))
}
