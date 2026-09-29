package io.github.ayfri.kore.website.playground.snippets.scheduling

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("telegraph") {
	pack { description = textComponent("Delay an action with schedule") }

	val explosionWarning = function("explosion_warning") {
		tellraw(allPlayers(), textComponent("Boom in 5 seconds!", Color.RED))
	}

	val explodeNow = function("explode_now") {
		summon(EntityTypes.TNT, vec3())
	}

	function("trigger_explosion") {
		function(explosionWarning)
		schedule(explodeNow).replace(5.seconds)
	}
}
