@file:Suppress("NOTHING_TO_INLINE")

package features.predicates.providers

import arguments.numbers.FloatRangeOrFloat
import features.predicates.types.EntityType

inline fun constant(value: Float) = ConstantNumberProvider(value)
inline fun uniform(min: FloatRangeOrFloat, max: FloatRangeOrFloat) = UniformNumberProvider(min, max)
inline fun binomial(n: Int, p: Float) = BinomialNumberProvider(n, p)
fun scoreNumber(score: String, target: EntityType? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(ScoreTargetType.CONTEXT, target = target), score, scale)

fun scoreNumber(score: String, name: String? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(ScoreTargetType.FIXED, name), score, scale)
