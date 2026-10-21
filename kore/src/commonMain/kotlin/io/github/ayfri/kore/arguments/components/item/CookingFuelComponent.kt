package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.ResolvableNumber
import io.github.ayfri.kore.arguments.components.data.resolvableNumber
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.NumberProviders
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:cooking_fuel` item component, which makes the item usable as fuel in furnaces, smokers and blast furnaces.
 * Both fields take a plain number or the ID of a [number provider file][io.github.ayfri.kore.features.numberproviders.NumberProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cooking_fuel
 */
@Serializable
data class CookingFuelComponent(
	var burnTime: ResolvableNumber,
	var speedMultiplier: ResolvableNumber,
) : Component()

/**
 * Makes the item a furnace fuel burning for the ticks [burnTime] gives, at the cooking speed [speedMultiplier] gives (vanilla default speed).
 *
 * ```
 * cookingFuel(NumberProviders.Cooking.TIME_COAL)
 * cookingFuel(NumberProviders.Cooking.TIME_COAL, NumberProviderArgument("fast", "my_pack"))
 * ```
 */
fun ComponentsScope.cookingFuel(
	burnTime: NumberProviderArgument,
	speedMultiplier: NumberProviderArgument = NumberProviders.Cooking.SPEED_DEFAULT,
) = cookingFuel(resolvableNumber(burnTime), resolvableNumber(speedMultiplier))

/**
 * Makes the item a furnace fuel with numbers or number provider IDs mixed freely.
 *
 * ```
 * cookingFuel(resolvableNumber(NumberProviders.Cooking.TIME_COAL), resolvableNumber(2))
 * ```
 */
fun ComponentsScope.cookingFuel(
	burnTime: ResolvableNumber,
	speedMultiplier: ResolvableNumber = resolvableNumber(NumberProviders.Cooking.SPEED_DEFAULT),
) = apply { this[ItemComponentTypes.COOKING_FUEL] = CookingFuelComponent(burnTime, speedMultiplier) }

/**
 * Makes the item a furnace fuel burning for [burnTime] ticks, at the cooking speed [speedMultiplier] (vanilla default speed when null).
 *
 * ```
 * cookingFuel(burnTime = 200, speedMultiplier = 1.5f)  // cooking_fuel={burn_time:200,speed_multiplier:1.5f}
 * cookingFuel(200)                                     // speed_multiplier:"minecraft:cooking/speed_default"
 * ```
 */
fun ComponentsScope.cookingFuel(burnTime: Int, speedMultiplier: Float? = null) = cookingFuel(
	resolvableNumber(burnTime),
	speedMultiplier?.let { resolvableNumber(it) } ?: resolvableNumber(NumberProviders.Cooking.SPEED_DEFAULT),
)
