package io.github.ayfri.kore.features.worldgen.intproviders

fun biasedToBottom(minInclusive: Int, maxInclusive: Int) = BiasedToBottomIntProvider(minInclusive, maxInclusive)

fun clamped(minInclusive: Int, maxInclusive: Int, source: IntProvider) = ClampedIntProvider(minInclusive, maxInclusive, source)
fun clamped(minInclusive: Int, maxInclusive: Int, source: () -> IntProvider) = ClampedIntProvider(minInclusive, maxInclusive, source())

fun clampedNormal(minInclusive: Int, maxInclusive: Int, mean: Float, deviation: Float) =
	ClampedNormalIntProvider(minInclusive, maxInclusive, mean, deviation)

fun constant(value: Int) = ConstantIntProvider(value)

context(list: MutableList<WeightedEntryIntProvider>)
fun entry(weight: Int, data: IntProvider) = WeightedEntryIntProvider(weight, data)

fun trapezoid(min: Int, max: Int, plateau: Int) = TrapezoidIntProvider(min, max, plateau)

fun uniform(minInclusive: Int, maxInclusive: Int) = UniformIntProvider(minInclusive, maxInclusive)

fun weightedEntry(weight: Int, data: IntProvider) = WeightedEntryIntProvider(weight, data)

fun weightedList(weighted: Collection<WeightedEntryIntProvider>) = WeightedListIntProvider(weighted.toMutableList())
fun weightedList(weighted: MutableList<WeightedEntryIntProvider>.() -> Unit) =
	WeightedListIntProvider(buildList(weighted).toMutableList())
