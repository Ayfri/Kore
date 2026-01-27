package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.assertions.assertsIsNbt
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.StringifiedNbt

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
	prettyPrint = true
	encodeDefaults = false
	explicitNulls = false
	prettyPrintIndent = "\t"
}

@OptIn(ExperimentalNbtApi::class)
val snbt = StringifiedNbt {
	prettyPrint = true
	nameRootClasses = false
	prettyPrintIndent = "\t"
}

fun serializersTests() {
	singlePropertySimplifierSerializer()
	eitherInlineSerializer()
	eitherInlineSerializerInlined()
}

@Serializable(with = Data.Companion.DataSerializer::class)
private class Data(
	val name: String,
	val optional: String? = null,
) {
	companion object {
		data object DataSerializer : SinglePropertySimplifierSerializer<Data, String>(Data::class, Data::name)
	}
}

fun singlePropertySimplifierSerializer() {
	val partialInstance = Data("name")
	val fullInstance = Data("name", "optional")

	json.encodeToString(Data.serializer(), partialInstance) assertsIsJson """
		"name"
	""".trimIndent()

	json.encodeToString(Data.serializer(), fullInstance) assertsIsJson """
		{
			"name": "name",
			"optional": "optional"
		}
	""".trimIndent()

	snbt.encodeToString(Data.serializer(), partialInstance) assertsIsNbt """
		"name"
	""".trimIndent()

	snbt.encodeToString(Data.serializer(), fullInstance) assertsIsNbt """
		{
			name: "name",
			optional: "optional"
		}
	""".trimIndent()

	val decodedPartial = json.decodeFromString(Data.serializer(), "\"name\"")
	assert(decodedPartial.name == "name")
	assert(decodedPartial.optional == null)

	val decodedFull = json.decodeFromString(
		Data.serializer(), """
		{
			"name": "name",
			"optional": "optional"
		}
	""".trimIndent()
	)
	assert(decodedFull.name == "name")
	assert(decodedFull.optional == "optional")

	val decodedPartialSnbt = snbt.decodeFromString(Data.serializer(), "\"name\"")
	assert(decodedPartialSnbt.name == "name")
	assert(decodedPartialSnbt.optional == null)

	val decodedFullSnbt = snbt.decodeFromString(
		Data.serializer(), """
		{
			name: "name",
			optional: "optional"
		}
	""".trimIndent()
	)
	assert(decodedFullSnbt.name == "name")
	assert(decodedFullSnbt.optional == "optional")
}

@Serializable(with = EitherData.Companion.EitherDataSerializer::class)
private class EitherData(
	val p1: String? = null,
	val p2: Int? = null,
) {
	companion object {
		data object EitherDataSerializer : EitherInlineSerializer<EitherData>(
			EitherData::class,
			EitherData::p1,
			EitherData::p2,
			inline = false
		)
	}
}

fun eitherInlineSerializer() {
	val data1 = EitherData(p1 = "hello")
	val data2 = EitherData(p2 = 42)

	json.encodeToString(EitherData.serializer(), data1) assertsIsJson """
		{
			"p1": "hello"
		}
	""".trimIndent()

	json.encodeToString(EitherData.serializer(), data2) assertsIsJson """
		{
			"p2": 42
		}
	""".trimIndent()

	snbt.encodeToString(EitherData.serializer(), data1) assertsIsNbt """
		{
			p1: "hello"
		}
	""".trimIndent()

	snbt.encodeToString(EitherData.serializer(), data2) assertsIsNbt """
		{
			p2: 42
		}
	""".trimIndent()

	val decoded1 = json.decodeFromString(EitherData.serializer(), "{\"p1\": \"hello\"}")
	assert(decoded1.p1 == "hello")
	assert(decoded1.p2 == null)

	val decoded2 = json.decodeFromString(EitherData.serializer(), "{\"p2\": 42}")
	assert(decoded2.p1 == null)
	assert(decoded2.p2 == 42)

	val decoded1Snbt = snbt.decodeFromString(EitherData.serializer(), "{p1: \"hello\"}")
	assert(decoded1Snbt.p1 == "hello")
	assert(decoded1Snbt.p2 == null)

	val decoded2Snbt = snbt.decodeFromString(EitherData.serializer(), "{p2: 42}")
	assert(decoded2Snbt.p1 == null)
	assert(decoded2Snbt.p2 == 42)
}

@Serializable(with = InlineEitherData.Companion.InlineEitherDataSerializer::class)
private class InlineEitherData(
	val p1: String? = null,
	val p2: Int? = null,
) {
	companion object {
		data object InlineEitherDataSerializer : EitherInlineSerializer<InlineEitherData>(
			InlineEitherData::class,
			InlineEitherData::p1,
			InlineEitherData::p2
		)
	}
}

fun eitherInlineSerializerInlined() {
	val data1 = InlineEitherData(p1 = "hello")
	val data2 = InlineEitherData(p2 = 42)

	json.encodeToString(InlineEitherData.serializer(), data1) assertsIsJson """
		"hello"
	""".trimIndent()

	json.encodeToString(InlineEitherData.serializer(), data2) assertsIsJson """
		42
	""".trimIndent()

	snbt.encodeToString(InlineEitherData.serializer(), data1) assertsIsNbt """
		"hello"
	""".trimIndent()

	snbt.encodeToString(InlineEitherData.serializer(), data2) assertsIsNbt """
		42
	""".trimIndent()

	val decoded1 = json.decodeFromString(InlineEitherData.serializer(), "\"hello\"")
	assert(decoded1.p1 == "hello")
	assert(decoded1.p2 == null)

	val decoded2 = json.decodeFromString(InlineEitherData.serializer(), "42")
	assert(decoded2.p1 == null)
	assert(decoded2.p2 == 42)

	val decoded1Snbt = snbt.decodeFromString(InlineEitherData.serializer(), "\"hello\"")
	assert(decoded1Snbt.p1 == "hello")
	assert(decoded1Snbt.p2 == null)

	val decoded2Snbt = snbt.decodeFromString(InlineEitherData.serializer(), "42")
	assert(decoded2Snbt.p1 == null)
	assert(decoded2Snbt.p2 == 42)
}
