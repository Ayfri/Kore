package io.github.ayfri.kore.serializers

import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/** Decodes the current value as a [JsonObject]. Only valid for JSON decoders. */
fun Decoder.decodeJsonObject(): JsonObject {
	require(this is JsonDecoder) { "Expected a JSON decoder, got ${this::class.simpleName}." }
	return decodeJsonElement().jsonObject
}

/** Splits a namespaced id (`namespace:path`) into a `(path, namespace)` pair, defaulting the namespace to `minecraft`. */
fun String.splitNamespacedId() = substringAfter(':') to substringBefore(':', "minecraft")
