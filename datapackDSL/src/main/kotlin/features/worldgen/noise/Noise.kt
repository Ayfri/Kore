package features.worldgen.noise

import DataPack
import Generator
import arguments.types.resources.worldgen.NoiseArgument
import serializers.JsonSerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class Noise(
	@Transient
	override var fileName: String = "noise",
	@JsonSerialName("firstOctave")
	var firstOctave: Int = 0,
	var amplitudes: List<Double> = emptyList(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.noise(fileName: String = "noise", block: Noise.() -> Unit = {}): NoiseArgument {
	noises += Noise(fileName).apply(block)
	return NoiseArgument(fileName, name)
}

fun DataPack.noise(fileName: String = "noise", firstOctave: Int = 0, amplitudes: List<Double> = emptyList()) = noise(fileName) {
	this.firstOctave = firstOctave
	this.amplitudes = amplitudes
}

fun DataPack.noise(fileName: String, firstOctave: Int = 0, vararg amplitudes: Double) = noise(fileName) {
	this.firstOctave = firstOctave
	this.amplitudes = amplitudes.toList()
}
