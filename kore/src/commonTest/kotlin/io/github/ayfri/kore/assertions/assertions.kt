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

/** Matches unquoted number literals, so JVM/JS number-formatting quirks can be compared by value instead of text. */
private val numberTokenRegex = Regex("""(?<!["\w.])-?\d+(?:\.\d+)?(?:[eE][+-]?\d+)?[dfsbL]?(?!["\w])""")

private fun numbersRepresentSameValue(a: String, b: String): Boolean {
	val suffixChars = "dfsbL"
	val aSuffix = a.lastOrNull()?.takeIf { it in suffixChars }
	val bSuffix = b.lastOrNull()?.takeIf { it in suffixChars }
	if (aSuffix != bSuffix) return false

	val aNumber = (if (aSuffix != null) a.dropLast(1) else a).toDoubleOrNull() ?: return a == b
	val bNumber = (if (bSuffix != null) b.dropLast(1) else b).toDoubleOrNull() ?: return a == b

	if (aNumber == bNumber) return true

	// tolerates JS widening a Float to Double (e.g. 0.1f -> 0.10000000149011612)
	val scale = maxOf(1.0, kotlin.math.abs(aNumber), kotlin.math.abs(bNumber))
	return kotlin.math.abs(aNumber - bNumber) <= scale * 1e-6
}

private infix fun String.equalsAcrossPlatforms(other: String): Boolean {
	val ownMatches = numberTokenRegex.findAll(this).iterator()
	val otherMatches = numberTokenRegex.findAll(other).iterator()
	var ownPos = 0
	var otherPos = 0

	while (ownMatches.hasNext() && otherMatches.hasNext()) {
		val ownMatch = ownMatches.next()
		val otherMatch = otherMatches.next()

		if (substring(ownPos, ownMatch.range.first) != other.substring(otherPos, otherMatch.range.first)) return false
		if (!numbersRepresentSameValue(ownMatch.value, otherMatch.value)) return false

		ownPos = ownMatch.range.last + 1
		otherPos = otherMatch.range.last + 1
	}

	if (ownMatches.hasNext() || otherMatches.hasNext()) return false
	return substring(ownPos) == other.substring(otherPos)
}

private infix fun String.shouldBeAcrossPlatforms(expected: String) {
	if (!(this equalsAcrossPlatforms expected)) this shouldBe expected
}

infix fun Command.assertsIs(string: String) = also { toString() shouldBeAcrossPlatforms string }

infix fun Command.assertsMatches(regex: Regex) = also { toString() shouldMatch regex }

infix fun String.assertsIs(string: String) = also { this shouldBeAcrossPlatforms string }

infix fun String.assertsIsJson(string: String) = also { this shouldBeAcrossPlatforms string }

infix fun String.assertsIsNbt(string: String) = also { this shouldBeAcrossPlatforms string }

infix fun ChatComponents.assertsIsJson(string: String) =
	also { toJsonString(jsonStringifier) shouldBeAcrossPlatforms string }

infix fun <T : Any> T.assertsIs(expected: T) = also { toString() shouldBeAcrossPlatforms expected.toString() }

infix fun Argument.assertsIs(string: String) {
	asString() shouldBeAcrossPlatforms string
}

context(dp: DataPack)
infix fun Generator.assertsIs(expected: String) {
	generateJson(dp) shouldBeAcrossPlatforms expected
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
