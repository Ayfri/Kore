package io.github.ayfri.kore.features.worldgen.carver

import io.github.ayfri.kore.features.worldgen.floatproviders.ConstantFloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import kotlinx.serialization.Serializable

/**
 * Carves a cave: a long tunnel that sometimes branches, occasionally starting from a circular room.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property count Number of tunnels attempted per chunk attempt, `0` or more.
 * @property floorLevel Between `-1` and `1`: `0` carves ellipsoids, `1` carves upper half-ellipsoids for a flat floor.
 * @property horizontalRadiusMultiplier Horizontal scaling of tunnels, which does not change their length.
 * @property roomVerticalRadiusMultiplier Vertical scaling of the rooms tunnels start from.
 * @property startVerticalRadiusMultiplier Vertical scaling of the first tunnel segment, `1.0` when `null`.
 * @property thickness Breadth and height of tunnels, `0` or more.
 * @property verticalRadiusMultiplier Vertical scaling of tunnels, which does not change their length.
 * @property weirdThicknessBias Whether tunnels get an irregular thickness, vanilla defaults to `false`.
 */
@Serializable
data class Cave(
	var count: IntProvider = ConstantIntProvider(1),
	var floorLevel: FloatProvider = ConstantFloatProvider(0f),
	var horizontalRadiusMultiplier: FloatProvider = ConstantFloatProvider(1f),
	override var probability: Double = 0.1,
	var roomVerticalRadiusMultiplier: FloatProvider = ConstantFloatProvider(1f),
	var startVerticalRadiusMultiplier: FloatProvider? = null,
	var thickness: FloatProvider = ConstantFloatProvider(1f),
	var verticalRadiusMultiplier: FloatProvider = ConstantFloatProvider(1f),
	var weirdThicknessBias: Boolean? = null,
	override var y: HeightProvider = ConstantHeightProvider(Absolute(0)),
) : Config()

/**
 * Creates a cave carver, configured in [init].
 *
 * ```kotlin
 * carvers {
 *     cave("my_cave") {
 *         probability = 0.15
 *         y = uniformHeightProvider(aboveBottom(8), absolute(180))
 *         count = veryBiasedToBottom(0, 14)
 *         floorLevel = uniform(-1f, -0.4f)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/carver/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun CarversScope.cave(fileName: String = "cave", init: Cave.() -> Unit = {}) =
	dp.carver(fileName, Cave().apply(init))

/**
 * Sets [Cave.count] to [value], the number of tunnels attempted per chunk.
 *
 * ```kotlin
 * count(veryBiasedToBottom(0, 14))
 * ```
 */
fun Cave.count(value: IntProvider) {
	count = value
}

/** Sets [Cave.count] to the constant [value]. */
fun Cave.count(value: Int) = count(ConstantIntProvider(value))

/**
 * Sets [Cave.floorLevel] to [value], `0.0` carving ellipsoids and `1.0` flat floors.
 *
 * ```kotlin
 * floorLevel(uniform(-1f, -0.4f))
 * ```
 */
fun Cave.floorLevel(value: FloatProvider) {
	floorLevel = value
}

/** Sets [Cave.floorLevel] to the constant [value]. */
fun Cave.floorLevel(value: Float) = floorLevel(ConstantFloatProvider(value))

/**
 * Sets [Cave.horizontalRadiusMultiplier] to [value], the horizontal scaling of the tunnels.
 *
 * ```kotlin
 * horizontalRadiusMultiplier(uniform(0.7f, 1.4f))
 * ```
 */
fun Cave.horizontalRadiusMultiplier(value: FloatProvider) {
	horizontalRadiusMultiplier = value
}

/** Sets [Cave.horizontalRadiusMultiplier] to the constant [value]. */
fun Cave.horizontalRadiusMultiplier(value: Float) = horizontalRadiusMultiplier(ConstantFloatProvider(value))

/**
 * Sets [Cave.roomVerticalRadiusMultiplier] to [value], the vertical scaling of the rooms the tunnels start from.
 *
 * ```kotlin
 * roomVerticalRadiusMultiplier(uniform(0.1f, 0.9f))
 * ```
 */
fun Cave.roomVerticalRadiusMultiplier(value: FloatProvider) {
	roomVerticalRadiusMultiplier = value
}

/** Sets [Cave.roomVerticalRadiusMultiplier] to the constant [value]. */
fun Cave.roomVerticalRadiusMultiplier(value: Float) = roomVerticalRadiusMultiplier(ConstantFloatProvider(value))

/**
 * Sets [Cave.startVerticalRadiusMultiplier] to [value], the vertical scaling of the tunnel start.
 *
 * ```kotlin
 * startVerticalRadiusMultiplier(1.5f)
 * ```
 */
fun Cave.startVerticalRadiusMultiplier(value: FloatProvider) {
	startVerticalRadiusMultiplier = value
}

/** Sets [Cave.startVerticalRadiusMultiplier] to the constant [value]. */
fun Cave.startVerticalRadiusMultiplier(value: Float) = startVerticalRadiusMultiplier(ConstantFloatProvider(value))

/**
 * Sets [Cave.thickness] to [value], the breadth and height of the tunnels.
 *
 * ```kotlin
 * thickness(trapezoid(0f, 3f, 1f))
 * ```
 */
fun Cave.thickness(value: FloatProvider) {
	thickness = value
}

/** Sets [Cave.thickness] to the constant [value]. */
fun Cave.thickness(value: Float) = thickness(ConstantFloatProvider(value))

/**
 * Sets [Cave.verticalRadiusMultiplier] to [value], the vertical scaling of the tunnels.
 *
 * ```kotlin
 * verticalRadiusMultiplier(uniform(0.8f, 1.3f))
 * ```
 */
fun Cave.verticalRadiusMultiplier(value: FloatProvider) {
	verticalRadiusMultiplier = value
}

/** Sets [Cave.verticalRadiusMultiplier] to the constant [value]. */
fun Cave.verticalRadiusMultiplier(value: Float) = verticalRadiusMultiplier(ConstantFloatProvider(value))
