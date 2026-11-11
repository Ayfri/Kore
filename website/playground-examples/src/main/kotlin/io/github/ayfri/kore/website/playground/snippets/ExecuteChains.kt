package io.github.ayfri.kore.website.playground.snippets.executechains

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("coin_aura") {
	pack { description = textComponent("Players with 10 coins or more leave a particle trail") }

	tick("coin_aura") {
		execute {
			asTarget(allPlayers())
			at(self())
			ifCondition { score(self(), "coins", rangeOrIntStart(10)) }
			run { particle(Particles.HAPPY_VILLAGER, vec3()) }
		}
	}
}
