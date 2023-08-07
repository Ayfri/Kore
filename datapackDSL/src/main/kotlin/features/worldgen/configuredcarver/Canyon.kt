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

@Serializable
data class Canyon(
	var probability: Double = 0.1,
	var y: HeightProvider = constantAbsolute(0),
	@JsonSerialName("yScale")
	var yScale: FloatProvider = constant(0f),
	var lavaLevel: HeightConstant = absolute(0),
	var replaceable: InlinableList<BlockOrTagArgument> = emptyList(),
	var debugSettings: DebugSettings? = null,
	var verticalRotation: FloatProvider = constant(0f),
	var shape: CanyonShapeConfig = CanyonShapeConfig(),
) : Config()

@Serializable
data class CanyonShapeConfig(
	var distanceFactor: FloatProvider = constant(0f),
	var thickness: FloatProvider = constant(0f),
	var widthSmoothness: Float = 0f,
	var horizontalRadiusMultiplier: FloatProvider = constant(0f),
	var verticalRadiusMultiplier: Float = 0f,
	var floorLevel: Float = 0f,
)

fun canyonConfig(block: Canyon.() -> Unit = {}) = Canyon().apply(block)

fun Canyon.debugSettings(block: DebugSettings.() -> Unit = {}) {
	debugSettings = DebugSettings().apply(block)
}

fun Canyon.shape(block: CanyonShapeConfig.() -> Unit = {}) {
	shape = CanyonShapeConfig().apply(block)
}
