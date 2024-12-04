package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.functions.Function

fun Function.rotateTests() {
	rotate(self(), rotation(90, 45)) assertsIs "rotate @s 90 45"

	rotateFacing(self(), vec3(1, 2, 3)) assertsIs "rotate @s facing 1 2 3"
	rotateFacing(self(), vec3()) assertsIs "rotate @s facing ~ ~ ~"

	rotateFacingEntity(self(), self()) assertsIs "rotate @s facing entity @s"
	rotateFacingEntity(self(), self(), Anchor.EYES) assertsIs "rotate @s facing entity @s eyes"
	rotateFacingEntity(self(), self(), Anchor.FEET) assertsIs "rotate @s facing entity @s feet"
}
