package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = IntOrNumberProvidersRange.Companion.IntOrNumberProviderRangeSerializer::class)
data class IntOrNumberProvidersRange(
	var value: Int? = null,
	var range: Uniform? = null,
) {
	companion object {
		data object IntOrNumberProviderRangeSerializer : EitherInlineSerializer<IntOrNumberProvidersRange>(
			IntOrNumberProvidersRange::class,
			IntOrNumberProvidersRange::value,
			IntOrNumberProvidersRange::range,
		)
	}
}

fun int(value: Int) = IntOrNumberProvidersRange(value)
fun providersRange(min: NumberProvider, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(min, max))
fun providersRange(min: Float, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(constant(min), max))
fun providersRange(min: NumberProvider, max: Float) = IntOrNumberProvidersRange(range = uniform(min, constant(max)))
fun intRange(min: Float, max: Float) = IntOrNumberProvidersRange(range = uniform(constant(min), constant(max)))
fun intRange(range: ClosedFloatingPointRange<Float>) =
	IntOrNumberProvidersRange(range = uniform(constant(range.start), constant(range.endInclusive)))
