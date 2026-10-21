package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.roundTrip
import io.github.ayfri.kore.features.worldgen.noise.amplitudeModifiers
import io.github.ayfri.kore.features.worldgen.noise.noise
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.noiseTests() {
	noise("noise_test") {
		baseOctave = 0
		amplitudeModifiers(1.0, 1.0, 1.0, 1.0)
	}

	noises.last() assertsIs """
		{
			"amplitude_modifiers": [
				1.0,
				1.0,
				1.0,
				1.0
			],
			"base_octave": 0,
			"octave_count": 4
		}
	""".trimIndent()

	noise("empty_noise")
	noises.last() assertsIs """
		{
			"base_octave": 0
		}
	""".trimIndent()

	val caveEntrance = noise("cave/entrance") {
		baseOctave = -7
		amplitudeModifiers(0.4, 0.5, 1.0)
	}

	noises.last() assertsIs """
		{
			"amplitude_modifiers": [
				0.4,
				0.5,
				1.0
			],
			"base_octave": -7,
			"octave_count": 3
		}
	""".trimIndent()

	caveEntrance assertsIs "noise:cave/entrance"
	noises.last().fileName assertsIs "cave/entrance"
	noises.last().resourceFolder assertsIs "worldgen/noise"

	noise("temperature") {
		amplitudeModifiers(1.5, 0.0, 1.0, 0.0, 0.0, 0.0)
		baseAmplitude = 1.2453007926713473
		baseOctave = -10
	}

	noises.last() assertsIs """
		{
			"amplitude_modifiers": [
				1.5,
				0.0,
				1.0,
				0.0,
				0.0,
				0.0
			],
			"base_amplitude": 1.2453007926713473,
			"base_octave": -10,
			"octave_count": 6
		}
	""".trimIndent()

	noise("unnormalized") {
		baseOctave = -3
		normalize = false
		octaveCount = 2
	}

	noises.last() assertsIs """
		{
			"base_octave": -3,
			"normalize": false,
			"octave_count": 2
		}
	""".trimIndent()

	roundTrip(noises.last())
}

class NoiseTests : FunSpec({
	test("noise") {
		dataPack("noise") {
			pretty()
			noiseTests()
		}
	}
})
