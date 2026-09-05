package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.noise.amplitudes
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

	noise("empty_noise")
	noises.last() assertsIs """
		{
			"firstOctave": 0,
			"amplitudes": []
		}
	""".trimIndent()

	val caveEntrance = noise("cave/entrance") {
		firstOctave = -7
		amplitudes(0.4, 0.5, 1.0)
	}

	noises.last() assertsIs """
		{
			"firstOctave": -7,
			"amplitudes": [
				0.4,
				0.5,
				1.0
			]
		}
	""".trimIndent()

	caveEntrance assertsIs "noise:cave/entrance"
	noises.last().fileName assertsIs "cave/entrance"
	noises.last().resourceFolder assertsIs "worldgen/noise"
}

class NoiseTests : FunSpec({
	test("noise") {
		dataPack("noise") {
			pretty()
			noiseTests()
		}
	}
})
