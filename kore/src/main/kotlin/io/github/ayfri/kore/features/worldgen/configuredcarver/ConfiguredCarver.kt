package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.worldgen.CarverArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

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
	configuredCarvers += ConfiguredCarver(fileName, config).apply(block)
	return CarverArgument(fileName, name)
}
