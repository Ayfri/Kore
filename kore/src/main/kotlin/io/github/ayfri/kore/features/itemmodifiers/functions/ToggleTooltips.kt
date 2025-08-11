package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Components that can be toggled in item tooltips.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable(with = ToggleableComponents.Companion.ToggleableComponentsSerializer::class)
enum class ToggleableComponents {
	ATTRIBUTE_MODIFIERS,
	CAN_BREAK,
	CAN_PLACE_ON,
	DYED_COLOR,
	ENCHANTMENTS,
	STORED_ENCHANTMENTS,
	TRIM,
	UNBREAKABLE;

	companion object {
		data object ToggleableComponentsSerializer : LowercaseSerializer<ToggleableComponents>(entries)
	}
}

/**
 * Toggles item tooltip components. Mirrors `minecraft:toggle_tooltip_components`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class ToggleTooltips(
	override var conditions: PredicateAsList? = null,
	var toggles: Map<ToggleableComponents, Boolean> = emptyMap(),
) : ItemFunction()

/** Add a `toggle_tooltip_components` step. */
fun ItemModifier.toggleTooltips(vararg toggles: Pair<ToggleableComponents, Boolean>, block: ToggleTooltips.() -> Unit) {
	modifiers += ToggleTooltips(toggles = toggles.toMap()).apply(block)
}

/** Add a `toggle_tooltip_components` step. */
fun ItemModifier.toggleTooltips(toggles: Map<ToggleableComponents, Boolean>, block: ToggleTooltips.() -> Unit = {}) {
	modifiers += ToggleTooltips(toggles = toggles).apply(block)
}

/** Add a `toggle_tooltip_components` step. */
fun ItemModifier.toggleTooltips(vararg toggles: ToggleableComponents, value: Boolean = true) {
	modifiers += ToggleTooltips(toggles = toggles.map { it to value }.toMap())
}

/** Append multiple toggles to the builder map. */
fun ToggleTooltips.toggles(toggle: Pair<ToggleableComponents, Boolean>, vararg toggles: Pair<ToggleableComponents, Boolean>) {
	this.toggles = this.toggles + toggles.toMap() + toggle
}

/** Append a single toggle to the builder map. */
fun ToggleTooltips.toggle(value: Boolean = true, vararg toggle: ToggleableComponents) {
	this.toggles = this.toggles + mapOf(*toggle.map { it to value }.toTypedArray())
}
