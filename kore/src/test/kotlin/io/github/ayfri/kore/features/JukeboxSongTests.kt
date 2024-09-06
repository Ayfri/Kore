package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.jukeboxsongs.jukeboxSong
import io.github.ayfri.kore.features.jukeboxsongs.soundEvent
import io.github.ayfri.kore.generated.SoundEvents

fun DataPack.jukeboxSongTests() {
	jukeboxSong("test") {
		soundEvent(SoundEvents.Music.DRAGON)
		lengthInSeconds = 221.0f
		comparatorOutput = 1

		description = textComponent("my song", color = Color.RED)
	}

	jukeboxSongs.last() assertsIs """
		{
			"sound_event": "minecraft:music.dragon",
			"description": {
				"text": "my song",
				"color": "red",
				"type": "text"
			},
			"length_in_seconds": 221.0,
			"comparator_output": 1
		}
	""".trimIndent()
}
