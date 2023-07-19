package commands

import arguments.types.EntityArgument
import arguments.types.literals.bool
import arguments.types.literals.int
import arguments.types.literals.literal
import arguments.types.resources.EffectArgument
import functions.Function

class Effect(private val fn: Function, val target: EntityArgument) {
	fun clear(effect: EffectArgument? = null) = fn.addLine(command("effect", literal("clear"), target, effect))
	fun give(effect: EffectArgument, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, int(duration), int(amplifier), bool(hideParticles)
		)
	)

	fun giveInfinite(effect: EffectArgument, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, literal("infinite"), int(amplifier), bool(hideParticles)
		)
	)
}

fun Function.effect(target: EntityArgument, block: Effect.() -> Command) = Effect(this, target).block()
