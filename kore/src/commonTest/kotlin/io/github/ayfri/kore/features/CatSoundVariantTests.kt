package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.catsoundvariants.adultSounds
import io.github.ayfri.kore.features.catsoundvariants.babySounds
import io.github.ayfri.kore.features.catsoundvariants.catSoundVariant
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.catSoundVariantTests() {
	catSoundVariant("funny") {
		adultSounds {
			ambientSound = SoundEvents.Entity.Cat.AMBIENT
			begForFoodSound = SoundEvents.Entity.Cat.BEG_FOR_FOOD
			deathSound = SoundEvents.Entity.Cat.DEATH
			eatSound = SoundEvents.Entity.Cat.EAT
			hissSound = SoundEvents.Entity.Cat.HISS
			hurtSound = SoundEvents.Entity.Cat.HURT
			purreowSound = SoundEvents.Entity.Cat.PURREOW
			purrSound = SoundEvents.Entity.Cat.PURR
			strayAmbientSound = SoundEvents.Entity.Cat.STRAY_AMBIENT
		}
	}

	catSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.cat.ambient",
				"beg_for_food_sound": "minecraft:entity.cat.beg_for_food",
				"death_sound": "minecraft:entity.cat.death",
				"eat_sound": "minecraft:entity.cat.eat",
				"hiss_sound": "minecraft:entity.cat.hiss",
				"hurt_sound": "minecraft:entity.cat.hurt",
				"purreow_sound": "minecraft:entity.cat.purreow",
				"purr_sound": "minecraft:entity.cat.purr",
				"stray_ambient_sound": "minecraft:entity.cat.stray_ambient"
			}
		}
	""".trimIndent()

	catSoundVariant("baby_test") {
		adultSounds {
			ambientSound = SoundEvents.Entity.Cat.AMBIENT
			begForFoodSound = SoundEvents.Entity.Cat.BEG_FOR_FOOD
			deathSound = SoundEvents.Entity.Cat.DEATH
			eatSound = SoundEvents.Entity.Cat.EAT
			hissSound = SoundEvents.Entity.Cat.HISS
			hurtSound = SoundEvents.Entity.Cat.HURT
			purreowSound = SoundEvents.Entity.Cat.PURREOW
			purrSound = SoundEvents.Entity.Cat.PURR
			strayAmbientSound = SoundEvents.Entity.Cat.STRAY_AMBIENT
		}
		babySounds {
			ambientSound = SoundEvents.Entity.Pig.AMBIENT
			deathSound = SoundEvents.Entity.Pig.DEATH
			hurtSound = SoundEvents.Entity.Pig.HURT
		}
	}

	catSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.cat.ambient",
				"beg_for_food_sound": "minecraft:entity.cat.beg_for_food",
				"death_sound": "minecraft:entity.cat.death",
				"eat_sound": "minecraft:entity.cat.eat",
				"hiss_sound": "minecraft:entity.cat.hiss",
				"hurt_sound": "minecraft:entity.cat.hurt",
				"purreow_sound": "minecraft:entity.cat.purreow",
				"purr_sound": "minecraft:entity.cat.purr",
				"stray_ambient_sound": "minecraft:entity.cat.stray_ambient"
			},
			"baby_sounds": {
				"ambient_sound": "minecraft:entity.pig.ambient",
				"beg_for_food_sound": "minecraft:entity.cat.beg_for_food",
				"death_sound": "minecraft:entity.pig.death",
				"eat_sound": "minecraft:entity.cat.eat",
				"hiss_sound": "minecraft:entity.cat.hiss",
				"hurt_sound": "minecraft:entity.pig.hurt",
				"purreow_sound": "minecraft:entity.cat.purreow",
				"purr_sound": "minecraft:entity.cat.purr",
				"stray_ambient_sound": "minecraft:entity.cat.stray_ambient"
			}
		}
	""".trimIndent()
}

class CatSoundVariantTests : FunSpec({
	test("cat sound variant") {
		dataPack("catSoundVariant") {
			pretty()
			catSoundVariantTests()
		}
	}
})
