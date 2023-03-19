package commands

import arguments.Argument
import arguments.bool
import arguments.int
import arguments.literal
import functions.Function

class Effect(private val fn: Function, val target: Argument.Entity) {
	fun clear(effect: Argument.MobEffect? = null) = fn.addLine(command("effect", literal("clear"), target, effect))
	fun give(effect: Argument.MobEffect, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, int(duration), int(amplifier), bool(hideParticles)
		)
	)

	fun giveInfinite(effect: Argument.MobEffect, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, literal("infinite"), int(amplifier), bool(hideParticles)
		)
	)
}

fun Function.effect(target: Argument.Entity, block: Effect.() -> Command) = Effect(this, target).block()
