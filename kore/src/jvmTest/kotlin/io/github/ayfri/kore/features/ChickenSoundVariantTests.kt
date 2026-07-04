package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.chickensoundvariants.adultSounds
import io.github.ayfri.kore.features.chickensoundvariants.babySounds
import io.github.ayfri.kore.features.chickensoundvariants.chickenSoundVariant
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.chickenSoundVariantTests() {
	chickenSoundVariant("funny") {
		adultSounds {
			ambientSound = SoundEvents.Entity.Chicken.AMBIENT
			deathSound = SoundEvents.Entity.Chicken.DEATH
			hurtSound = SoundEvents.Entity.Chicken.HURT
			stepSound = SoundEvents.Entity.Chicken.STEP
		}
	}

	chickenSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.chicken.ambient",
				"death_sound": "minecraft:entity.chicken.death",
				"hurt_sound": "minecraft:entity.chicken.hurt",
				"step_sound": "minecraft:entity.chicken.step"
			}
		}
	""".trimIndent()

	chickenSoundVariant("baby_test") {
		adultSounds {
			ambientSound = SoundEvents.Entity.Chicken.AMBIENT
			deathSound = SoundEvents.Entity.Chicken.DEATH
			hurtSound = SoundEvents.Entity.Chicken.HURT
			stepSound = SoundEvents.Entity.Chicken.STEP
		}
		babySounds {
			ambientSound = SoundEvents.Entity.Pig.AMBIENT
			deathSound = SoundEvents.Entity.Pig.DEATH
			hurtSound = SoundEvents.Entity.Pig.HURT
			stepSound = SoundEvents.Entity.Pig.STEP
		}
	}

	chickenSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.chicken.ambient",
				"death_sound": "minecraft:entity.chicken.death",
				"hurt_sound": "minecraft:entity.chicken.hurt",
				"step_sound": "minecraft:entity.chicken.step"
			},
			"baby_sounds": {
				"ambient_sound": "minecraft:entity.pig.ambient",
				"death_sound": "minecraft:entity.pig.death",
				"hurt_sound": "minecraft:entity.pig.hurt",
				"step_sound": "minecraft:entity.pig.step"
			}
		}
	""".trimIndent()
}

class ChickenSoundVariantTests : FunSpec({
	test("chicken sound variant") {
		testDataPack("chickenSoundVariant") {
			pretty()
			chickenSoundVariantTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
