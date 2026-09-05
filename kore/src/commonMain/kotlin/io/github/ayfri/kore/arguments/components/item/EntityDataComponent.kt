package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

/**
 * Represents the `minecraft:entity_data` item component, which stores entity NBT data for spawn eggs or items that spawn entities.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#entity_data
 */
@Serializable(with = EntityDataComponent.Companion.EntityDataComponentSerializer::class)
data class EntityDataComponent(var data: NbtCompound) : Component() {
	companion object {
		data object EntityDataComponentSerializer : InlineAutoSerializer<EntityDataComponent, NbtCompound>(
			serializer<NbtCompound>(),
			EntityDataComponent::data,
			::EntityDataComponent
		)
	}
}

/** Stores entity NBT data for spawn eggs or items that spawn entities. */
fun ComponentsScope.entityData(data: NbtCompound) = apply {
	this[ItemComponentTypes.ENTITY_DATA] = EntityDataComponent(data)
}

fun ComponentsScope.entityData(block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ItemComponentTypes.ENTITY_DATA] = EntityDataComponent(nbt(block))
}
