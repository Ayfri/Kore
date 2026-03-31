package io.github.ayfri.kore.features

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.serializers.JsonNamingSnakeCaseStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
private val roundTripJson = Json {
	prettyPrint = true
	prettyPrintIndent = "\t"
	encodeDefaults = true
	explicitNulls = false
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingSnakeCaseStrategy
	useAlternativeNames = false
}

internal inline fun <reified T> roundTrip(value: T) {
	val encoded = roundTripJson.encodeToString(value)
	val decoded = roundTripJson.decodeFromString<T>(encoded)

	roundTripJson.encodeToString(decoded) assertsIs encoded
}