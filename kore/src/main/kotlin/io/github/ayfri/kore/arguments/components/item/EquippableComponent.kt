package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.EquipmentSlot
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquippableComponent(
	var slot: EquipmentSlot,
	@SerialName("asset_id")
	var assetId: ModelArgument,
	@SerialName("allowed_entities")
	var allowedEntities: InlinableList<EntityTypeOrTagArgument>? = null,
	@SerialName("camera_overlay")
	var cameraOverlay: ModelArgument? = null,
	@SerialName("can_be_sheared")
	var canBeSheared: Boolean? = null,
	@SerialName("damage_on_hurt")
	var damageOnHurt: Boolean? = null,
	var dispensable: Boolean? = null,
	@SerialName("equip_on_interact")
	var equipOnInteract: Boolean? = null,
	@SerialName("equip_sound")
	var equipSound: SoundEventArgument? = null,
	@SerialName("shearing_sound")
	var shearingSound: SoundEventArgument? = null,
	var swappable: Boolean? = null,
) : Component()

fun ComponentsScope.equippable(
	slot: EquipmentSlot,
	assetId: ModelArgument,
	init: EquippableComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.EQUIPPABLE] = EquippableComponent(slot, assetId = assetId).apply(init)
}

fun ComponentsScope.equippable(
	slot: EquipmentSlot,
	assetId: String,
	namespace: String = "minecraft",
	init: EquippableComponent.() -> Unit = {},
) = equippable(slot, ModelArgument(assetId, namespace), init)

fun EquippableComponent.allowedEntities(vararg entities: EntityTypeOrTagArgument) = apply {
	allowedEntities = entities.toList()
}
