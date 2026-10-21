package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.NumberProviders
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:cooking_fuel` item component, which makes the item usable as fuel in furnaces, smokers and blast furnaces.
 * Both fields are read from [number provider files][io.github.ayfri.kore.features.numberproviders.NumberProviderFile].
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cooking_fuel
 */
@Serializable
data class CookingFuelComponent(
	var burnTime: NumberProviderArgument,
	var speedMultiplier: NumberProviderArgument,
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
) = apply { this[ItemComponentTypes.COOKING_FUEL] = CookingFuelComponent(burnTime, speedMultiplier) }
