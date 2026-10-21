package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:brewing_fuel` item component, which makes the item usable as fuel in brewing stands.
 * Both fields are read from [number provider files][io.github.ayfri.kore.features.numberproviders.NumberProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#brewing_fuel
 */
@Serializable
data class BrewingFuelComponent(
	var speedMultiplier: NumberProviderArgument,
	var uses: NumberProviderArgument,
) : Component()

/**
 * Makes the item a brewing stand fuel brewing [uses] times, at the speed [speedMultiplier] gives.
 *
 * ```
 * brewingFuel(NumberProviders.Cooking.SPEED_DEFAULT, NumberProviders.Brewing.USES_DEFAULT)
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: NumberProviderArgument, uses: NumberProviderArgument) =
	apply { this[ItemComponentTypes.BREWING_FUEL] = BrewingFuelComponent(speedMultiplier, uses) }
