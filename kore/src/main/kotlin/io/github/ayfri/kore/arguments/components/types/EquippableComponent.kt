package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.EquipmentSlot
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.EntityTypeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.arguments.types.resources.SoundEventArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquippableComponent(
	var slot: EquipmentSlot,
	@SerialName("equip_sound")
	var equipSound: SoundEventArgument? = null,
	var model: ModelArgument,
	@SerialName("allowed_entities")
	var allowedEntities: InlinableList<EntityTypeOrTagArgument>? = null,
	var dispensable: Boolean? = null,
) : Component()

fun ComponentsScope.equippable(
	slot: EquipmentSlot,
	model: ModelArgument,
	init: EquippableComponent.() -> Unit = {},
) = apply {
	this[ComponentTypes.EQUIPPABLE] = EquippableComponent(slot, model = model).apply(init)
}

fun ComponentsScope.equippable(
	slot: EquipmentSlot,
	model: String,
	namespace: String = "minecraft",
	init: EquippableComponent.() -> Unit = {},
) = equippable(slot, ModelArgument(model, namespace), init)

fun EquippableComponent.allowedEntities(vararg entities: EntityTypeOrTagArgument) = apply {
	allowedEntities = entities.toList()
}
