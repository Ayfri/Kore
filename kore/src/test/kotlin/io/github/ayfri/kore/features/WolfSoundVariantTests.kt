package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.wolfsoundvariants.wolfSoundVariant
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.wolfSoundVariantTests() {
	wolfSoundVariant("funny") {
		ambientSound = SoundEvents.Entity.Pig.AMBIENT
		deathSound = SoundEvents.Entity.Creeper.DEATH
		growlSound = SoundEvents.Entity.Player.LEVELUP
		hurtSound = SoundEvents.Entity.Zombie.HURT
		pantSound = SoundEvents.Entity.EnderDragon.FLAP
		whineSound = SoundEvents.Entity.Cat.PURR
	}

	wolfSoundVariants.last() assertsIs """
		{
			"ambient_sound": "minecraft:entity.pig.ambient",
			"death_sound": "minecraft:entity.creeper.death",
			"growl_sound": "minecraft:entity.player.levelup",
			"hurt_sound": "minecraft:entity.zombie.hurt",
			"pant_sound": "minecraft:entity.ender_dragon.flap",
			"whine_sound": "minecraft:entity.cat.purr"
		}
	""".trimIndent()
}

class WolfSoundVariantTests : FunSpec({
	test("wolf sound variant") {
		testDataPack("wolfSoundVariant") {
			pretty()
			wolfSoundVariantTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
