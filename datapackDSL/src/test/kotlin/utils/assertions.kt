package utils

import DataPack
import Generator
import arguments.Argument
import commands.Command
import org.intellij.lang.annotations.Language
import kotlinx.serialization.json.Json

private val alreadyPrinted = mutableSetOf<Int>()
private val prettyPrintJson = Json {
	prettyPrint = true
}

infix fun Command.assertsIs(string: String) = also { assertsIs(toString(), string) }
infix fun Command.assertsMatches(regex: Regex) = also { assertsMatches(regex, toString()) }

infix fun String.assertsIs(string: String) = also { assertsIs(this, string) }

infix fun Argument.assertsIs(string: String) = assertsIs(asString(), string)

context(DataPack)
infix fun Generator.assertsIs(@Language("json") expected: String) {
	val result = generateJson(this@DataPack)
	if (result == expected || !alreadyPrinted.add(hashCode())) return

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}

	System.err.println(generateDiffStringJson(expected, result))
}

private fun <T : Any> T.assertsIs(result: String, expected: String) {
	if (result == expected || !alreadyPrinted.add(hashCode())) return

	val stack = Thread.currentThread().stackTrace
	with(stack[3]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}

	System.err.println(generateDiffString(expected, result))
}

private fun <T : Any> T.assertsMatches(regex: Regex, expected: String) {
	if (regex.matches(expected) || !alreadyPrinted.add(hashCode())) return

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}
	System.err.println(generateDiffString(regex, expected))
}

fun assertsThrows(message: String, function: () -> Any) {
	if (!alreadyPrinted.add(function.hashCode())) return

	val errorMessage = try {
		function()
		"No exception was thrown."
	} catch (e: Exception) {
		if (e.message == message) return
		e.message ?: "Exception thrown without message"
	}

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("Bad error message :\nat $className.$methodName($fileName:$lineNumber)")
	}
	System.err.println(generateDiffString(message, errorMessage))
}
