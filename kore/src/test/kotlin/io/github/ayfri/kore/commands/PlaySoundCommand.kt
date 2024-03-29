package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Sounds

fun Function.playSoundTests() {
	playSound(Sounds.Mob.Bat.TAKEOFF) assertsIs "playsound minecraft:mob/bat/takeoff"
	playSound(
		sound = Sounds.Mob.Bat.TAKEOFF,
		source = PlaySoundMixer.MASTER,
		target = allEntities(),
		pos = vec3(),
		volume = 1.0,
		pitch = 2.0,
		minVolume = 1.0,
	) assertsIs "playsound minecraft:mob/bat/takeoff master @e ~ ~ ~ 1 2 1"
}
