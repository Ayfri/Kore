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
		player.giveEffect(speed, duration = 200, amplifier = 1)
		player.giveEffect(regen, duration = 100)
		player.giveInfiniteEffect(nightVision, hideParticles = true)
		player.clearEffect(speed)
		player.clearAllEffects()

		lines.any { it.contains("effect give") && it.contains("speed") } assertsIs true
		lines.any { it.contains("effect give") && it.contains("regeneration") } assertsIs true
		lines.any { it.contains("effect give") && it.contains("night_vision") && it.contains("infinite") } assertsIs true
		lines.any { it.contains("effect clear") && it.contains("speed") } assertsIs true
		lines.any { it.contains("effect clear") && !it.contains("speed") } assertsIs true
		lines.size assertsIs 5
	}
}.apply {
	generate()
}
