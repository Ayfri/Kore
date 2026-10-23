package io.github.ayfri.kore.arguments.components.data

import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * A float written inline, or the ID of a context float provider file evaluated when the item is used (mcdoc
 * `ResolvableFloat`). An inline provider object or a tag isn't accepted. Exactly one of the two is set.
 *
 * @property id The context float provider file ID, null when the instance holds a value.
 * @property value The number, null when the instance holds an ID.
 */
@KeepGeneratedSerializer
@Serializable(with = ResolvableFloat.Companion.ResolvableFloatSerializer::class)
data class ResolvableFloat(
	var id: ContextFloatProviderArgument? = null,
	var value: Float? = null,
) {
	companion object {
		data object ResolvableFloatSerializer :
			EitherInlineSerializer<ResolvableFloat>(generatedSerializer(), "value", "id")
	}
}

/**
 * A whole number written inline, or the ID of a context int provider file evaluated when the item is used (mcdoc
 * `ResolvableInt`). An inline provider object or a tag isn't accepted. Exactly one of the two is set.
 *
 * @property id The context int provider file ID, null when the instance holds a value.
 * @property value The number, null when the instance holds an ID.
 */
@KeepGeneratedSerializer
@Serializable(with = ResolvableInt.Companion.ResolvableIntSerializer::class)
data class ResolvableInt(
	var id: ContextIntProviderArgument? = null,
	var value: Int? = null,
) {
	companion object {
		data object ResolvableIntSerializer :
			EitherInlineSerializer<ResolvableInt>(generatedSerializer(), "value", "id")
	}
}

/**
 * References the context float provider file [id], evaluated when the item is used.
 *
 * ```
 * resolvableFloat(ContextFloatProviders.Cooking.SPEED_DEFAULT)  // "minecraft:cooking/speed_default"
 * ```
 */
fun resolvableFloat(id: ContextFloatProviderArgument) = ResolvableFloat(id = id)

/**
 * Writes [value] as a float.
 *
 * ```
 * resolvableFloat(1.5f)  // speed_multiplier:1.5f
 * ```
 */
fun resolvableFloat(value: Float) = ResolvableFloat(value = value)

/**
 * References the context int provider file [id], evaluated when the item is used.
 *
 * ```
 * resolvableInt(ContextIntProviders.Cooking.TIME_COAL)  // "minecraft:cooking/time_coal"
 * ```
 */
fun resolvableInt(id: ContextIntProviderArgument) = ResolvableInt(id = id)

/**
 * Writes [value] as an int.
 *
 * ```
 * resolvableInt(200)  // burn_time:200
 * ```
 */
fun resolvableInt(value: Int) = ResolvableInt(value = value)
