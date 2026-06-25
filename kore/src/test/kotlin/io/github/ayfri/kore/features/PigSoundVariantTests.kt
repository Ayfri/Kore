package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.pigsoundvariants.adultSounds
import io.github.ayfri.kore.features.pigsoundvariants.babySounds
import io.github.ayfri.kore.features.pigsoundvariants.pigSoundVariant
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.pigSoundVariantTests() {
	pigSoundVariant("funny") {
		adultSounds {
			ambientSound = SoundEvents.Entity.Pig.AMBIENT
			deathSound = SoundEvents.Entity.Pig.DEATH
			hurtSound = SoundEvents.Entity.Pig.HURT
			stepSound = SoundEvents.Entity.Pig.STEP
		}
	}

	pigSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.pig.ambient",
				"death_sound": "minecraft:entity.pig.death",
				"hurt_sound": "minecraft:entity.pig.hurt",
				"step_sound": "minecraft:entity.pig.step"
			}
		}
	""".trimIndent()

	pigSoundVariant("baby_test") {
		adultSounds {
			ambientSound = SoundEvents.Entity.Pig.AMBIENT
			deathSound = SoundEvents.Entity.Pig.DEATH
			hurtSound = SoundEvents.Entity.Pig.HURT
			stepSound = SoundEvents.Entity.Pig.STEP
		}
		babySounds {
			ambientSound = SoundEvents.Entity.Chicken.AMBIENT
			deathSound = SoundEvents.Entity.Chicken.DEATH
			hurtSound = SoundEvents.Entity.Chicken.HURT
			stepSound = SoundEvents.Entity.Chicken.STEP
		}
	}

	pigSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.pig.ambient",
				"death_sound": "minecraft:entity.pig.death",
				"hurt_sound": "minecraft:entity.pig.hurt",
				"step_sound": "minecraft:entity.pig.step"
			},
			"baby_sounds": {
				"ambient_sound": "minecraft:entity.chicken.ambient",
				"death_sound": "minecraft:entity.chicken.death",
				"hurt_sound": "minecraft:entity.chicken.hurt",
				"step_sound": "minecraft:entity.chicken.step"
			}
		}
	""".trimIndent()
}

class PigSoundVariantTests : FunSpec({
	test("pig sound variant") {
		testDataPack("pigSoundVariant") {
			pretty()
			pigSoundVariantTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
