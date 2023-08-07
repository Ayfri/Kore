package features.worldgen.configuredcarver

import arguments.types.BlockOrTagArgument
import features.worldgen.floatproviders.FloatProvider
import features.worldgen.floatproviders.constant
import features.worldgen.heightproviders.HeightProvider
import features.worldgen.heightproviders.constantAbsolute
import features.worldgen.noisesettings.rules.conditions.HeightConstant
import features.worldgen.noisesettings.rules.conditions.absolute
import serializers.InlinableList
import serializers.JsonSerialName
import kotlinx.serialization.Serializable

typealias NetherCave = Cave

@Serializable
data class Cave(
	var probability: Double = 0.1,
	var y: HeightProvider = constantAbsolute(0),
	@JsonSerialName("yScale")
	var yScale: FloatProvider = constant(0f),
	var lavaLevel: HeightConstant = absolute(0),
	var replaceable: InlinableList<BlockOrTagArgument> = emptyList(),
	var debugSettings: DebugSettings? = null,
	var horizontalRadiusMultiplier: FloatProvider = constant(0f),
	var verticalRadiusMultiplier: FloatProvider = constant(0f),
	var floorLevel: FloatProvider = constant(0f),
) : Config()

fun caveConfig(block: Cave.() -> Unit = {}) = Cave().apply(block)

fun netherCaveConfig(block: NetherCave.() -> Unit = {}) = NetherCave().apply(block)

fun Cave.debugSettings(block: DebugSettings.() -> Unit = {}) {
	debugSettings = DebugSettings().apply(block)
}
