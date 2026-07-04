package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** A single bee stored inside a hive: its [entityData], time spent in the hive and minimum stay. */
@Serializable
data class BeeData(
	@SerialName("entity_data")
	var entityData: Map<String, String>,
	@SerialName("ticks_in_hive")
	var ticksInHive: Int,
	@SerialName("min_ticks_in_hive")
	var minTicksInHive: Int,
)

/**
 * Represents the `minecraft:bees` item component, which stores the bee entities inside a beehive or bee nest item.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#bees
 */
@Serializable(with = BeesContainer.Companion.BeesContainerSerializer::class)
data class BeesContainer(var list: List<BeeData>) : Component() {
	companion object {
		data object BeesContainerSerializer : InlineAutoSerializer<BeesContainer, List<BeeData>>(
			serializer<List<BeeData>>(),
			BeesContainer::list,
			::BeesContainer
		)
	}
}

/** Stores bee entities inside a beehive or bee nest item. */
fun ComponentsScope.bees(bees: List<BeeData>) = apply {
	this[ItemComponentTypes.BEES] = BeesContainer(bees)
}

fun ComponentsScope.bees(vararg bees: BeeData) = apply {
	this[ItemComponentTypes.BEES] = BeesContainer(bees.toList())
}

fun ComponentsScope.bees(block: BeesContainer.() -> Unit) = apply {
	this[ItemComponentTypes.BEES] = BeesContainer(mutableListOf()).apply(block)
}

fun BeesContainer.bee(ticksInHive: Int, minTicksInHive: Int, entityData: Map<String, String>) = apply {
	list += BeeData(entityData, ticksInHive, minTicksInHive)
}

fun BeesContainer.bee(ticksInHive: Int, minTicksInHive: Int, entityData: Map<String, String> = emptyMap(), block: BeeData.() -> Unit) =
	apply {
		list += BeeData(entityData, ticksInHive, minTicksInHive).apply(block)
	}

fun BeeData.entityData(block: MutableMap<String, String>.() -> Unit) = apply {
	entityData = entityData + buildMap(block)
}

fun BeeData.entityDataId(id: EntityTypes) = apply {
	entityData = entityData + ("id" to id.asId())
}
