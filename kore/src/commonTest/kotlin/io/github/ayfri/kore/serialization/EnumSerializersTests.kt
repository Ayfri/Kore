package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.StructureRotation
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.generated.Activities
import io.kotest.core.spec.style.FunSpec

class EnumSerializersTests : FunSpec({
	test("enums with a custom name decode it back") {
		json.decodeFromString(Operation.serializer(), "\"+=\"") assertsIs Operation.ADD
		json.decodeFromString(StructureRotation.serializer(), "\"180\"") assertsIs StructureRotation.ROT_180
	}

	test("generated enums decode their id with or without namespace") {
		json.encodeToString(Activities.serializer(), Activities.IDLE) assertsIs "\"minecraft:idle\""
		json.decodeFromString(Activities.serializer(), "\"minecraft:idle\"") assertsIs Activities.IDLE
		json.decodeFromString(Activities.serializer(), "\"idle\"") assertsIs Activities.IDLE
	}
})
