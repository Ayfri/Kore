package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument

/**
 * DSL scope for the `effect` command bound to a [target] entity selector.
 *
 * See the [Minecraft wiki](https://minecraft.wiki/w/Commands/effect).
 */
class Effect(private val fn: Function, val target: EntityArgument) {
	/** Clears every effect, or only [effect] if specified, from the bound target. */
	fun clear(effect: MobEffectArgument? = null) = fn.addLine(command("effect", literal("clear"), target, effect))

	/**
	 * Gives [effect] to the target for [duration] seconds, with [amplifier] level
	 * and optional [hideParticles] flag to suppress particle rendering.
	 */
	fun give(effect: MobEffectArgument, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, int(duration), int(amplifier), bool(hideParticles)
		)
	)

	/** Gives [effect] to the target for an infinite duration, with optional [amplifier] and [hideParticles]. */
	fun giveInfinite(effect: MobEffectArgument, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, literal("infinite"), int(amplifier), bool(hideParticles)
		)
	)
}

/** Opens the [Effect] DSL bound to [target]. */
fun Function.effect(target: EntityArgument, block: Effect.() -> Command) = Effect(this, target).block()

/** Clears every active effect from the executing entity (`effect clear`). */
fun Function.effectClear() = addLine(command("effect", literal("clear")))
