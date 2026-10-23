package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.ResolvableFloat
import io.github.ayfri.kore.arguments.components.data.ResolvableInt
import io.github.ayfri.kore.arguments.components.data.resolvableFloat
import io.github.ayfri.kore.arguments.components.data.resolvableInt
import io.github.ayfri.kore.generated.ContextFloatProviders
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:cooking_fuel` item component, which makes the item usable as fuel in furnaces, smokers and blast furnaces.
 * Both fields take a plain number or the ID of a context provider file
 * ([int][io.github.ayfri.kore.features.contextproviders.ContextIntProviderFile] for [burnTime],
 * [float][io.github.ayfri.kore.features.contextproviders.ContextFloatProviderFile] for [speedMultiplier]).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cooking_fuel
 */
@Serializable
data class CookingFuelComponent(
	var burnTime: ResolvableInt,
	var speedMultiplier: ResolvableFloat,
) : Component()

/**
 * Makes the item a furnace fuel burning for the ticks [burnTime] gives, at the cooking speed [speedMultiplier] gives (vanilla default speed).
 *
 * ```
 * cookingFuel(ContextIntProviders.Cooking.TIME_COAL)
 * cookingFuel(ContextIntProviders.Cooking.TIME_COAL, ContextFloatProviderArgument("fast", "my_pack"))
 * ```
 */
fun ComponentsScope.cookingFuel(
	burnTime: ContextIntProviderArgument,
	speedMultiplier: ContextFloatProviderArgument = ContextFloatProviders.Cooking.SPEED_DEFAULT,
) = cookingFuel(resolvableInt(burnTime), resolvableFloat(speedMultiplier))

/**
 * Makes the item a furnace fuel with numbers or context provider IDs mixed freely.
 *
 * ```
 * cookingFuel(resolvableInt(ContextIntProviders.Cooking.TIME_COAL), resolvableFloat(2f))
 * ```
 */
fun ComponentsScope.cookingFuel(
	burnTime: ResolvableInt,
	speedMultiplier: ResolvableFloat = resolvableFloat(ContextFloatProviders.Cooking.SPEED_DEFAULT),
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
	resolvableInt(burnTime),
	speedMultiplier?.let { resolvableFloat(it) } ?: resolvableFloat(ContextFloatProviders.Cooking.SPEED_DEFAULT),
)
