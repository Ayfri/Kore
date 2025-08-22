package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.types.resources.StatisticArgument
import io.github.ayfri.kore.arguments.types.resources.StatisticTypeArgument
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Statistic(
	var statisticType: StatisticTypeArgument? = null,
	var stat: StatisticArgument? = null,
	var value: IntRangeOrIntJson? = null,
)

fun statistic(statisticType: StatisticTypeArgument? = null, stat: StatisticArgument? = null, value: IntRangeOrIntJson? = null) =
	Statistic(statisticType, stat, value)
