package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:compostable` item component, which makes the item usable in a composter and sets how many layers
 * it adds, read from a [number provider file][io.github.ayfri.kore.features.numberproviders.NumberProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#compostable
 */
@Serializable
data class CompostableComponent(
	var layers: NumberProviderArgument,
) : Component()

/**
 * Makes the item compostable, adding the number of layers [layers] gives.
 *
 * ```
 * compostable(NumberProviders.Compostable.LOW)
 * ```
 */
fun ComponentsScope.compostable(layers: NumberProviderArgument) =
	apply { this[ItemComponentTypes.COMPOSTABLE] = CompostableComponent(layers) }
