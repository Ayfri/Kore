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
})
