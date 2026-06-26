package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.InstrumentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Sets the instrument for goat horns. Mirrors `minecraft:set_instrument`.
 *
 * `options` accepts a single ID, a tag, or a list of IDs/tags.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetInstrument(
	override var conditions: PredicateAsList? = null,
	var options: InlinableList<InstrumentOrTagArgument>,
) : ItemFunction()

/** Add a `set_instrument` step with instrument IDs or tags. */
fun ItemModifier.setInstrument(
	instruments: InlinableList<InstrumentOrTagArgument>,
	block: SetInstrument.() -> Unit = {}
) {
	modifiers += SetInstrument(options = instruments).apply(block)
}

/** Add a `set_instrument` step with multiple instrument IDs/tags. */
fun ItemModifier.setInstrument(vararg instruments: InstrumentOrTagArgument, block: SetInstrument.() -> Unit = {}) {
	modifiers += SetInstrument(options = instruments.toList()).apply(block)
}
