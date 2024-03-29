package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.functions.Function

fun Function.teleportTests() {
	teleport(vec3()) assertsIs "teleport ~ ~ ~"
	teleport(vec3(1, 2, 3)) assertsIs "teleport 1 2 3"
	teleport(self()) assertsIs "teleport @s"
	teleport(self(), vec3()) assertsIs "teleport @s ~ ~ ~"
	teleport(self(), self()) assertsIs "teleport @s @s"
	teleport(self(), vec3(), rotation()) assertsIs "teleport @s ~ ~ ~ ~ ~"
	teleport(self(), vec3(), rotation(1, 2)) assertsIs "teleport @s ~ ~ ~ 1 2"
	teleport(self(), vec3(), vec3()) assertsIs "teleport @s ~ ~ ~ facing ~ ~ ~"
	teleport(self(), vec3(), self(), Anchor.EYES) assertsIs "teleport @s ~ ~ ~ facing @s eyes"
}
