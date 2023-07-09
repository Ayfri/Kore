package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetStewEffect(
	val effects: List<PotionEffect>
) : ItemFunctionSurrogate

@Serializable
data class PotionEffect(
	var type: Argument.Potion,
	var duration: NumberProvider = constant(0f)
)

fun ItemModifierEntry.setStewEffect(effects: MutableList<PotionEffect>.() -> Unit) {
	function = SetStewEffect(buildList(effects))
}

fun ItemModifierEntry.setStewEffect(type: Argument.Potion, duration: NumberProvider = constant(0f)) {
	function = SetStewEffect(listOf(PotionEffect(type, duration)))
}

fun MutableList<PotionEffect>.potionEffect(type: Argument.Potion, duration: NumberProvider = constant(0f)) {
	add(PotionEffect(type, duration))
}
