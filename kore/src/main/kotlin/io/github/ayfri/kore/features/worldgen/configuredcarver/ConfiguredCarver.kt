package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.CarverArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ConfiguredCarver(
	@Transient
	override var fileName: String = "configured_carver",
	var config: Config = Cave(),
) : Generator("worldgen/configured_carver") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(config)
}

// TODO : #32
fun DataPack.configuredCarver(
	fileName: String = "configured_carver",
	config: Config,
	block: ConfiguredCarver. () -> Unit = {},
): CarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, config).apply(block)
	configuredCarvers += configuredCarver
	return CarverArgument(fileName, configuredCarver.namespace ?: name)
}
