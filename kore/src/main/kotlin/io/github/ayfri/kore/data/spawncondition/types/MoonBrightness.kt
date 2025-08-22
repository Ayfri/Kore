package io.github.ayfri.kore.data.spawncondition.types

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

@Serializable
data class MoonBrightness(var range: FloatRangeOrFloatJson) : VariantCondition
