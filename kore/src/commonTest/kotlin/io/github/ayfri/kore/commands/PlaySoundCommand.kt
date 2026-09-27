package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.SoundEvents
import io.kotest.core.spec.style.FunSpec

fun Function.playSoundTests() {
	playSound(SoundEvents.Entity.Bat.TAKEOFF) assertsIs "playsound minecraft:entity.bat.takeoff"
	playSound(
		sound = SoundEvents.Entity.Bat.TAKEOFF,
		source = PlaySoundMixer.MASTER,
		target = allPlayers(),
		pos = vec3(),
		volume = 1.0,
		pitch = 2.0,
		minVolume = 1.0,
	) assertsIs "playsound minecraft:entity.bat.takeoff master @a ~ ~ ~ 1 2 1"
	playSound(SoundEvents.Entity.Bat.TAKEOFF, target = allPlayers()) assertsIs "playsound minecraft:entity.bat.takeoff master @a"
	playSound(SoundEvents.Entity.Bat.TAKEOFF, volume = 2.0) assertsIs "playsound minecraft:entity.bat.takeoff master @s ~ ~ ~ 2"
	playSound(SoundEvents.Entity.Bat.TAKEOFF, minVolume = 0.5) assertsIs "playsound minecraft:entity.bat.takeoff master @s ~ ~ ~ 1 1 0.5"
}

class PlaySoundCommandTests : FunSpec({
	test("play sound") {
		dataPack("unit_tests") {
			load { playSoundTests() }
		}
	}
})
