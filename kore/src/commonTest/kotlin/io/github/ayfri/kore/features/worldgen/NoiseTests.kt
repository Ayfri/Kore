package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.noise.noise
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

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

class NoiseTests : FunSpec({
	test("noise") {
		dataPack("noise") {
			pretty()
			noiseTests()
		}
	}
})
