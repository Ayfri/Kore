package io.github.ayfri.kore.entities

import io.github.ayfri.kore.commands.Effect
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument

/** Gives an effect to the entity. */
context(fn: Function)
fun Entity.giveEffect(
	effect: MobEffectArgument,
	duration: Int? = null,
	amplifier: Int? = null,
	hideParticles: Boolean? = null,
) = fn.effect(asSelector()) { give(effect, duration, amplifier, hideParticles) }

/** Gives an infinite effect to the entity. */
context(fn: Function)
fun Entity.giveInfiniteEffect(
	effect: MobEffectArgument,
	amplifier: Int? = null,
	hideParticles: Boolean? = null,
) = fn.effect(asSelector()) { giveInfinite(effect, amplifier, hideParticles) }

/** Clears a specific effect from the entity. */
context(fn: Function)
fun Entity.clearEffect(effect: MobEffectArgument) = fn.effect(asSelector()) { clear(effect) }

/** Clears all effects from the entity. */
context(fn: Function)
fun Entity.clearAllEffects() = fn.effect(asSelector()) { clear() }

/** Applies multiple effects using a builder block. */
context(fn: Function)
fun Entity.effects(block: Effect.() -> Unit) = Effect(fn, asSelector()).apply(block)
