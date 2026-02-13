package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntEnd
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJsonSerializer
import io.github.ayfri.kore.assertions.assertsIs
import kotlinx.serialization.json.Json

fun intRangeOrIntJsonSerializerTests() {
	val json = Json { prettyPrint = false }

	json.encodeToString(IntRangeOrIntJsonSerializer, rangeOrIntStart(1)) assertsIs "{\"min\":1}"
	json.encodeToString(IntRangeOrIntJsonSerializer, rangeOrIntEnd(5)) assertsIs "{\"max\":5}"
	json.encodeToString(IntRangeOrIntJsonSerializer, rangeOrInt(1..5)) assertsIs "{\"min\":1,\"max\":5}"
	json.encodeToString(IntRangeOrIntJsonSerializer, rangeOrInt(10)) assertsIs "10"

	json.decodeFromString(IntRangeOrIntJsonSerializer, "{\"min\":1}").range!!.start.toString() assertsIs "1"
	(json.decodeFromString(IntRangeOrIntJsonSerializer, "{\"min\":1}").range!!.end == null) assertsIs true
	(json.decodeFromString(IntRangeOrIntJsonSerializer, "{\"max\":5}").range!!.start == null) assertsIs true
	json.decodeFromString(IntRangeOrIntJsonSerializer, "{\"max\":5}").range!!.end.toString() assertsIs "5"
	json.decodeFromString(IntRangeOrIntJsonSerializer, "{\"min\":1,\"max\":5}").range!!.start.toString() assertsIs "1"
	json.decodeFromString(IntRangeOrIntJsonSerializer, "{\"min\":1,\"max\":5}").range!!.end.toString() assertsIs "5"
	json.decodeFromString(IntRangeOrIntJsonSerializer, "10").int.toString() assertsIs "10"
}
