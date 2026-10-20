package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.EquipmentSlot
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.EquipmentAssetArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:equippable` item component, which configures equipment slot, sounds, and model when the item is worn.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#equippable
 */
@Serializable
data class EquippableComponent(
	var slot: EquipmentSlot,
	var assetId: EquipmentAssetArgument,
	var allowedEntities: InlinableList<EntityTypeOrTagArgument>? = null,
	var cameraOverlay: ModelArgument? = null,
	var canBeSheared: Boolean? = null,
	var damageOnHurt: Boolean? = null,
	var dispensable: Boolean? = null,
	var equipOnInteract: Boolean? = null,
	var equipSound: SoundEventArgument? = null,
	var shearingSound: SoundEventArgument? = null,
	var swappable: Boolean? = null,
) : Component()

/** Configures equipment slot, sounds, and model when the item is worn. */
fun ComponentsScope.equippable(
	slot: EquipmentSlot,
	assetId: EquipmentAssetArgument,
	init: EquippableComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.EQUIPPABLE] = EquippableComponent(slot, assetId = assetId).apply(init)
}

/** Configures equipment slot, sounds, and model when the item is worn. */
fun ComponentsScope.equippable(
	slot: EquipmentSlot,
	assetId: String,
	namespace: String = "minecraft",
	init: EquippableComponent.() -> Unit = {},
) = equippable(slot, EquipmentAssetArgument(assetId, namespace), init)

/** Sets the allowed entities to wear the item. */
fun EquippableComponent.allowedEntities(vararg entities: EntityTypeOrTagArgument) = apply {
	allowedEntities = entities.toList()
}
