package io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise

import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DoubleOrDoublePair.Companion.DoubleOrDoublePairSerializer::class)
data class DoubleOrDoublePair(
	val number: Double? = null,
	val pair: Pair<Double, Double>? = null
) {
	companion object {
		data object DoubleOrDoublePairSerializer : EitherInlineSerializer<DoubleOrDoublePair>(
			DoubleOrDoublePair::class,
			DoubleOrDoublePair::number,
			DoubleOrDoublePair::pair
		)
	}
}

fun doubleOrPair(number: Double) = DoubleOrDoublePair(number = number)
fun doubleOrPair(pair: Pair<Double, Double>) = DoubleOrDoublePair(pair = pair)
fun doubleOrPair(first: Double, second: Double) = DoubleOrDoublePair(pair = first to second)
