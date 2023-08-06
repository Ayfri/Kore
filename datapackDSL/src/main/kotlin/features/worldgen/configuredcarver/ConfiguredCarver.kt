package features.worldgen.configuredcarver

import DataPack
import Generator
import arguments.types.resources.worldgen.CarverArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class ConfiguredCarver(
	@Transient
	override var fileName: String = "configured_carver",
	var config: Config = Cave(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(config)
}

fun DataPack.configuredCarver(fileName: String = "configured_carver", config: Config): CarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, config)
	configuredCarvers += configuredCarver
	return CarverArgument(fileName, name)
}
