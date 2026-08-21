package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * An integer bound written either as a bare number ([value]) or as a `{ "min": ..., "max": ... }` object backed by
 * number providers ([range]).
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = IntOrNumberProvidersRange.Companion.IntOrNumberProviderRangeSerializer::class)
data class IntOrNumberProvidersRange(
	var value: Int? = null,
	var range: UniformNumberProvider? = null,
) {
	companion object {
		data object IntOrNumberProviderRangeSerializer :
			EitherInlineSerializer<IntOrNumberProvidersRange>(generatedSerializer(), "value", "range")
	}
}

/** Creates an [IntOrNumberProvidersRange] matching the exact [value]. */
fun intValue(value: Int) = IntOrNumberProvidersRange(value)

/** Creates an [IntOrNumberProvidersRange] between the values produced by [min] and [max]. */
fun providersRange(min: NumberProvider, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(min, max))

/** Creates an [IntOrNumberProvidersRange] between [min] and the value produced by [max]. */
fun providersRange(min: Float, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(constant(min), max))

/** Creates an [IntOrNumberProvidersRange] between the value produced by [min] and [max]. */
fun providersRange(min: NumberProvider, max: Float) = IntOrNumberProvidersRange(range = uniform(min, constant(max)))

/** Creates an [IntOrNumberProvidersRange] between [min] and [max]. */
fun intRange(min: Float, max: Float) = IntOrNumberProvidersRange(range = uniform(constant(min), constant(max)))

/** Creates an [IntOrNumberProvidersRange] spanning [range]. */
fun intRange(range: ClosedFloatingPointRange<Float>) =
	IntOrNumberProvidersRange(range = uniform(constant(range.start), constant(range.endInclusive)))
