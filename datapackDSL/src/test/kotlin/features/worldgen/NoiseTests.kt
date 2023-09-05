package features.worldgen

import DataPack
import assertions.assertsIs
import features.worldgen.noise.noise

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
