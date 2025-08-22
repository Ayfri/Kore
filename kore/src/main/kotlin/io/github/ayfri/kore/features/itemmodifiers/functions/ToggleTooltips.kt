package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.DataComponentTypeArgument
import kotlinx.serialization.Serializable

/**
 * Toggles item tooltip components. Mirrors `minecraft:toggle_tooltip_components`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class ToggleTooltips(
	override var conditions: PredicateAsList? = null,
	var toggles: Map<DataComponentTypeArgument, Boolean> = emptyMap(),
) : ItemFunction()

/** Add a `toggle_tooltip_components` step. */
fun ItemModifier.toggleTooltips(vararg toggles: Pair<DataComponentTypeArgument, Boolean>, block: ToggleTooltips.() -> Unit) {
	modifiers += ToggleTooltips(toggles = toggles.toMap()).apply(block)
}

/** Add a `toggle_tooltip_components` step. */
fun ItemModifier.toggleTooltips(toggles: Map<DataComponentTypeArgument, Boolean>, block: ToggleTooltips.() -> Unit = {}) {
	modifiers += ToggleTooltips(toggles = toggles).apply(block)
}

/** Add a `toggle_tooltip_components` step. */
fun ItemModifier.toggleTooltips(vararg toggles: DataComponentTypeArgument, value: Boolean = true) {
	modifiers += ToggleTooltips(toggles = toggles.map { it to value }.toMap())
}

/** Append multiple toggles to the builder map. */
fun ToggleTooltips.toggles(toggle: Pair<DataComponentTypeArgument, Boolean>, vararg toggles: Pair<DataComponentTypeArgument, Boolean>) {
	this.toggles = this.toggles + toggles.toMap() + toggle
}

/** Append a single toggle to the builder map. */
fun ToggleTooltips.toggle(value: Boolean = true, vararg toggle: DataComponentTypeArgument) {
	this.toggles = this.toggles + mapOf(*toggle.map { it to value }.toTypedArray())
}
