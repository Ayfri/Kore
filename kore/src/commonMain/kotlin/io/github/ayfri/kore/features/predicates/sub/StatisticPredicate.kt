package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.generated.arguments.types.StatTypeArgument
import kotlinx.serialization.Serializable

/**
 * Matches one statistic of a player, as an entry of the `stats` list of a [PlayerSubPredicate].
 *
 * [type] selects the statistic family (`minecraft:custom`, `minecraft:mined`, ...) and [stat] the value inside it,
 * so `minecraft:mined` expects a block id while `minecraft:custom` expects a custom statistic id.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class StatisticPredicate(
	var type: StatTypeArgument,
	var stat: ResourceLocationArgument,
	var value: IntRangeOrIntJson,
)

/** Creates a [StatisticPredicate] matching an exact statistic [value]. */
fun statisticPredicate(type: StatTypeArgument, stat: ResourceLocationArgument, value: Int) =
	StatisticPredicate(type, stat, value.asRangeOrInt())

/** Creates a [StatisticPredicate] matching a statistic value within [value]. */
fun statisticPredicate(type: StatTypeArgument, stat: ResourceLocationArgument, value: IntRangeOrIntJson) =
	StatisticPredicate(type, stat, value)

/** Creates a [StatisticPredicate] matching a statistic value within [value]. */
fun statisticPredicate(type: StatTypeArgument, stat: ResourceLocationArgument, value: IntRange) =
	StatisticPredicate(type, stat, value.asRangeOrInt())
