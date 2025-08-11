package io.github.ayfri.kore.features.worldgen.noise

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven noise definition.
 *
 * Defines octave-based amplitudes for a noise instance used by noise routers and terrain
 * generation. Often referenced by noise settings/routers.
 *
 * JSON format reference: https://minecraft.wiki/w/Noise_router
 * Docs: https://kore.ayfri.com/docs/worldgen
 */
@Serializable
data class Noise(
	@Transient
	override var fileName: String = "noise",
	@JsonSerialName("firstOctave")
	var firstOctave: Int = 0,
	var amplitudes: List<Double> = emptyList(),
) : Generator("worldgen/noise") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Creates a noise definition using a builder block. */
fun DataPack.noise(fileName: String = "noise", init: Noise.() -> Unit = {}): NoiseArgument {
	val noise = Noise(fileName).apply(init)
	noises += noise
	return NoiseArgument(fileName, noise.namespace ?: name)
}

/**
 * Creates a noise definition with inline parameters.
 *
 * Quick helper when you already know octave and amplitude values.
 */
fun DataPack.noise(
	fileName: String = "noise",
	firstOctave: Int = 0,
	amplitudes: List<Double> = emptyList(),
	block: Noise.() -> Unit = {},
) = noise(fileName) {
	this.firstOctave = firstOctave
	this.amplitudes = amplitudes
	block()
}

fun DataPack.noise(fileName: String, firstOctave: Int = 0, vararg amplitudes: Double, block: Noise.() -> Unit = {}) = noise(fileName) {
	this.firstOctave = firstOctave
	this.amplitudes = amplitudes.toList()
	block()
}
