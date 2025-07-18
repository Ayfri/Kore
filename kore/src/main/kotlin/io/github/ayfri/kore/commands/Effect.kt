package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument

class Effect(private val fn: Function, val target: EntityArgument) {
	fun clear(effect: MobEffectArgument? = null) = fn.addLine(command("effect", literal("clear"), target, effect))
	fun give(effect: MobEffectArgument, duration: Int? = null, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, int(duration), int(amplifier), bool(hideParticles)
		)
	)

	fun giveInfinite(effect: MobEffectArgument, amplifier: Int? = null, hideParticles: Boolean? = null) = fn.addLine(
		command(
			"effect", literal("give"), target, effect, literal("infinite"), int(amplifier), bool(hideParticles)
		)
	)
}

fun Function.effect(target: EntityArgument, block: Effect.() -> Command) = Effect(this, target).block()
