package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.ResolvableFloat
import io.github.ayfri.kore.arguments.components.data.ResolvableInt
import io.github.ayfri.kore.arguments.components.data.resolvableFloat
import io.github.ayfri.kore.arguments.components.data.resolvableInt
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:brewing_fuel` item component, which makes the item usable as fuel in brewing stands.
 * Both fields take a plain number or the ID of a context provider file
 * ([float][io.github.ayfri.kore.features.contextproviders.ContextFloatProviderFile] for [speedMultiplier],
 * [int][io.github.ayfri.kore.features.contextproviders.ContextIntProviderFile] for [uses]).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#brewing_fuel
 */
@Serializable
data class BrewingFuelComponent(
	var speedMultiplier: ResolvableFloat,
	var uses: ResolvableInt,
) : Component()

/**
 * Makes the item a brewing stand fuel brewing [uses] times, at the speed [speedMultiplier] gives.
 *
 * ```
 * brewingFuel(ContextFloatProviders.Brewing.SPEED_DEFAULT, ContextIntProviders.Brewing.USES_DEFAULT)
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: ContextFloatProviderArgument, uses: ContextIntProviderArgument) =
	brewingFuel(resolvableFloat(speedMultiplier), resolvableInt(uses))

/**
 * Makes the item a brewing stand fuel with numbers or context provider IDs mixed freely.
 *
 * ```
 * brewingFuel(resolvableFloat(2f), resolvableInt(ContextIntProviders.Brewing.USES_DEFAULT))
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: ResolvableFloat, uses: ResolvableInt) =
	apply { this[ItemComponentTypes.BREWING_FUEL] = BrewingFuelComponent(speedMultiplier, uses) }

/**
 * Makes the item a brewing stand fuel brewing [uses] times, at the speed [speedMultiplier].
 *
 * ```
 * brewingFuel(speedMultiplier = 2f, uses = 20)  // brewing_fuel={speed_multiplier:2.0f,uses:20}
 * ```
 */
fun ComponentsScope.brewingFuel(speedMultiplier: Float, uses: Int) =
	brewingFuel(resolvableFloat(speedMultiplier), resolvableInt(uses))
