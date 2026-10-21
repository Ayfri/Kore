package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.ResolvableNumber
import io.github.ayfri.kore.arguments.components.data.resolvableNumber
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:brewing_fuel` item component, which makes the item usable as fuel in brewing stands.
 * Both fields take a plain number or the ID of a [number provider file][io.github.ayfri.kore.features.numberproviders.NumberProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#brewing_fuel
 */
@Serializable
data class BrewingFuelComponent(
	var speedMultiplier: ResolvableNumber,
	var uses: ResolvableNumber,
) : Component()

/**
 * Makes the item a brewing stand fuel brewing [uses] times, at the speed [speedMultiplier] gives.
 *
 * ```
 * brewingFuel(NumberProviders.Brewing.SPEED_DEFAULT, NumberProviders.Brewing.USES_DEFAULT)
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: NumberProviderArgument, uses: NumberProviderArgument) =
	brewingFuel(resolvableNumber(speedMultiplier), resolvableNumber(uses))

/**
 * Makes the item a brewing stand fuel with numbers or number provider IDs mixed freely.
 *
 * ```
 * brewingFuel(resolvableNumber(2f), resolvableNumber(NumberProviders.Brewing.USES_DEFAULT))
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: ResolvableNumber, uses: ResolvableNumber) =
	apply { this[ItemComponentTypes.BREWING_FUEL] = BrewingFuelComponent(speedMultiplier, uses) }

/**
 * Makes the item a brewing stand fuel brewing [uses] times, at the speed [speedMultiplier].
 *
 * ```
 * brewingFuel(speedMultiplier = 2f, uses = 20)  // brewing_fuel={speed_multiplier:2.0f,uses:20}
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: Float, uses: Int) =
	brewingFuel(resolvableNumber(speedMultiplier), resolvableNumber(uses))
