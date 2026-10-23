package io.github.ayfri.kore.serializers

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * [this] writing every finite float and double in JSON with the canonical number text (see `toCanonicalString`), the same
 * on every platform. The JVM already writes it, so [this] stays as is there; on JS it is wrapped to rewrite the numbers.
 */
internal expect fun <T> SerializationStrategy<T>.canonicalNumbers(): SerializationStrategy<T>

/** Encodes [value] to a JSON tree like [canonicalNumbers], for a serializer that builds a nested tree through the [kotlinx.serialization.json.JsonEncoder.json] of its encoder. */
internal fun <T> Json.encodeToCanonicalJsonElement(serializer: SerializationStrategy<T>, value: T): JsonElement =
	encodeToJsonElement(serializer.canonicalNumbers(), value)

/**
 * The JSON format of a data pack, a [Json] writing every finite float and double with the canonical number text on every
 * platform: `1f` is `1.0` and `1e-5` is `1.0E-5` on the JVM and on JS.
 *
 * @property json The format underneath, for what a data pack file never needs (decoding from a tree, its configuration).
 */
class DataPackJson internal constructor(val json: Json) : StringFormat {
	override val serializersModule: SerializersModule get() = json.serializersModule

	override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T = json.decodeFromString(deserializer, string)

	override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String = json.encodeToString(serializer.canonicalNumbers(), value)

	inline fun <reified T> decodeFromString(string: String): T = decodeFromString(serializersModule.serializer<T>(), string)

	inline fun <reified T> encodeToJsonElement(value: T): JsonElement = encodeToJsonElement(serializersModule.serializer<T>(), value)

	inline fun <reified T> encodeToString(value: T): String = encodeToString(serializersModule.serializer<T>(), value)

	fun <T> encodeToJsonElement(serializer: SerializationStrategy<T>, value: T): JsonElement = json.encodeToCanonicalJsonElement(serializer, value)

	fun parseToJsonElement(string: String): JsonElement = json.parseToJsonElement(string)
}
