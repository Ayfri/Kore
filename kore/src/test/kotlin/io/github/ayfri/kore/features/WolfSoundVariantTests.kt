package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.wolfsoundvariants.WolfSoundVariantSounds
import io.github.ayfri.kore.features.wolfsoundvariants.wolfSoundVariant
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.wolfSoundVariantTests() {
	wolfSoundVariant("funny") {
		adultSounds = WolfSoundVariantSounds(
			ambientSound = SoundEvents.Entity.Pig.AMBIENT,
			deathSound = SoundEvents.Entity.Creeper.DEATH,
			growlSound = SoundEvents.Entity.Player.LEVELUP,
			hurtSound = SoundEvents.Entity.Zombie.HURT,
			pantSound = SoundEvents.Entity.EnderDragon.FLAP,
			whineSound = SoundEvents.Entity.Cat.PURR,
		)
	}

	wolfSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.pig.ambient",
				"death_sound": "minecraft:entity.creeper.death",
				"growl_sound": "minecraft:entity.player.levelup",
				"hurt_sound": "minecraft:entity.zombie.hurt",
				"pant_sound": "minecraft:entity.ender_dragon.flap",
				"whine_sound": "minecraft:entity.cat.purr"
			}
		}
	""".trimIndent()

	wolfSoundVariant("baby_test") {
		adultSounds = WolfSoundVariantSounds(
			ambientSound = SoundEvents.Entity.Wolf.AMBIENT,
			deathSound = SoundEvents.Entity.Wolf.DEATH,
			growlSound = SoundEvents.Entity.Wolf.GROWL,
			hurtSound = SoundEvents.Entity.Wolf.HURT,
			pantSound = SoundEvents.Entity.Wolf.PANT,
			whineSound = SoundEvents.Entity.Wolf.WHINE,
		)
		babySounds = WolfSoundVariantSounds(
			ambientSound = SoundEvents.Entity.Pig.AMBIENT,
			deathSound = SoundEvents.Entity.Pig.DEATH,
			growlSound = SoundEvents.Entity.Wolf.GROWL,
			hurtSound = SoundEvents.Entity.Pig.HURT,
			pantSound = SoundEvents.Entity.Wolf.PANT,
			whineSound = SoundEvents.Entity.Wolf.WHINE,
		)
	}

	wolfSoundVariants.last() assertsIs """
		{
			"adult_sounds": {
				"ambient_sound": "minecraft:entity.wolf.ambient",
				"death_sound": "minecraft:entity.wolf.death",
				"growl_sound": "minecraft:entity.wolf.growl",
				"hurt_sound": "minecraft:entity.wolf.hurt",
				"pant_sound": "minecraft:entity.wolf.pant",
				"whine_sound": "minecraft:entity.wolf.whine"
			},
			"baby_sounds": {
				"ambient_sound": "minecraft:entity.pig.ambient",
				"death_sound": "minecraft:entity.pig.death",
				"growl_sound": "minecraft:entity.wolf.growl",
				"hurt_sound": "minecraft:entity.pig.hurt",
				"pant_sound": "minecraft:entity.wolf.pant",
				"whine_sound": "minecraft:entity.wolf.whine"
			}
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
