package io.github.ayfri.kore.features.predicates.providers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

typealias IntOrNumberProvidersRange = @Serializable(IntOrIntNumberProvidersRange.Companion.IntOrNumberProviderRangeSerializer::class) IntOrIntNumberProvidersRange

@Serializable
class IntOrIntNumberProvidersRange internal constructor(
	var value: Int? = null,
	var range: Pair<NumberProvider, NumberProvider>? = null,
) {
	companion object {
		object IntOrNumberProviderRangeSerializer : JsonTransformingSerializer<IntOrIntNumberProvidersRange>(serializer()) {
			override fun transformSerialize(element: JsonElement) = when {
				element.jsonObject["value"] != null -> element.jsonObject["value"]!!
				element.jsonObject["range"] != null -> buildJsonObject {
					val range = element.jsonObject["range"]!!.jsonObject
					put("min", range["first"]!!)
					put("max", range["second"]!!)
				}

				else -> error("Invalid IntOrNumberProviderRange")
			}
		}
	}
}

fun int(value: Int) = IntOrNumberProvidersRange(value)
fun providersRange(min: NumberProvider, max: NumberProvider) = IntOrNumberProvidersRange(range = min to max)
fun providersRange(min: Float, max: NumberProvider) = IntOrNumberProvidersRange(range = constant(min) to max)
fun providersRange(min: NumberProvider, max: Float) = IntOrNumberProvidersRange(range = min to constant(max))
fun intRange(min: Float, max: Float) = IntOrNumberProvidersRange(range = constant(min) to constant(max))
fun intRange(range: ClosedFloatingPointRange<Float>) =
	IntOrNumberProvidersRange(range = constant(range.start) to constant(range.endInclusive))
