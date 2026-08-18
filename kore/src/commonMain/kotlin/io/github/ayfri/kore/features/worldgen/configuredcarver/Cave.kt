package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.HeightConstant
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.absolute
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredCarverArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

/**
 * Carves a cave: a long tunnel that sometimes branches, occasionally starting from a circular room.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property horizontalRadiusMultiplier Horizontal scaling of tunnels, which does not change their length.
 * @property verticalRadiusMultiplier Vertical scaling of tunnels, which does not change their length.
 * @property floorLevel Between `-1` and `1`: `0` carves ellipsoids, `1` carves upper half-ellipsoids for a flat floor.
 */
@Serializable
data class Cave(
	override var probability: Double = 0.1,
	override var y: HeightProvider = constantAbsolute(0),
	@JsonSerialName("yScale")
	override var yScale: FloatProvider = constant(1f),
	override var lavaLevel: HeightConstant = absolute(-54),
	override var replaceable: InlinableList<BlockOrTagArgument> = listOf(Tags.Block.OVERWORLD_CARVER_REPLACEABLES),
	override var debugSettings: DebugSettings? = null,
	var horizontalRadiusMultiplier: FloatProvider = constant(1f),
	var verticalRadiusMultiplier: FloatProvider = constant(1f),
	var floorLevel: FloatProvider = constant(0f),
) : Config()

/**
 * Carves a nether cave: the [Cave] algorithm with wider tunnels, and without aquifers, so everything carved below
 * `bottom_y + 32` is filled with lava and [lavaLevel] has no effect.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property horizontalRadiusMultiplier Horizontal scaling of tunnels, which does not change their length.
 * @property verticalRadiusMultiplier Vertical scaling of tunnels, which does not change their length.
 * @property floorLevel Between `-1` and `1`: `0` carves ellipsoids, `1` carves upper half-ellipsoids for a flat floor.
 */
@Serializable
data class NetherCave(
	override var probability: Double = 0.1,
	override var y: HeightProvider = constantAbsolute(0),
	@JsonSerialName("yScale")
	override var yScale: FloatProvider = constant(1f),
	override var lavaLevel: HeightConstant = absolute(-54),
	override var replaceable: InlinableList<BlockOrTagArgument> = listOf(Tags.Block.NETHER_CARVER_REPLACEABLES),
	override var debugSettings: DebugSettings? = null,
	var horizontalRadiusMultiplier: FloatProvider = constant(1f),
	var verticalRadiusMultiplier: FloatProvider = constant(1f),
	var floorLevel: FloatProvider = constant(0f),
) : Config()

/**
 * Creates a cave carver, configured in [init].
 *
 * ```kotlin
 * configuredCarvers {
 *     cave("my_cave") {
 *         probability = 0.15
 *         y = uniformHeightProvider(aboveBottom(8), absolute(180))
 *         floorLevel = uniform(-1f, -0.4f)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun ConfiguredCarversScope.cave(fileName: String = "cave", init: Cave.() -> Unit = {}): ConfiguredCarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, Cave().apply(init))
	dp.configuredCarvers += configuredCarver
	return ConfiguredCarverArgument(fileName, configuredCarver.namespace ?: dp.name)
}

/**
 * Creates a nether cave carver, configured in [init].
 *
 * Produces `data/<namespace>/worldgen/configured_carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun ConfiguredCarversScope.netherCave(
	fileName: String = "nether_cave",
	init: NetherCave.() -> Unit = {},
): ConfiguredCarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, NetherCave().apply(init))
	dp.configuredCarvers += configuredCarver
	return ConfiguredCarverArgument(fileName, configuredCarver.namespace ?: dp.name)
}
