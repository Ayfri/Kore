package commands

import arguments.maths.vec3
import arguments.types.literals.allEntities
import assertions.assertsIs
import functions.Function
import generated.Sounds

fun Function.playSoundTests() {
	playSound(Sounds.Mob.Bat.TAKEOFF, PlaySoundSource.MASTER, allEntities()) assertsIs "playsound minecraft:mob/bat/takeoff master @e"
	playSound(
		sound = Sounds.Mob.Bat.TAKEOFF,
		source = PlaySoundSource.MASTER,
		target = allEntities(),
		pos = vec3(),
		volume = 1.0,
		pitch = 2.0,
		minVolume = 1.0,
	) assertsIs "playsound minecraft:mob/bat/takeoff master @e ~ ~ ~ 1 2 1"
}
