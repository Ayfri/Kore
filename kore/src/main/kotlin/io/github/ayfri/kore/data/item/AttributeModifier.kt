package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class AttributeModifier(
	var attribute: AttributeArgument,
	var amount: NumberProvider = constant(0f),
	var operation: AttributeModifierOperation = AttributeModifierOperation.ADD_VALUE,
	var slot: InlinableList<EquipmentSlot>? = null,
	var uuid: UUIDArgument? = null,
)
