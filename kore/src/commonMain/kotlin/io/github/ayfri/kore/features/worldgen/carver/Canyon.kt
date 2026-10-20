package io.github.ayfri.kore.features.worldgen.carver

import io.github.ayfri.kore.features.worldgen.floatproviders.ConstantFloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.generated.arguments.worldgen.types.CarverArgument
import kotlinx.serialization.Serializable

/**
 * Carves a canyon: a deep ravine with steep walls.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property shape The shape of the ravine, see [CanyonShapeConfig].
 * @property verticalRotation Vertical rotation applied as the canyon extends.
 */
@Serializable
data class Canyon(
	override var probability: Double = 0.1,
	var shape: CanyonShapeConfig = CanyonShapeConfig(),
	var verticalRotation: FloatProvider = ConstantFloatProvider(0f),
	override var y: HeightProvider = ConstantHeightProvider(Absolute(0)),
) : Config()

/**
 * The shape of a [Canyon].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property distanceFactor Length of the canyon, higher is longer.
 * @property horizontalRadiusFactor Breadth of the canyon, higher is wider.
 * @property thickness Breadth and height of the canyon.
 * @property verticalRadiusCenterFactor Extra depth based on the horizontal distance from the canyon center.
 * @property verticalRadiusDefaultFactor Depth of the canyon, higher is deeper.
 * @property widthSmoothness Smoothing of the walls along the vertical axis, must be greater than `0`.
 * @property yScale Vertical scaling of the carved shape.
 */
@Serializable
data class CanyonShapeConfig(
	var distanceFactor: FloatProvider = ConstantFloatProvider(1f),
	var horizontalRadiusFactor: FloatProvider = ConstantFloatProvider(1f),
	var thickness: FloatProvider = ConstantFloatProvider(1f),
	var verticalRadiusCenterFactor: Float = 0f,
	var verticalRadiusDefaultFactor: Float = 1f,
	var widthSmoothness: Int = 1,
	var yScale: FloatProvider = ConstantFloatProvider(1f),
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
 * carvers {
 *     canyon("my_canyon") {
 *         probability = 0.02
 *         verticalRotation = clampedNormal(0f, 1f, -1f, 1f)
 *         shape { widthSmoothness = 3 }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun CarversScope.canyon(fileName: String = "canyon", init: Canyon.() -> Unit = {}): CarverArgument =
	dp.carver(fileName, Canyon().apply(init))
