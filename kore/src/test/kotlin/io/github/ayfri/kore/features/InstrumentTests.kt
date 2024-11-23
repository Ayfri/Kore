package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.instruments.instrument
import io.github.ayfri.kore.generated.SoundEvents

fun DataPack.instrumentTests() {
	instrument("test_horn") {
		soundEvent = SoundEvents.Item.GoatHorn.Sound.`0`
		range = 64f
		useDuration = 14f
		description = textComponent("A mighty horn!")
	}

	instruments.last() assertsIs """
		{
			"sound_event": "minecraft:item.goat_horn.sound.0",
			"range": 64.0,
			"use_duration": 14.0,
			"description": {
				"text": "A mighty horn!"
			}
		}
	""".trimIndent()
}
