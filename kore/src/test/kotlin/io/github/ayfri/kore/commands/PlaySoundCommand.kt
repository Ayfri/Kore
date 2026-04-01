package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Sounds
import io.kotest.core.spec.style.FunSpec

fun Function.playSoundTests() {
	playSound(Sounds.Mob.Bat.TAKEOFF) assertsIs "playsound minecraft:mob/bat/takeoff"
	playSound(
		sound = Sounds.Mob.Bat.TAKEOFF,
		source = PlaySoundMixer.MASTER,
		target = allPlayers(),
		pos = vec3(),
		volume = 1.0,
		pitch = 2.0,
		minVolume = 1.0,
	) assertsIs "playsound minecraft:mob/bat/takeoff master @a ~ ~ ~ 1 2 1"
}

class PlaySoundCommandTests : FunSpec({
	test("play sound") {
		dataPack("unit_tests") {
			load { playSoundTests() }
		}.generate()
	}
})
