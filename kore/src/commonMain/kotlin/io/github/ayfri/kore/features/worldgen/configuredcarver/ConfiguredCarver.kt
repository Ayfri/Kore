package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredCarverArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A configured carver, a carver type paired with its configuration.
 *
 * Carvers run during the `carvers` generation step, after terrain noise and surface rules but before features, and
 * remove terrain to form cave systems and canyons. Biomes reference them through their `carvers` list.
 *
 * Produces `data/<namespace>/worldgen/configured_carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property config The carver type and its configuration, see [Cave], [NetherCave] and [Canyon].
 */
@Serializable
data class ConfiguredCarver(
	@Transient
	override var fileName: String = "configured_carver",
	var config: Config,
) : Generator("worldgen/configured_carver") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(config)
}

/**
 * Builder scope for declaring configured carvers via [configuredCarvers].
 *
 * Each carver type ([cave], [netherCave], [canyon]) exposes a function on this class that creates one
 * [ConfiguredCarver] file, so a configured carver can only ever hold the single config it was declared with.
 */
data class ConfiguredCarversScope(val dp: DataPack)

val DataPack.configuredCarversBuilder get() = ConfiguredCarversScope(this)

/**
 * Declares configured carvers using Kore's DSL builder, one call per carver type ([cave], [netherCave], [canyon]).
 *
 * ```kotlin
 * configuredCarvers {
 *     cave("my_cave") { probability = 0.15 }
 *     canyon("my_canyon") { probability = 0.02 }
 * }
 * ```
 *
 * Produces one `data/<namespace>/worldgen/configured_carver/<fileName>.json` per call inside [init].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun DataPack.configuredCarvers(init: ConfiguredCarversScope.() -> Unit) = configuredCarversBuilder.apply(init)

/**
 * Creates a configured carver from an already built [config], adjusting the generator itself in [init].
 *
 * Prefer [configuredCarvers] and its per-type functions; this entry point exists for the cases where the
 * [ConfiguredCarver] itself needs tweaking, such as overriding its namespace.
 *
 * Produces `data/<namespace>/worldgen/configured_carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun DataPack.configuredCarver(
	fileName: String = "configured_carver",
	config: Config,
	init: ConfiguredCarver.() -> Unit = {},
): ConfiguredCarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, config).apply(init)
	configuredCarvers += configuredCarver
	return ConfiguredCarverArgument(configuredCarver.fileName, configuredCarver.namespace ?: name)
}
