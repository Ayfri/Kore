package io.github.ayfri.kore.website.playground.snippets.predicates

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("storm_strikes") {
	pack { description = textComponent("Named predicates checked from commands") }

	val thunderstorm = predicate("thunderstorm") { weatherCheck(raining = true, thundering = true) }
	val rareChance = predicate("rare_chance") { randomChance(0.002f) }

	tick("strike_players") {
		execute {
			asTarget(allPlayers())
			at(self())
			ifCondition(thunderstorm)
			ifCondition(rareChance)
			run { summon(EntityTypes.LIGHTNING_BOLT, vec3()) }
		}
	}
}
