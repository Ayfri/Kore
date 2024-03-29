package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class ContainerSlot(var slot: Int, var item: ItemStack)

@Serializable(with = ContainerComponent.Companion.ContainerComponentSerializer::class)
data class ContainerComponent(var slots: List<ContainerSlot>) : Component() {
	companion object {
		object ContainerComponentSerializer : InlineSerializer<ContainerComponent, List<ContainerSlot>>(
			ListSerializer(ContainerSlot.serializer()),
			ContainerComponent::slots
		)
	}
}

fun Components.container(slots: List<ContainerSlot>) = apply {
	this[ComponentTypes.CONTAINER] = ContainerComponent(slots)
}

fun Components.container(vararg slots: ContainerSlot) = apply {
	this[ComponentTypes.CONTAINER] = ContainerComponent(slots.toList())
}

fun Components.container(block: ContainerComponent.() -> Unit) = apply {
	this[ComponentTypes.CONTAINER] = ContainerComponent(mutableListOf()).apply(block)
}

fun ContainerComponent.slot(slot: Int, item: ItemStack) = apply {
	slots += ContainerSlot(slot, item)
}

operator fun ContainerComponent.set(slot: Int, item: ItemStack) = slot(slot, item)

fun ContainerComponent.slot(slot: ItemSlotType, item: ItemStack) = slot(slot.asIndex(), item)
operator fun ContainerComponent.set(slot: ItemSlotType, item: ItemStack) = slot(slot, item)
