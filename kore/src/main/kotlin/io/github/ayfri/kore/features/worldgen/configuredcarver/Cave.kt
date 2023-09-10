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
