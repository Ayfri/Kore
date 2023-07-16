package features.advancements.types

import arguments.types.resources.StatisticArgument
import arguments.types.resources.StatisticTypeArgument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Statistic(
	var statisticType: StatisticTypeArgument? = null,
	var stat: StatisticArgument? = null,
	var value: IntRangeOrIntJson? = null,
)

fun statistic(statisticType: StatisticTypeArgument? = null, stat: StatisticArgument? = null, value: IntRangeOrIntJson? = null) =
	Statistic(statisticType, stat, value)
