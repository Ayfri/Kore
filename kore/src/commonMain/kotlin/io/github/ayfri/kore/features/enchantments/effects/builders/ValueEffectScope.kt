package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.value.Add
import io.github.ayfri.kore.features.enchantments.effects.value.AllOf
import io.github.ayfri.kore.features.enchantments.effects.value.Multiply
import io.github.ayfri.kore.features.enchantments.effects.value.RemoveBinomial
import io.github.ayfri.kore.features.enchantments.effects.value.Set
import io.github.ayfri.kore.features.enchantments.effects.value.ValueEffect
import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.predicates.Predicate

/**
 * Receiver of the [ValueEffect] builders, implemented by every component that computes a number.
 *
 * A single set of builders serves `damage`, `armor_effectiveness`, `ammo_use`, `equipment_drops` and the nested
 * `all_of` blocks: each scope only decides where [addEffect] puts the effect it is handed.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Value_effects
 */
interface ValueEffectScope : LevelBasedScope {
	/** Appends [effect] to the component being built, lifting its requirements next to it where the format allows. */
	fun addEffect(effect: ValueEffect)
}

/**
 * Collects the effects of an `all_of` block, applying them in order to the number the component computes.
 *
 * `requirements { }` is only honored on the outermost `all_of` of a component, since the nested ones have nowhere to
 * put it in the JSON.
 */
class ValueEffectAllOfScope internal constructor(internal val allOf: AllOf = AllOf()) : ValueEffectScope {
	override fun addEffect(effect: ValueEffect) {
		allOf.effects += effect
	}
}

/** Sets the conditions the whole `all_of` block applies under. */
fun ValueEffectAllOfScope.requirements(block: Predicate.() -> Unit = {}) {
	allOf.requirements = Predicate().apply(block).predicateConditions
}

/**
 * Appends an `add` effect adding [value] to the number the component computes.
 *
 * ```kotlin
 * damage {
 *     add(linearLevelBased(1, 1)) {
 *         requirements { weatherCheck(raining = true) }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#add
 */
fun ValueEffectScope.add(value: LevelBased, block: Add.() -> Unit = {}) = addEffect(Add(value).apply(block))

/** Appends an `add` effect adding a constant [value] to the number the component computes. */
fun ValueEffectScope.add(value: Float, block: Add.() -> Unit = {}) = addEffect(Add(Constant(value)).apply(block))

/** Appends an `add` effect adding a constant [value] to the number the component computes. */
fun ValueEffectScope.add(value: Int, block: Add.() -> Unit = {}) = addEffect(Add(Constant(value.toFloat())).apply(block))

/**
 * Appends an `all_of` effect applying every effect built in [block] in order.
 *
 * ```kotlin
 * armorEffectiveness {
 *     allOf {
 *         requirements { weatherCheck(raining = true) }
 *         add(2)
 *         multiply(0.5f)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#all_of
 */
fun ValueEffectScope.allOf(block: ValueEffectAllOfScope.() -> Unit = {}) =
	addEffect(ValueEffectAllOfScope().apply(block).allOf)

/**
 * Appends a `multiply` effect multiplying the number the component computes by [factor].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#multiply
 */
fun ValueEffectScope.multiply(factor: LevelBased, block: Multiply.() -> Unit = {}) =
	addEffect(Multiply(factor).apply(block))

/** Appends a `multiply` effect multiplying the number the component computes by a constant [factor]. */
fun ValueEffectScope.multiply(factor: Float, block: Multiply.() -> Unit = {}) =
	addEffect(Multiply(Constant(factor)).apply(block))

/** Appends a `multiply` effect multiplying the number the component computes by a constant [factor]. */
fun ValueEffectScope.multiply(factor: Int, block: Multiply.() -> Unit = {}) =
	addEffect(Multiply(Constant(factor.toFloat())).apply(block))

/**
 * Appends a `remove_binomial` effect rolling the number the component computes as a binomial trial, removing one
 * unit per success with a [chance] probability each.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#remove_binomial
 */
fun ValueEffectScope.removeBinomial(chance: LevelBased, block: RemoveBinomial.() -> Unit = {}) =
	addEffect(RemoveBinomial(chance).apply(block))

/** Appends a `remove_binomial` effect rolling each unit against a constant [chance], from `0` to `1`. */
fun ValueEffectScope.removeBinomial(chance: Float, block: RemoveBinomial.() -> Unit = {}) =
	addEffect(RemoveBinomial(Constant(chance)).apply(block))

/**
 * Appends a `set` effect replacing the number the component computes by [value].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#set
 */
fun ValueEffectScope.set(value: LevelBased, block: Set.() -> Unit = {}) = addEffect(Set(value).apply(block))

/** Appends a `set` effect replacing the number the component computes by a constant [value]. */
fun ValueEffectScope.set(value: Float, block: Set.() -> Unit = {}) = addEffect(Set(Constant(value)).apply(block))

/** Appends a `set` effect replacing the number the component computes by a constant [value]. */
fun ValueEffectScope.set(value: Int, block: Set.() -> Unit = {}) = addEffect(Set(Constant(value.toFloat())).apply(block))
