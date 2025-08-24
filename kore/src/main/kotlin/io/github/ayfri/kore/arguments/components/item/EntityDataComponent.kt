package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

@Serializable(with = EntityDataComponent.Companion.EntityDataComponentSerializer::class)
data class EntityDataComponent(var data: NbtCompound) : Component() {
	companion object {
		data object EntityDataComponentSerializer : InlineAutoSerializer<EntityDataComponent>(
			EntityDataComponent::class
		)
	}
}

fun ComponentsScope.entityData(data: NbtCompound) = apply {
	this[ItemComponentTypes.ENTITY_DATA] = EntityDataComponent(data)
}

fun ComponentsScope.entityData(block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ItemComponentTypes.ENTITY_DATA] = EntityDataComponent(nbt(block))
}
