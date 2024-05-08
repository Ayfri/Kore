package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

@Serializable(with = EntityDataComponent.Companion.EntityDataComponentSerializer::class)
data class EntityDataComponent(var data: NbtCompound) : Component() {
	companion object {
		object EntityDataComponentSerializer : InlineSerializer<EntityDataComponent, NbtCompound>(
			NbtCompound.serializer(),
			EntityDataComponent::data
		)
	}
}

fun ComponentsScope.entityData(data: NbtCompound) = apply {
	this[ComponentTypes.ENTITY_DATA] = EntityDataComponent(data)
}

fun ComponentsScope.entityData(block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ComponentTypes.ENTITY_DATA] = EntityDataComponent(nbt(block))
}
