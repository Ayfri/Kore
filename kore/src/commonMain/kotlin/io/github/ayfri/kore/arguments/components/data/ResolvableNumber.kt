package io.github.ayfri.kore.arguments.components.data

import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * A number written inline, or the ID of a number provider file evaluated when the item is used (mcdoc `ResolvableNumber`).
 * An [int] prints without decimals and a [float] with them, so each form keeps its own field.
 *
 * @property float The number as a float, null when the instance holds another form.
 * @property id The number provider file ID, null when the instance holds another form.
 * @property int The number as an int, null when the instance holds another form.
 */
@KeepGeneratedSerializer
@Serializable(with = ResolvableNumber.Companion.ResolvableNumberSerializer::class)
data class ResolvableNumber(
	var float: Float? = null,
	var id: NumberProviderArgument? = null,
	var int: Int? = null,
) {
	companion object {
		data object ResolvableNumberSerializer :
			EitherInlineSerializer<ResolvableNumber>(generatedSerializer(), "int", "float", "id")
	}
}

/**
 * Writes [value] as a float.
 *
 * ```
 * resolvableNumber(1.5f)  // speed_multiplier:1.5f
 * ```
 */
fun resolvableNumber(value: Float) = ResolvableNumber(float = value)

/**
 * References the number provider file [id], evaluated when the item is used.
 *
 * ```
 * resolvableNumber(NumberProviders.Cooking.SPEED_DEFAULT)  // "minecraft:cooking/speed_default"
 * ```
 */
fun resolvableNumber(id: NumberProviderArgument) = ResolvableNumber(id = id)

/**
 * Writes [value] as an int.
 *
 * ```
 * resolvableNumber(200)  // burn_time:200
 * ```
 */
fun resolvableNumber(value: Int) = ResolvableNumber(int = value)
