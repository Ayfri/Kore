package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.StructureRotation
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.Hand
import io.github.ayfri.kore.commands.TemplateRotation
import io.github.ayfri.kore.commands.Visibility
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.generated.Activities
import io.kotest.core.spec.style.FunSpec

class EnumSerializersTests : FunSpec({
	test("enums with a custom name decode it back") {
		json.decodeFromString(Operation.serializer(), "\"+=\"") assertsIs Operation.ADD
		json.decodeFromString(StructureRotation.serializer(), "\"180\"") assertsIs StructureRotation.ROT_180
	}

	test("enum arguments serialize to the string they write in commands") {
		Gamemode.CREATIVE.asString() assertsIs "creative"
		json.encodeToString(Gamemode.serializer(), Gamemode.CREATIVE) assertsIs "\"creative\""
		json.encodeToString(Hand.serializer(), Hand.MAIN_HAND) assertsIs "\"${Hand.MAIN_HAND.asString()}\""
		json.encodeToString(Operation.serializer(), Operation.ADD) assertsIs "\"${Operation.ADD.asString()}\""
		json.encodeToString(TemplateRotation.serializer(), TemplateRotation.CLOCKWISE_180) assertsIs "\"180\""
		json.encodeToString(Visibility.serializer(), Visibility.HIDE_FOR_OTHER_TEAMS) assertsIs "\"hideForOtherTeams\""
	}

	test("generated enums decode their id with or without namespace") {
		json.encodeToString(Activities.serializer(), Activities.IDLE) assertsIs "\"minecraft:idle\""
		json.decodeFromString(Activities.serializer(), "\"minecraft:idle\"") assertsIs Activities.IDLE
		json.decodeFromString(Activities.serializer(), "\"idle\"") assertsIs Activities.IDLE
	}
})
