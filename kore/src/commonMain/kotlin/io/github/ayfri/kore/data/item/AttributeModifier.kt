package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.predicates.providers.ConstantContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class AttributeModifier(
	var attribute: AttributeArgument,
	var amount: ContextFloatProvider = ConstantContextFloatProvider(0f),
	var id: String,
	var operation: AttributeModifierOperation = AttributeModifierOperation.ADD_VALUE,
	var slot: InlinableList<EquipmentSlot>? = null,
)
