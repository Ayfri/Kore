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
 * Carves a canyon: a deep ravine with steep walls.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property verticalRotation Vertical rotation applied as the canyon extends.
 * @property shape The shape of the ravine, see [CanyonShapeConfig].
 */
@Serializable
data class Canyon(
	override var probability: Double = 0.1,
	override var y: HeightProvider = constantAbsolute(0),
	@JsonSerialName("yScale")
	override var yScale: FloatProvider = constant(1f),
	override var lavaLevel: HeightConstant = absolute(-54),
	override var replaceable: InlinableList<BlockOrTagArgument> = listOf(Tags.Block.OVERWORLD_CARVER_REPLACEABLES),
	override var debugSettings: DebugSettings? = null,
	var verticalRotation: FloatProvider = constant(0f),
	var shape: CanyonShapeConfig = CanyonShapeConfig(),
) : Config()

/**
 * The shape of a [Canyon].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property distanceFactor Length of the canyon, higher is longer.
 * @property thickness Breadth and height of the canyon.
 * @property widthSmoothness Smoothing of the walls along the vertical axis, must be greater than `0`.
 * @property horizontalRadiusFactor Breadth of the canyon, higher is wider.
 * @property verticalRadiusDefaultFactor Depth of the canyon, higher is deeper.
 * @property verticalRadiusCenterFactor Extra depth based on the horizontal distance from the canyon center.
 */
@Serializable
data class CanyonShapeConfig(
	var distanceFactor: FloatProvider = constant(1f),
	var thickness: FloatProvider = constant(1f),
	var widthSmoothness: Int = 1,
	var horizontalRadiusFactor: FloatProvider = constant(1f),
	var verticalRadiusDefaultFactor: Float = 1f,
	var verticalRadiusCenterFactor: Float = 0f,
)

/**
 * Sets the shape of the ravine.
 *
 * ```kotlin
 * canyon("my_canyon") {
 *     shape {
 *         thickness = trapezoid(0f, 6f, 2f)
 *         widthSmoothness = 3
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun Canyon.shape(init: CanyonShapeConfig.() -> Unit = {}) {
	shape = CanyonShapeConfig().apply(init)
}

/**
 * Creates a canyon carver, configured in [init].
 *
 * ```kotlin
 * configuredCarvers {
 *     canyon("my_canyon") {
 *         probability = 0.02
 *         verticalRotation = clampedNormal(0f, 1f, -1f, 1f)
 *         shape { widthSmoothness = 3 }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun ConfiguredCarversScope.canyon(fileName: String = "canyon", init: Canyon.() -> Unit = {}): ConfiguredCarverArgument {
	val configuredCarver = ConfiguredCarver(fileName, Canyon().apply(init))
	dp.configuredCarvers += configuredCarver
	return ConfiguredCarverArgument(fileName, configuredCarver.namespace ?: dp.name)
}
