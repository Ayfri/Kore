package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:sulfur_cube_content` item component, which stores the item held inside a sulfur cube.
 *
 * Serializes as the item id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#sulfur_cube_content
 */
@Serializable(with = SulfurCubeContentComponent.Companion.SulfurCubeContentComponentSerializer::class)
data class SulfurCubeContentComponent(
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var value: ItemArgument,
) : Component() {
	companion object {
		@Suppress("UNCHECKED_CAST")
		data object SulfurCubeContentComponentSerializer : InlineAutoSerializer<SulfurCubeContentComponent, ItemArgument>(
			ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer as KSerializer<ItemArgument>,
			SulfurCubeContentComponent::value,
			::SulfurCubeContentComponent,
		)
	}
}

/** Stores the item held inside a sulfur cube. */
fun ComponentsScope.sulfurCubeContent(value: ItemArgument) = apply {
	this[ItemComponentTypes.SULFUR_CUBE_CONTENT] = SulfurCubeContentComponent(value)
}

fun ComponentsScope.sulfurCubeContent(value: String, namespace: String = "minecraft") = sulfurCubeContent(ItemArgument(value, namespace))
