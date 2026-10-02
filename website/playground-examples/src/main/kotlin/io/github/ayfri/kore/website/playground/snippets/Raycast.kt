package io.github.ayfri.kore.website.playground.snippets.raycast

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.helpers.raycast.raycast
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("laser") {
	pack { description = textComponent("A raycast from the helpers module") }

	val laser = raycast {
		name = "laser"
		maxDistance = 32
		step = 0.5
		onStep = { particle(Particles.END_ROD, vec3()) }
		onHitBlock = { particle(Particles.EXPLOSION, vec3()) }
		onMaxDistance = { tellraw(self(), textComponent("Nothing in range", Color.GRAY)) }
	}

	function("fire") {
		execute {
			asTarget(allPlayers())
			at(self())
			run { laser.cast() }
		}
	}
}
