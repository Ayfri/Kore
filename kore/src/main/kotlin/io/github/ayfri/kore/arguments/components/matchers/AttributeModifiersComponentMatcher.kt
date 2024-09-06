package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeModifierArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.advancements.serializers.FloatRangeOrFloatJson
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class AttributeModifierMatcher(
	var type: AttributeArgument? = null,
	var slot: EquipmentSlot? = null,
	var id: AttributeModifierArgument? = null,
	var amount: FloatRangeOrFloatJson? = null,
	var operation: AttributeModifierOperation? = null,
)

@Serializable
data class AttributeModifiersComponentMatcher(
	var modifiers: CollectionMatcher<AttributeModifierMatcher> = CollectionMatcher(),
) : ComponentMatcher()

fun ItemStackSubPredicates.attributeModifiers(init: AttributeModifiersComponentMatcher.() -> Unit) =
	apply { matchers += AttributeModifiersComponentMatcher().apply(init) }

fun AttributeModifiersComponentMatcher.modifiers(block: CollectionMatcher<AttributeModifierMatcher>.() -> Unit) {
	modifiers = CollectionMatcher<AttributeModifierMatcher>().apply(block)
}

fun MutableList<AttributeModifierMatcher>.modifier(
	type: AttributeArgument? = null,
	slot: EquipmentSlot? = null,
	id: AttributeModifierArgument? = null,
	amount: FloatRangeOrFloatJson? = null,
	operation: AttributeModifierOperation? = null,
) = apply { this += AttributeModifierMatcher(type, slot, id, amount, operation) }

fun MutableList<AttributeModifierMatcher>.modifier(
	type: AttributeArgument? = null,
	slot: EquipmentSlot? = null,
	name: String,
	namespace: String = "minecraft",
	amount: FloatRangeOrFloatJson? = null,
	operation: AttributeModifierOperation? = null,
) = modifier(type, slot, AttributeModifierArgument(name, namespace), amount, operation)

fun MutableList<AttributeModifierMatcher>.modifier(
	amount: Double,
	type: AttributeArgument? = null,
	slot: EquipmentSlot? = null,
	id: AttributeModifierArgument? = null,
	operation: AttributeModifierOperation? = null,
) = modifier(type, slot, id, rangeOrDouble(amount), operation)

fun MutableList<AttributeModifierMatcher>.modifier(
	amount: Double,
	type: AttributeArgument? = null,
	slot: EquipmentSlot? = null,
	name: String,
	namespace: String = "minecraft",
	operation: AttributeModifierOperation? = null,
) = modifier(type, slot, name, namespace, rangeOrDouble(amount), operation)
