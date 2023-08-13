package features.itemmodifiers.functions

import arguments.types.resources.PotionArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetPotion(
	override var conditions: PredicateAsList? = null,
	val potion: PotionArgument,
) : ItemFunction()

fun ItemModifier.setPotion(potion: PotionArgument, block: SetPotion.() -> Unit = {}) {
	modifiers += SetPotion(potion = potion).apply(block)
}
