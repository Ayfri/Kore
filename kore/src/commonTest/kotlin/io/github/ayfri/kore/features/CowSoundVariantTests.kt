package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.cowsoundvariants.cowSoundVariant
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.cowSoundVariantTests() {
	cowSoundVariant("funny") {
		ambientSound = SoundEvents.Entity.Cow.AMBIENT
		deathSound = SoundEvents.Entity.Cow.DEATH
		hurtSound = SoundEvents.Entity.Cow.HURT
		stepSound = SoundEvents.Entity.Cow.STEP
	}

	cowSoundVariants.last() assertsIs """
		{
			"ambient_sound": "minecraft:entity.cow.ambient",
			"death_sound": "minecraft:entity.cow.death",
			"hurt_sound": "minecraft:entity.cow.hurt",
			"step_sound": "minecraft:entity.cow.step"
		}
	""".trimIndent()

	cowSoundVariant(
		"custom",
		ambientSound = SoundEvents.Entity.Pig.AMBIENT,
		deathSound = SoundEvents.Entity.Pig.DEATH,
		hurtSound = SoundEvents.Entity.Pig.HURT,
		stepSound = SoundEvents.Entity.Pig.STEP,
	)

	cowSoundVariants.last() assertsIs """
		{
			"ambient_sound": "minecraft:entity.pig.ambient",
			"death_sound": "minecraft:entity.pig.death",
			"hurt_sound": "minecraft:entity.pig.hurt",
			"step_sound": "minecraft:entity.pig.step"
		}
	""".trimIndent()
}

class CowSoundVariantTests : FunSpec({
	test("cow sound variant") {
		dataPack("cowSoundVariant") {
			pretty()
			cowSoundVariantTests()
		}
	}
})
