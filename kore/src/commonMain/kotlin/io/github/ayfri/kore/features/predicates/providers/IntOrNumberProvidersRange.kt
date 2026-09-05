package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * An integer bound written either as a bare number ([value]) or as a `{ "min": ..., "max": ... }` object backed by
 * number providers ([range]). Exactly one of the two is set.
 *
 * Both bounds are clamped to an integer by the game.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 *
 * @property value The exact value to match, when the bound is a bare number.
 * @property range The bounds to match, when the bound is an object.
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

/** Creates an [IntOrNumberProvidersRange] between [min] and [max], both included. */
fun intRange(min: NumberProvider, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(min, max))

/** Creates an [IntOrNumberProvidersRange] between [min] and [max], both included. */
fun intRange(min: Float, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(min, max))

/** Creates an [IntOrNumberProvidersRange] between [min] and [max], both included. */
fun intRange(min: NumberProvider, max: Float) = IntOrNumberProvidersRange(range = uniform(min, max))

/** Creates an [IntOrNumberProvidersRange] between [min] and [max], both included. */
fun intRange(min: Float, max: Float) = IntOrNumberProvidersRange(range = uniform(min, max))

/** Creates an [IntOrNumberProvidersRange] spanning [range], both bounds included. */
fun intRange(range: ClosedFloatingPointRange<Float>) = intRange(range.start, range.endInclusive)

/** Creates an [IntOrNumberProvidersRange] spanning [range], both bounds included. */
fun intRange(range: IntRange) = intRange(range.first.toFloat(), range.last.toFloat())
