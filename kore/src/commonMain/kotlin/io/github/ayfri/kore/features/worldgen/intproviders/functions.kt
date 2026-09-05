package io.github.ayfri.kore.features.worldgen.intproviders

/**
 * Creates a `biased_to_bottom` int provider drawing between [minInclusive] and [maxInclusive], favouring the lower
 * values.
 *
 * ```kotlin
 * count(biasedToBottom(3, 8))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.biasedToBottom(minInclusive: Int, maxInclusive: Int) = BiasedToBottomIntProvider(minInclusive, maxInclusive)

/**
 * Creates a `clamped` int provider restricting the value drawn by [source] to [minInclusive]..[maxInclusive].
 *
 * ```kotlin
 * count(clamped(4, 12, uniform(0, 20)))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.clamped(minInclusive: Int, maxInclusive: Int, source: IntProvider) =
	ClampedIntProvider(minInclusive, maxInclusive, source)

/**
 * Creates a `clamped_normal` int provider sampling a normal distribution of [mean] and [deviation], clamped to
 * [minInclusive]..[maxInclusive].
 *
 * ```kotlin
 * count(clampedNormal(1, 10, mean = 5f, deviation = 2f))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.clampedNormal(minInclusive: Int, maxInclusive: Int, mean: Float, deviation: Float) =
	ClampedNormalIntProvider(minInclusive, maxInclusive, mean, deviation)

/**
 * Creates a `constant` int provider always returning [value].
 *
 * It is inlined when serialized, so it produces `5` rather than an object with a `type` field.
 *
 * ```kotlin
 * count(constant(10))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.constant(value: Int) = ConstantIntProvider(value)

/**
 * Creates a `trapezoid` int provider drawing between [min] and [max], with a flat top of [plateau] values in the
 * middle of the range.
 *
 * ```kotlin
 * count(trapezoid(1, 5, plateau = 2))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.trapezoid(min: Int, max: Int, plateau: Int) = TrapezoidIntProvider(min, max, plateau)

/**
 * Creates a `uniform` int provider drawing between [minInclusive] and [maxInclusive], both included.
 *
 * ```kotlin
 * count(uniform(1, 5))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.uniform(minInclusive: Int, maxInclusive: Int) = UniformIntProvider(minInclusive, maxInclusive)

/**
 * Creates a `weighted_list` int provider drawing from [distribution], which must not be empty.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.weightedList(distribution: List<WeightedListIntProviderEntry>) = WeightedListIntProvider(distribution)

/**
 * Creates a `weighted_list` int provider drawing from [distribution], each pair associating a weight with the int
 * provider it draws.
 *
 * ```kotlin
 * count(weightedList(7 to constant(1), 3 to constant(3)))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.weightedList(vararg distribution: Pair<Int, IntProvider>) =
	weightedList(distribution.map { weightedListEntry(it.first, it.second) })

/**
 * Creates a `weighted_list` int provider whose entries are added in [block].
 *
 * ```kotlin
 * count(weightedList {
 *     entry(7, constant(1))
 *     entry(3, constant(3))
 * })
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
fun IntProviderScope.weightedList(block: WeightedListIntProviderBuilder.() -> Unit) =
	weightedList(WeightedListIntProviderBuilder().apply(block).entries)
