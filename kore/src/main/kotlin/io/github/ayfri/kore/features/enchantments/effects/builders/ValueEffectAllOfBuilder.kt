package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.value.*
import io.github.ayfri.kore.features.enchantments.effects.value.Set
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
sealed class ValueEffectAllOfBuilder(var effects: AllOf = AllOf())

data class ValueEffectAllOfTopBuilder(var requirements: InlinableList<PredicateCondition>? = null) : ValueEffectAllOfBuilder()

fun ValueEffectAllOfTopBuilder.allOf(block: ValueEffectAllOfSubBuilder.() -> Unit = {}) =
	apply { effects.effects += ValueEffectAllOfSubBuilder().apply(block).effects }

fun ValueEffectAllOfTopBuilder.requirements(block: Predicate.() -> Unit = {}) =
	apply { requirements = Predicate().apply(block).predicateConditions }

class ValueEffectAllOfSubBuilder : ValueEffectAllOfBuilder()

fun ValueEffectAllOfSubBuilder.allOf(block: ValueEffectAllOfSubBuilder.() -> Unit = {}) =
	apply { effects.effects += ValueEffectAllOfSubBuilder().apply(block).effects }


fun ValueEffectAllOfBuilder.add(value: LevelBased) = apply { effects.effects += Add(value) }

fun ValueEffectAllOfBuilder.add(value: Int) = apply { effects.effects += Add(constantLevelBased(value)) }

fun ValueEffectAllOfBuilder.multiply(value: LevelBased) = apply { effects.effects += Multiply(value) }
fun ValueEffectAllOfBuilder.multiply(value: Int) = apply { effects.effects += Multiply(constantLevelBased(value)) }

fun ValueEffectAllOfBuilder.removeBinomial(chance: LevelBased) = apply { effects.effects += RemoveBinomial(chance) }
fun ValueEffectAllOfBuilder.removeBinomial(chance: Int) = apply { effects.effects += RemoveBinomial(constantLevelBased(chance)) }


fun ValueEffectAllOfBuilder.set(value: LevelBased) = apply { effects.effects += Set(value) }
fun ValueEffectAllOfBuilder.set(value: Int) = apply { effects.effects += Set(constantLevelBased(value)) }
