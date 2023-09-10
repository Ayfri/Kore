package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.noise.noise

fun DataPack.noiseTests() {
	noise("noise_test") {
		firstOctave = 0
		amplitudes = listOf(1.0, 1.0, 1.0, 1.0)
	}

	noises.last() assertsIs """
		{
			"firstOctave": 0,
			"amplitudes": [
				1.0,
				1.0,
				1.0,
				1.0
			]
		}
	""".trimIndent()
}
