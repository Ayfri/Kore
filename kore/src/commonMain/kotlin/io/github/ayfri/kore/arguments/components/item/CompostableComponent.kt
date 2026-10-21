package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.ResolvableNumber
import io.github.ayfri.kore.arguments.components.data.resolvableNumber
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:compostable` item component, which makes the item usable in a composter and sets how many layers
 * it adds, as a plain number or the ID of a [number provider file][io.github.ayfri.kore.features.numberproviders.NumberProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#compostable
 */
@Serializable
data class CompostableComponent(
	var layers: ResolvableNumber,
) : Component()

/**
 * Makes the item compostable, adding the number of layers [layers] gives.
 *
 * ```
 * compostable(NumberProviders.Compostable.LOW)
 * ```
 */
fun ComponentsScope.compostable(layers: NumberProviderArgument) = compostable(resolvableNumber(layers))

/**
 * Makes the item compostable, adding the layers [layers] holds, a number or a number provider ID.
 *
 * ```
 * compostable(resolvableNumber(NumberProviders.Compostable.LOW))
 * ```
 */
fun ComponentsScope.compostable(layers: ResolvableNumber) =
	apply { this[ItemComponentTypes.COMPOSTABLE] = CompostableComponent(layers) }

/**
 * Makes the item compostable, adding [layers] layers.
 *
 * ```
 * compostable(1)  // compostable={layers:1}
 * ```
 */
fun ComponentsScope.compostable(layers: Int) = compostable(resolvableNumber(layers))

/**
 * Makes the item compostable, adding [layers] layers.
 *
 * ```
 * compostable(0.5f)  // compostable={layers:0.5f}
 * ```
 */
fun ComponentsScope.compostable(layers: Float) = compostable(resolvableNumber(layers))
