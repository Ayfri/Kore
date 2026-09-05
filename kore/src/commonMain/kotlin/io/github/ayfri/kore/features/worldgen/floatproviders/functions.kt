package io.github.ayfri.kore.features.worldgen.floatproviders

/**
 * Creates a `clamped_normal` float provider sampling a normal distribution of [mean] and [deviation], clamped to
 * [min]..[max].
 *
 * ```kotlin
 * floorLevel = clampedNormal(mean = 0.5f, deviation = 0.2f, min = 0f, max = 1f)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 */
fun FloatProviderScope.clampedNormal(mean: Float, deviation: Float, min: Float, max: Float) =
	ClampedNormalFloatProvider(mean, deviation, min, max)

/**
 * Creates a `constant` float provider always returning [value].
 *
 * It is inlined when serialized, so it produces `1.5` rather than an object with a `type` field.
 *
 * ```kotlin
 * yScale = constant(1.5f)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 */
fun FloatProviderScope.constant(value: Float) = ConstantFloatProvider(value)

/**
 * Creates a `trapezoid` float provider drawing between [min] and [max], with a flat top of [plateau] in the middle
 * of the range.
 *
 * ```kotlin
 * thickness = trapezoid(min = 0f, max = 2f, plateau = 0.5f)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 */
fun FloatProviderScope.trapezoid(min: Float, max: Float, plateau: Float) = TrapezoidFloatProvider(min, max, plateau)

/**
 * Creates a `uniform` float provider drawing in `[`[minInclusive]`, `[maxExclusive]`)`.
 *
 * ```kotlin
 * pitch = uniform(0.8f, 1.2f)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 */
fun FloatProviderScope.uniform(minInclusive: Float, maxExclusive: Float) = UniformFloatProvider(minInclusive, maxExclusive)
