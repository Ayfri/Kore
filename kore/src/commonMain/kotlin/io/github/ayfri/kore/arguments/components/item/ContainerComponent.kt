package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** A single slot/item pair stored inside a [ContainerComponent]. */
@Serializable
data class ContainerSlot(var slot: Int, var item: ItemStack)

/**
 * Represents the `minecraft:container` item component, which stores items inside a container item (e.g., shulker box).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#container
 */
@Serializable(with = ContainerComponent.Companion.ContainerComponentSerializer::class)
data class ContainerComponent(var slots: List<ContainerSlot>) : Component() {
	companion object {
		data object ContainerComponentSerializer : InlineAutoSerializer<ContainerComponent, List<ContainerSlot>>(
			serializer<List<ContainerSlot>>(),
			ContainerComponent::slots,
			::ContainerComponent
		)
	}
}

/** Stores items inside a container item (e.g., shulker box). */
fun ComponentsScope.container(slots: List<ContainerSlot>) = apply {
	this[ItemComponentTypes.CONTAINER] = ContainerComponent(slots)
}

fun ComponentsScope.container(vararg slots: ContainerSlot) = apply {
	this[ItemComponentTypes.CONTAINER] = ContainerComponent(slots.toList())
}

fun ComponentsScope.container(block: ContainerComponent.() -> Unit) = apply {
	this[ItemComponentTypes.CONTAINER] = ContainerComponent(mutableListOf()).apply(block)
}

fun ContainerComponent.slot(slot: Int, item: ItemStack) = apply {
	slots += ContainerSlot(slot, item)
}

operator fun ContainerComponent.set(slot: Int, item: ItemStack) = slot(slot, item)

fun ContainerComponent.slot(slot: ItemSlotType, item: ItemStack) = slot(slot.asIndex(), item)
operator fun ContainerComponent.set(slot: ItemSlotType, item: ItemStack) = slot(slot, item)
