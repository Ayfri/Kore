package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import kotlinx.serialization.Serializable

@Serializable
data class SetPotion(
	override var conditions: PredicateAsList? = null,
	val potion: PotionArgument,
) : ItemFunction()

fun ItemModifier.setPotion(potion: PotionArgument, block: SetPotion.() -> Unit = {}) {
	modifiers += SetPotion(potion = potion).apply(block)
}
