package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.PotionOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Sets a random potion on potion-like items from a list of options. Mirrors `minecraft:set_random_potion`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * Minecraft Wiki: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetRandomPotion(
	override var conditions: PredicateAsList? = null,
	var options: InlinableList<PotionOrTagArgument>? = null,
) : ItemFunction()

/** Add a `set_random_potion` step. */
fun ItemModifier.setRandomPotion(vararg options: PotionOrTagArgument, block: SetRandomPotion.() -> Unit = {}) {
	modifiers += SetRandomPotion(options = options.toList().ifEmpty { null }).apply(block)
}
