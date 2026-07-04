package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.CarverArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven configured carver.
 *
 * Associates a carver type (such as 'cave', 'nether_cave', or 'canyon') with its configuration.
 * Configured carvers are referenced by biomes to generate cave systems and canyons by removing terrain.
 *
 * The configuration includes probability, height, lava level, replaceable blocks, and optional debug settings.
 * Additional fields depend on the carver type.
 *
 * JSON format reference: https://minecraft.wiki/w/Carver_definition
 */
@Serializable
data class ConfiguredCarver(
	@Transient
	override var fileName: String = "configured_carver",
	var config: Config = Cave(),
) : Generator("worldgen/configured_carver") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(config)
}

// TODO : #32
/**
 * Creates a configured carver using a builder block.
 *
 * Produces `data/<namespace>/worldgen/configured_carver/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Carver_definition
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 */
fun DataPack.configuredCarver(
	fileName: String = "configured_carver",
	config: Config,
	block: ConfiguredCarver. () -> Unit = {},
): CarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, config).apply(block)
	configuredCarvers += configuredCarver
	return CarverArgument(fileName, configuredCarver.namespace ?: name)
}
