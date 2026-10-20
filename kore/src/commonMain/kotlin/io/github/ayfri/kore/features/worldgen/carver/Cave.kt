package io.github.ayfri.kore.features.worldgen.carver

import io.github.ayfri.kore.features.worldgen.floatproviders.ConstantFloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.generated.arguments.worldgen.types.CarverArgument
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
 *         count = uniform(0, 14)
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
fun CarversScope.cave(fileName: String = "cave", init: Cave.() -> Unit = {}): CarverArgument =
	dp.carver(fileName, Cave().apply(init))
