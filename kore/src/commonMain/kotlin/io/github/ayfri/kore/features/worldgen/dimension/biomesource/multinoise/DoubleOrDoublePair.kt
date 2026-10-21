package io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import io.github.ayfri.kore.serializers.PairAsArray
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@KeepGeneratedSerializer
@Serializable(with = DoubleOrDoublePair.Companion.DoubleOrDoublePairSerializer::class)
data class DoubleOrDoublePair(
	val number: Double? = null,
	val pair: PairAsArray<Double, Double>? = null
) {
	companion object {
		data object DoubleOrDoublePairSerializer :
			EitherInlineSerializer<DoubleOrDoublePair>(generatedSerializer(), "number", "pair")
	}
}

fun doubleOrPair(number: Double) = DoubleOrDoublePair(number = number)
fun doubleOrPair(pair: Pair<Double, Double>) = DoubleOrDoublePair(pair = pair)
fun doubleOrPair(first: Double, second: Double) = DoubleOrDoublePair(pair = first to second)
fun doubleOrPair(range: ClosedFloatingPointRange<Double>) = doubleOrPair(range.start, range.endInclusive)
