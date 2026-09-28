package io.github.ayfri.kore.website.playground.snippets.macros

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.player
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.teleport
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.getValue
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

class TeleportMacros : Macros() {
	val player by "player"
}

fun playground() = dataPack("macros_demo") {
	pack { description = textComponent("Functions parameterized with macros") }

	val teleportToSpawn = function("teleport_to_spawn", ::TeleportMacros) {
		teleport(player(macros.player), vec3())
	}

	load("bootstrap") {
		function(teleportToSpawn, arguments = nbt { this["player"] = "jeb_" })
	}
}
