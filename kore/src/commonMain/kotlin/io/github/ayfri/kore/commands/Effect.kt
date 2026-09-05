package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument

/**
 * DSL scope for the `/effect` command bound to a [target] entity selector.
 *
 * `/effect` applies or removes status effects from one entity selector at a time. Use [give] to
 * apply an effect, [giveInfinite] for the infinite-duration form, and [clear] to remove active
 * effects.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/effect)
 */
class Effect(private val fn: Function, val target: EntityArgument) {
	/**
	 * Clears every effect, or only [effect] if specified, from the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/effect)
	 */
	fun clear(effect: MobEffectArgument? = null) = fn.addLine(command("effect", literal("clear"), target, effect))

	/**
	 * Gives [effect] to the target for [duration] seconds, with [amplifier] level and an optional
	 * [hideParticles] flag to suppress particle rendering.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/effect)
	 */
	fun give(effect: MobEffectArgument, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, int(duration), int(amplifier), bool(hideParticles)
		)
	)

	/**
	 * Gives [effect] to the target for an infinite duration, with optional [amplifier] and
	 * [hideParticles].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/effect)
	 */
	fun giveInfinite(effect: MobEffectArgument, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, literal("infinite"), int(amplifier), bool(hideParticles)
		)
	)
}

/** Opens the [Effect] DSL bound to [target]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/effect) */
fun Function.effect(target: EntityArgument, block: Effect.() -> Command) = Effect(this, target).block()

/** Clears every active effect from the executing entity (`effect clear`). @see [Minecraft wiki](https://minecraft.wiki/w/Commands/effect) */
fun Function.effectClear() = addLine(command("effect", literal("clear")))
