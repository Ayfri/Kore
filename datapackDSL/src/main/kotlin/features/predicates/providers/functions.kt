@file:Suppress("NOTHING_TO_INLINE")

package features.predicates.providers

import features.predicates.types.EntityType

inline fun constant(value: Float) = ConstantNumberProvider(value)

inline fun uniform(min: NumberProvider, max: NumberProvider) = UniformNumberProvider(min, max)
inline fun uniform(min: Float, max: Float) = UniformNumberProvider(constant(min), constant(max))
inline fun uniform(min: Float, max: NumberProvider) = UniformNumberProvider(constant(min), max)
inline fun uniform(min: NumberProvider, max: Float) = UniformNumberProvider(min, constant(max))


inline fun binomial(n: NumberProvider, p: NumberProvider) = BinomialNumberProvider(n, p)
inline fun binomial(n: Float, p: Float) = BinomialNumberProvider(constant(n), constant(p))
inline fun binomial(n: Float, p: NumberProvider) = BinomialNumberProvider(constant(n), p)
inline fun binomial(n: NumberProvider, p: Float) = BinomialNumberProvider(n, constant(p))

fun scoreNumber(score: String, target: EntityType? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(ScoreTargetType.CONTEXT, target = target), score, scale)

fun scoreNumber(score: String, name: String? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(ScoreTargetType.FIXED, name), score, scale)
