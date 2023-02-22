package features.advancements.types

import arguments.Argument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Statistic(
	var statisticType: Argument.StatisticType? = null,
	var stat: Argument.Statistic? = null,
	var value: IntRangeOrIntJson? = null,
)

fun statistic(statisticType: Argument.StatisticType? = null, stat: Argument.Statistic? = null, value: IntRangeOrIntJson? = null) =
	Statistic(statisticType, stat, value)
