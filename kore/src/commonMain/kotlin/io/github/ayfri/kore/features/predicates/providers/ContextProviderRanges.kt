package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * The `{ "min": ..., "max": ... }` object of a [ContextFloatProviderRange]. Either bound can be left out to leave that
 * side open.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 *
 * @property max The highest value matched, included.
 * @property min The lowest value matched, included.
 */
@Serializable
data class ContextFloatProviderBounds(
	var max: ContextFloatProvider? = null,
	var min: ContextFloatProvider? = null,
)

/**
 * A float range written either as one provider ([value]), matched exactly, or as a `{ "min": ..., "max": ... }` object
 * ([bounds]). Exactly one of the two is set.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 *
 * @property bounds The bounds to match, when the range is an object.
 * @property value The provider the checked value must equal, when the range is a single provider.
 */
@KeepGeneratedSerializer
@Serializable(with = ContextFloatProviderRange.Companion.ContextFloatProviderRangeSerializer::class)
data class ContextFloatProviderRange(
	var bounds: ContextFloatProviderBounds? = null,
	var value: ContextFloatProvider? = null,
) {
	companion object {
		data object ContextFloatProviderRangeSerializer :
			EitherInlineSerializer<ContextFloatProviderRange>(generatedSerializer(), "value", "bounds")
	}
}

/**
 * The `{ "min": ..., "max": ... }` object of a [ContextIntProviderRange], also used alone by the `limit_count` item
 * function. Either bound can be left out to leave that side open.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 *
 * @property max The highest value matched, included.
 * @property min The lowest value matched, included.
 */
@Serializable
data class ContextIntProviderBounds(
	var max: ContextIntProvider? = null,
	var min: ContextIntProvider? = null,
)

/**
 * An int range written either as one provider ([value]), matched exactly, or as a `{ "min": ..., "max": ... }` object
 * ([bounds]). Exactly one of the two is set.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 *
 * @property bounds The bounds to match, when the range is an object.
 * @property value The provider the checked value must equal, when the range is a single provider.
 */
@KeepGeneratedSerializer
@Serializable(with = ContextIntProviderRange.Companion.ContextIntProviderRangeSerializer::class)
data class ContextIntProviderRange(
	var bounds: ContextIntProviderBounds? = null,
	var value: ContextIntProvider? = null,
) {
	companion object {
		data object ContextIntProviderRangeSerializer :
			EitherInlineSerializer<ContextIntProviderRange>(generatedSerializer(), "value", "bounds")
	}
}

/**
 * Creates a [ContextFloatProviderRange] between [min] and [max], both included, an omitted bound leaving that side open.
 *
 * ```
 * floatRange(0.5f, 2f)
 * ```
 */
fun floatRange(min: Float? = null, max: Float? = null) = ContextFloatProviderRange(
	ContextFloatProviderBounds(max?.let(::ConstantContextFloatProvider), min?.let(::ConstantContextFloatProvider))
)

/** Creates a [ContextFloatProviderRange] between [min] and [max], both included, an omitted bound leaving that side open. */
fun floatRange(min: ContextFloatProvider? = null, max: ContextFloatProvider? = null) =
	ContextFloatProviderRange(ContextFloatProviderBounds(max, min))

/** Creates a [ContextFloatProviderRange] spanning [range], both bounds included. */
fun floatRange(range: ClosedFloatingPointRange<Float>) = floatRange(range.start, range.endInclusive)

/** Creates a [ContextFloatProviderRange] matching exactly the provider [value]. */
fun floatValue(value: ContextFloatProvider) = ContextFloatProviderRange(value = value)

/**
 * Creates a [ContextFloatProviderRange] matching exactly [value].
 *
 * ```
 * floatValue(1.5f)  // "test": 1.5
 * ```
 */
fun floatValue(value: Float) = floatValue(ConstantContextFloatProvider(value))

/**
 * Creates a [ContextIntProviderRange] between [min] and [max], both included, an omitted bound leaving that side open.
 *
 * ```
 * intRange(1, 10)  // "test": { "max": 10, "min": 1 }
 * ```
 */
fun intRange(min: Int? = null, max: Int? = null) = ContextIntProviderRange(
	ContextIntProviderBounds(max?.let(::ConstantContextIntProvider), min?.let(::ConstantContextIntProvider))
)

/** Creates a [ContextIntProviderRange] between [min] and [max], both included, an omitted bound leaving that side open. */
fun intRange(min: ContextIntProvider? = null, max: ContextIntProvider? = null) =
	ContextIntProviderRange(ContextIntProviderBounds(max, min))

/** Creates a [ContextIntProviderRange] spanning [range], both bounds included. */
fun intRange(range: IntRange) = intRange(range.first, range.last)

/** Creates a [ContextIntProviderRange] matching exactly the provider [value]. */
fun intValue(value: ContextIntProvider) = ContextIntProviderRange(value = value)

/**
 * Creates a [ContextIntProviderRange] matching exactly [value].
 *
 * ```
 * intValue(3)  // "test": 3
 * ```
 */
fun intValue(value: Int) = intValue(ConstantContextIntProvider(value))
