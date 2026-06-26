package io.github.ayfri.kore.features.worldgen.floatproviders

/** Creates a [ClampedNormalFloatProvider] sampling from a normal distribution with [mean] and [deviation], clamped to [[min], [max]]. */
fun clampedNormal(mean: Float, deviation: Float, min: Float, max: Float) =
	ClampedNormalFloatProvider(mean, deviation, min, max)

/** Alias for [clampedNormal] for disambiguation when both float and int provider functions are in scope. */
fun clampedNormalFloatProvider(mean: Float, deviation: Float, min: Float, max: Float) =
	ClampedNormalFloatProvider(mean, deviation, min, max)

/** Creates a [ConstantFloatProvider] that always returns [value]. */
fun constant(value: Float) = ConstantFloatProvider(value)

/** Alias for [constant] for disambiguation when both float and int provider functions are in scope. */
fun constantFloatProvider(value: Float) = ConstantFloatProvider(value)

/** Creates a [TrapezoidFloatProvider] sampling from a trapezoid distribution between [min] and [max] with a flat top of width [plateau]. */
fun trapezoid(min: Float, max: Float, plateau: Float) = TrapezoidFloatProvider(min, max, plateau)

/** Alias for [trapezoid] for disambiguation when both float and int provider functions are in scope. */
fun trapezoidFloatProvider(min: Float, max: Float, plateau: Float) = TrapezoidFloatProvider(min, max, plateau)

/** Creates a [UniformFloatProvider] returning a uniformly random float in [[minInclusive], [maxExclusive]). */
fun uniform(minInclusive: Float, maxExclusive: Float) = UniformFloatProvider(minInclusive, maxExclusive)

/** Alias for [uniform] for disambiguation when both float and int provider functions are in scope. */
fun uniformFloatProvider(minInclusive: Float, maxExclusive: Float) = UniformFloatProvider(minInclusive, maxExclusive)
