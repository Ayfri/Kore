package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.assertions.assertsIsNbt
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
