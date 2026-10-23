package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.ResolvableInt
import io.github.ayfri.kore.arguments.components.data.resolvableInt
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:compostable` item component, which makes the item usable in a composter and sets how many layers
 * it adds, as a plain number or the ID of a [context int provider file][io.github.ayfri.kore.features.contextproviders.ContextIntProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#compostable
 */
@Serializable
data class CompostableComponent(
	var layers: ResolvableInt,
) : Component()

/**
 * Makes the item compostable, adding the number of layers [layers] gives.
 *
 * ```
 * compostable(ContextIntProviders.Compostable.LOW)
 * ```
 */
fun ComponentsScope.compostable(layers: ContextIntProviderArgument) = compostable(resolvableInt(layers))

/**
 * Makes the item compostable, adding the layers [layers] holds, a number or a context int provider ID.
 *
 * ```
 * compostable(resolvableInt(ContextIntProviders.Compostable.LOW))
 * ```
 */
fun ComponentsScope.compostable(layers: ResolvableInt) =
	apply { this[ItemComponentTypes.COMPOSTABLE] = CompostableComponent(layers) }

/**
 * Makes the item compostable, adding [layers] layers.
 *
 * ```
 * compostable(1)  // compostable={layers:1}
 * ```
 */
fun ComponentsScope.compostable(layers: Int) = compostable(resolvableInt(layers))
