package io.github.ayfri.kore.assertions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.commands.Command
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
private val jsonStringifier = Json {
	prettyPrint = true
	prettyPrintIndent = "\t"
	namingStrategy = JsonNamingStrategy.SnakeCase
}

infix fun Command.assertsIs(string: String) = also { toString() shouldBe string }
infix fun Command.assertsMatches(regex: Regex) = also { toString() shouldMatch regex }

infix fun String.assertsIs(string: String) = also { this shouldBe string }
infix fun String.assertsIsJson(string: String) = also { this shouldBe string }
infix fun String.assertsIsNbt(string: String) = also { this shouldBe string }

infix fun ChatComponents.assertsIsJson(string: String) =
	also { toJsonString(jsonStringifier) shouldBe string }

infix fun <T : Any> T.assertsIs(expected: T) = also { toString() shouldBe expected.toString() }

infix fun Argument.assertsIs(string: String) {
	asString() shouldBe string
}

context(dp: DataPack)
infix fun Generator.assertsIs(expected: String) {
	generateJson(dp) shouldBe expected
}

fun assertsThrows(message: String, function: () -> Any) {
	val errorMessage = try {
		function()
		"No exception was thrown."
	} catch (e: Exception) {
		if (e.message == message) return
		e.message ?: "Exception thrown without message"
	}

	errorMessage shouldBe message
}
