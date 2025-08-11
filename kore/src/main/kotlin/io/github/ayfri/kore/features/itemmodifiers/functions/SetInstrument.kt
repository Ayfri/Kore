package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.tagged.InstrumentTagArgument
import kotlinx.serialization.Serializable

/**
 * Sets the instrument tag for goat horns. Mirrors `minecraft:set_instrument`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetInstrument(
	override var conditions: PredicateAsList? = null,
	val options: InstrumentTagArgument,
) : ItemFunction()

/** Add a `set_instrument` step. */
fun ItemModifier.setInstrument(instrument: InstrumentTagArgument, block: SetInstrument.() -> Unit = {}) {
	modifiers += SetInstrument(options = instrument).apply(block)
}
