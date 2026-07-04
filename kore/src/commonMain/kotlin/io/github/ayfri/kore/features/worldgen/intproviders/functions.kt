package io.github.ayfri.kore.features.worldgen.intproviders

/** Creates a [BiasedToBottomIntProvider] sampling integers from [minInclusive] to [maxInclusive], biased towards lower values. */
fun biasedToBottom(minInclusive: Int, maxInclusive: Int) = BiasedToBottomIntProvider(minInclusive, maxInclusive)

/** Creates a [ClampedIntProvider] that restricts [source] output to the range [minInclusive]..[maxInclusive]. */
fun clamped(minInclusive: Int, maxInclusive: Int, source: IntProvider) = ClampedIntProvider(minInclusive, maxInclusive, source)

/** Creates a [ClampedIntProvider] that restricts the output of the [source] lambda to the range [minInclusive]..[maxInclusive]. */
fun clamped(minInclusive: Int, maxInclusive: Int, source: () -> IntProvider) = ClampedIntProvider(minInclusive, maxInclusive, source())

/** Creates a [ClampedNormalIntProvider] sampling from a normal distribution with [mean] and [deviation], clamped to [minInclusive]..[maxInclusive]. */
fun clampedNormal(minInclusive: Int, maxInclusive: Int, mean: Float, deviation: Float) =
	ClampedNormalIntProvider(minInclusive, maxInclusive, mean, deviation)

/** Creates a [ConstantIntProvider] that always returns [value]. */
fun constant(value: Int) = ConstantIntProvider(value)

/** Adds a [WeightedEntryIntProvider] with the given [weight] and [data] to the enclosing weighted list builder. */
context(list: MutableList<WeightedEntryIntProvider>)
fun entry(weight: Int, data: IntProvider) = WeightedEntryIntProvider(weight, data)

/** Creates a [TrapezoidIntProvider] sampling from a trapezoid distribution between [min] and [max] with a flat top of width [plateau]. */
fun trapezoid(min: Int, max: Int, plateau: Int) = TrapezoidIntProvider(min, max, plateau)

/** Creates a [UniformIntProvider] returning a uniformly random integer from [minInclusive] to [maxInclusive]. */
fun uniform(minInclusive: Int, maxInclusive: Int) = UniformIntProvider(minInclusive, maxInclusive)

/** Creates a [WeightedEntryIntProvider] pairing [data] with the given [weight]. */
fun weightedEntry(weight: Int, data: IntProvider) = WeightedEntryIntProvider(weight, data)

/** Creates a [WeightedListIntProvider] from a collection of weighted entries. */
fun weightedList(weighted: Collection<WeightedEntryIntProvider>) = WeightedListIntProvider(weighted.toMutableList())

/** Creates a [WeightedListIntProvider] using a builder block to populate the entry list. */
fun weightedList(weighted: MutableList<WeightedEntryIntProvider>.() -> Unit) =
	WeightedListIntProvider(buildList(weighted).toMutableList())
