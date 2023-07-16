package features.itemmodifiers.functions

import arguments.types.resources.PotionArgument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetPotion(
	val potion: PotionArgument
) : ItemFunction()

fun ItemModifier.setPotion(potion: PotionArgument, block: SetPotion.() -> Unit = {}) {
	modifiers += SetPotion(potion).apply(block)
}
