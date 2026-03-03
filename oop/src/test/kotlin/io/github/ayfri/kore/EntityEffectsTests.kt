package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.entities.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.utils.testDataPack

fun entityEffectsTests() = testDataPack("effects_tests") {
	val player = player("TestPlayer")
	val speed = MobEffectArgument("speed")
	val regen = MobEffectArgument("regeneration")
	val nightVision = MobEffectArgument("night_vision")

	function("test_effects") {
		player.giveEffect(
			speed,
			duration = 200,
			amplifier = 1
		) assertsIs "effect give @e[limit=1,name=TestPlayer,type=minecraft:player] minecraft:speed 200 1"
		player.giveEffect(
			regen,
			duration = 100
		) assertsIs "effect give @e[limit=1,name=TestPlayer,type=minecraft:player] minecraft:regeneration 100"
		player.giveInfiniteEffect(
			nightVision,
			hideParticles = true
		) assertsIs "effect give @e[limit=1,name=TestPlayer,type=minecraft:player] minecraft:night_vision infinite true"
		player.clearEffect(speed) assertsIs "effect clear @e[limit=1,name=TestPlayer,type=minecraft:player] minecraft:speed"
		player.clearAllEffects() assertsIs "effect clear @e[limit=1,name=TestPlayer,type=minecraft:player]"
		lines.size assertsIs 5
	}
}.apply {
	generate()
}
