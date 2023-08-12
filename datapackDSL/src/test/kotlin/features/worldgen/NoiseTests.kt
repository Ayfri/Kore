package features.worldgen

import DataPack
import features.worldgen.noise.noise
import utils.assertsIs

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
