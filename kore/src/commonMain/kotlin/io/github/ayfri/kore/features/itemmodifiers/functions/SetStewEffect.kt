package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.utils.toUniformProvider
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import kotlinx.serialization.Serializable

/**
 * Sets suspicious-stew effects. Mirrors `minecraft:set_stew_effect`.
 * Each entry defines a mob effect id (`Effects.NIGHT_VISION`) and a duration as a context int provider.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetStewEffect(
	override var condition: PredicateCondition? = null,
	val effects: List<PotionEffect>,
) : ItemFunction()

/** Single stew effect entry. */
@Serializable
data class PotionEffect(
	var type: MobEffectArgument,
	var duration: ContextIntProvider = ConstantContextIntProvider(0),
)

/** Add a `set_stew_effect` step via a builder list. */
fun ItemModifier.setStewEffect(effects: MutableList<PotionEffect>.() -> Unit) =
	SetStewEffect(effects = buildList(effects)).also { modifiers += it }

/** Add a single-entry `set_stew_effect` step. */
fun ItemModifier.setStewEffect(
	type: MobEffectArgument,
	duration: ContextIntProvider = ConstantContextIntProvider(0),
	block: SetStewEffect.() -> Unit = {},
) {
	modifiers += SetStewEffect(
		effects = listOf(
			PotionEffect(
				type,
				duration
			)
		)
	).apply(block)
}

/**
 * Add a single-entry `set_stew_effect` step lasting exactly [duration] ticks.
 *
 * ```
 * setStewEffect(Effects.JUMP_BOOST, 160)  // "duration": 160
 * ```
 */
fun ItemModifier.setStewEffect(type: MobEffectArgument, duration: Int, block: SetStewEffect.() -> Unit = {}) =
	setStewEffect(type, ConstantContextIntProvider(duration), block)

/**
 * Add a single-entry `set_stew_effect` step lasting a number of ticks drawn uniformly from [duration], both ends included.
 *
 * ```
 * setStewEffect(Effects.JUMP_BOOST, 100..160)  // "duration": {"type": "minecraft:uniform", "max": 160, "min": 100}
 * ```
 */
fun ItemModifier.setStewEffect(type: MobEffectArgument, duration: IntRange, block: SetStewEffect.() -> Unit = {}) =
	setStewEffect(type, duration.toUniformProvider(), block)

/** Append one stew effect entry to the builder list. */
fun MutableList<PotionEffect>.potionEffect(type: MobEffectArgument, duration: ContextIntProvider = ConstantContextIntProvider(0)) {
	add(PotionEffect(type, duration))
}

/**
 * Append one stew effect entry lasting exactly [duration] ticks to the builder list.
 *
 * ```
 * potionEffect(Effects.NIGHT_VISION, 200)  // "duration": 200
 * ```
 */
fun MutableList<PotionEffect>.potionEffect(type: MobEffectArgument, duration: Int) = potionEffect(type, ConstantContextIntProvider(duration))

/**
 * Append one stew effect entry lasting a number of ticks drawn uniformly from [duration], both ends included.
 *
 * ```
 * potionEffect(Effects.NIGHT_VISION, 100..200)  // "duration": {"type": "minecraft:uniform", "max": 200, "min": 100}
 * ```
 */
fun MutableList<PotionEffect>.potionEffect(type: MobEffectArgument, duration: IntRange) = potionEffect(type, duration.toUniformProvider())
