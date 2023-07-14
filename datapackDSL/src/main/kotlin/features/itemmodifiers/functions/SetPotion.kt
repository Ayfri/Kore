package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetPotion(
	val potion: Argument.Potion
) : ItemFunction()

fun ItemModifier.setPotion(potion: Argument.Potion, block: SetPotion.() -> Unit = {}) {
	modifiers += SetPotion(potion).apply(block)
}
