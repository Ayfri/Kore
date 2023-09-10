package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.HeightConstant
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.absolute
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.JsonSerialName
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
