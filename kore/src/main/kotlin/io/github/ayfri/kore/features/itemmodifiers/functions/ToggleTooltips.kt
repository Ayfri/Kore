package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ToggleableComponents.Companion.ToggleableComponentsSerializer::class)
enum class ToggleableComponents {
	TRIM,
	DYED_COLOR,
	ENCHANTMENTS,
	STORED_ENCHANTMENTS,
	UNBREAKABLE,
	CAN_BREAK,
	CAN_PLACE_ON,
	ATTRIBUTE_MODIFIERS;

	companion object {
		data object ToggleableComponentsSerializer : LowercaseSerializer<ToggleableComponents>(entries)
	}
}

@Serializable
data class ToggleTooltips(
	override var conditions: PredicateAsList? = null,
	var toggles: Map<ToggleableComponents, Boolean> = emptyMap(),
) : ItemFunction()

fun ItemModifier.toggleTooltips(vararg toggles: Pair<ToggleableComponents, Boolean>, block: ToggleTooltips.() -> Unit) {
	modifiers += ToggleTooltips(toggles = toggles.toMap()).apply(block)
}

fun ItemModifier.toggleTooltips(toggles: Map<ToggleableComponents, Boolean>, block: ToggleTooltips.() -> Unit = {}) {
	modifiers += ToggleTooltips(toggles = toggles).apply(block)
}

fun ItemModifier.toggleTooltips(vararg toggles: ToggleableComponents, value: Boolean = true) {
	modifiers += ToggleTooltips(toggles = toggles.map { it to value }.toMap())
}

fun ToggleTooltips.toggles(toggle: Pair<ToggleableComponents, Boolean>, vararg toggles: Pair<ToggleableComponents, Boolean>) {
	this.toggles = this.toggles + toggles.toMap() + toggle
}

fun ToggleTooltips.toggle(value: Boolean = true, vararg toggle: ToggleableComponents) {
	this.toggles = this.toggles + mapOf(*toggle.map { it to value }.toTypedArray())
}
