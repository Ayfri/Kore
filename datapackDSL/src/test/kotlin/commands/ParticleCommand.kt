package commands

import arguments.allEntities
import arguments.vec3
import functions.Function
import generated.Particles
import utils.assertsIs

fun Function.particleTests() {
	particle(Particles.ASH) assertsIs "particle minecraft:ash"
	particle(Particles.ASH, vec3()) assertsIs "particle minecraft:ash ~ ~ ~"
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2"
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2, ParticleMode.FORCE) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 force"

	particle(
		Particles.ASH,
		vec3(),
		vec3(),
		1.0,
		2,
		ParticleMode.NORMAL,
		allEntities()
	) assertsIs "particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 normal @e"
}
