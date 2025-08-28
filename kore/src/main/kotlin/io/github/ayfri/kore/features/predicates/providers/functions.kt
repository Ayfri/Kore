@file:Suppress("NOTHING_TO_INLINE")

package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.types.EntityType

fun constant(value: Float) = Constant(value)

fun uniform(min: NumberProvider, max: NumberProvider) = Uniform(min, max)
fun uniform(min: Float, max: Float) = Uniform(constant(min), constant(max))
fun uniform(min: Float, max: NumberProvider) = Uniform(constant(min), max)
fun uniform(min: NumberProvider, max: Float) = Uniform(min, constant(max))

fun binomial(n: NumberProvider, p: NumberProvider) = Binomial(n, p)
fun binomial(n: Float, p: Float) = Binomial(constant(n), constant(p))
fun binomial(n: Float, p: NumberProvider) = Binomial(constant(n), p)
fun binomial(n: NumberProvider, p: Float) = Binomial(n, constant(p))

fun scoreNumber(score: String, target: EntityType? = null, scale: Float? = null) =
	Score(ScoreTargetNumberProvider(ScoreTargetType.CONTEXT, target = target), score, scale)

fun scoreNumber(score: String, name: String? = null, scale: Float? = null) =
	Score(ScoreTargetNumberProvider(ScoreTargetType.FIXED, name), score, scale)

fun storageNumber(storage: String, path: String) = Storage(storage, path)

fun enchantmentLevel(amount: Int) = Enchantment(constantLevelBased(amount))
fun enchantmentLevel(amount: LevelBased) = Enchantment(amount)
