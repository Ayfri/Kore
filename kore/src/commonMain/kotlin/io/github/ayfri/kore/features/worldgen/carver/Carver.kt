package io.github.ayfri.kore.features.worldgen.carver

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.CarverArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A carver, a carver type paired with its configuration.
 *
 * Carvers run during the `carvers` generation step, after terrain noise and material rules but before features, and
 * remove terrain to form cave systems and canyons. Biomes reference them through their `carvers` list.
 *
 * Produces `data/<namespace>/worldgen/carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property config The carver type and its configuration, see [Cave] and [Canyon].
 */
@Serializable
data class Carver(
	@Transient
	override var fileName: String = "carver",
	var config: Config,
) : Generator("worldgen/carver") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(config)
}

/**
 * Builder scope for declaring carvers via [carvers].
 *
 * Each carver type ([cave], [canyon]) exposes a function on this class that creates one [Carver] file, so a carver
 * can only ever hold the single config it was declared with.
 */
data class CarversScope(val dp: DataPack)

val DataPack.carversBuilder get() = CarversScope(this)

/**
 * Declares carvers using Kore's DSL builder, one call per carver type ([cave], [canyon]).
 *
 * ```kotlin
 * carvers {
 *     cave("my_cave") { probability = 0.15 }
 *     canyon("my_canyon") { probability = 0.02 }
 * }
 * ```
 *
 * Produces one `data/<namespace>/worldgen/carver/<fileName>.json` per call inside [init].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun DataPack.carvers(init: CarversScope.() -> Unit) = carversBuilder.apply(init)

/**
 * Registers a carver holding [config], adjusting the generator itself in [init], for instance to override its namespace.
 *
 * Produces `data/<namespace>/worldgen/carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun DataPack.carver(fileName: String = "carver", config: Config, init: Carver.() -> Unit = {}): CarverArgument {
	val carver = Carver(fileName, config).apply(init)
	carvers += carver
	return CarverArgument(carver.fileName, carver.namespace ?: name)
}
