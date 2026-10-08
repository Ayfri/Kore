package io.github.ayfri.kore.features.worldgen.ruletest

import kotlinx.serialization.Serializable

/**
 * Matches every block whose Y level is between [minInclusive] and [maxInclusive].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property maxInclusive The highest matching Y level.
 * @property minInclusive The lowest matching Y level.
 */
@Serializable
data class HeightMatch(
	var maxInclusive: Int,
	var minInclusive: Int,
) : RuleTest()

/**
 * Creates a `height_match` rule test, matching every block whose Y level is between [minInclusive] and [maxInclusive].
 *
 * ```kotlin
 * rule {
 *     locationPredicate = heightMatch(minInclusive = -64, maxInclusive = 0)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.heightMatch(minInclusive: Int, maxInclusive: Int) = HeightMatch(maxInclusive, minInclusive)
