package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import kotlinx.serialization.Serializable

@Serializable(with = EntityDataComponent.Companion.EntityDataComponentSerializer::class)
data class EntityDataComponent(var data: NbtCompound) : Component() {
	companion object {
		object EntityDataComponentSerializer : InlineSerializer<EntityDataComponent, NbtCompound>(
			NbtCompound.serializer(),
			EntityDataComponent::data
		)
	}
}

fun Components.entityData(data: NbtCompound) = apply {
	components["entity_data"] = EntityDataComponent(data)
}

fun Components.entityData(block: NbtCompoundBuilder.() -> Unit) = apply {
	components["entity_data"] = EntityDataComponent(nbt(block))
}
