@file:Suppress("NOTHING_TO_INLINE")

package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.types.EntityType

fun constant(value: Float) = ConstantNumberProvider(value)

fun uniform(min: NumberProvider, max: NumberProvider) = UniformNumberProvider(min, max)
fun uniform(min: Float, max: Float) = UniformNumberProvider(constant(min), constant(max))
fun uniform(min: Float, max: NumberProvider) = UniformNumberProvider(constant(min), max)
fun uniform(min: NumberProvider, max: Float) = UniformNumberProvider(min, constant(max))

fun binomial(n: NumberProvider, p: NumberProvider) = BinomialNumberProvider(n, p)
fun binomial(n: Float, p: Float) = BinomialNumberProvider(constant(n), constant(p))
fun binomial(n: Float, p: NumberProvider) = BinomialNumberProvider(constant(n), p)
fun binomial(n: NumberProvider, p: Float) = BinomialNumberProvider(n, constant(p))

fun scoreNumber(score: String, target: EntityType? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(ScoreTargetType.CONTEXT, target = target), score, scale)

fun scoreNumber(score: String, name: String? = null, scale: Float? = null) =
	ScoreNumberProvider(ScoreTargetNumberProvider(ScoreTargetType.FIXED, name), score, scale)

fun storageNumber(storage: String, path: String) =
	StorageNumberProvider(storage, path)

fun enchantmentLevel(amount: Int) = EnchantmentLevelNumberProvider(constantLevelBased(amount))
fun enchantmentLevel(amount: LevelBased) = EnchantmentLevelNumberProvider(amount)
