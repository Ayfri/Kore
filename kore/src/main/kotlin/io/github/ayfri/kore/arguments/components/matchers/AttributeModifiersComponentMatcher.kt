package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.advancements.serializers.FloatRangeOrFloatJson
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import kotlinx.serialization.Serializable

@Serializable
data class AttributeModifierMatcher(
	var type: AttributeArgument? = null,
	var slot: EquipmentSlot? = null,
	var uuid: UUIDArgument? = null,
	var name: String? = null,
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
	uuid: UUIDArgument? = null,
	name: String? = null,
	amount: FloatRangeOrFloatJson? = null,
	operation: AttributeModifierOperation? = null,
) = apply { this += AttributeModifierMatcher(type, slot, uuid, name, amount, operation) }

fun MutableList<AttributeModifierMatcher>.modifier(
	amount: Double,
	type: AttributeArgument? = null,
	slot: EquipmentSlot? = null,
	uuid: UUIDArgument? = null,
	name: String? = null,
	operation: AttributeModifierOperation? = null,
) = modifier(type, slot, uuid, name, rangeOrDouble(amount), operation)
