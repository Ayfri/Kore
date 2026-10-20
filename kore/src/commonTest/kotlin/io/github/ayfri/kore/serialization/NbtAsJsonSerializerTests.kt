package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.toSnbt
import io.kotest.core.spec.style.FunSpec

class NbtAsJsonSerializerTests : FunSpec({
	test("json arrays of one type keep their tag type") {
		json.decodeFromString(NbtAsJsonSerializer, "[1, 2]").toSnbt() assertsIs "[1,2]"
		json.decodeFromString(NbtAsJsonSerializer, """["a", "b"]""").toSnbt() assertsIs """["a","b"]"""
		json.decodeFromString(NbtAsJsonSerializer, """[{"a": 1}, {"a": 2}]""").toSnbt() assertsIs "[{a:1},{a:2}]"
	}

	test("mixed json arrays fall back to strings") {
		json.decodeFromString(NbtAsJsonSerializer, """[1, "a"]""").toSnbt() assertsIs """["1","a"]"""
	}

	test("json primitives map to the smallest fitting tag") {
		json.decodeFromString(NbtAsJsonSerializer, "true").toSnbt() assertsIs "1b"
		json.decodeFromString(NbtAsJsonSerializer, "3000000000").toSnbt() assertsIs "3000000000L"
		json.decodeFromString(NbtAsJsonSerializer, "0.5").toSnbt() assertsIs "0.5d"
	}

	test("value-based numbers map whole numbers to int tags and promote mixed number lists") {
		fun snbt(text: String) = NbtAsJsonSerializer.fromJson(json.parseToJsonElement(text), valueBasedNumbers = true).toSnbt()

		snbt("1.0") assertsIs "1"
		snbt("0.5") assertsIs "0.5d"
		snbt("3000000000.0") assertsIs "3000000000L"
		snbt("[1.0, 2.0]") assertsIs "[1,2]"
		snbt("[1.0, 0.5, 2]") assertsIs "[1.0d,0.5d,2.0d]"
		snbt("[1, 3000000000]") assertsIs "[1L,3000000000L]"
	}
})
