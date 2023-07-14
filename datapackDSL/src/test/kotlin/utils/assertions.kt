package utils

import DataPack
import Generator
import arguments.Argument
import commands.Command
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language

private val alreadyPrinted = mutableSetOf<Int>()
private val prettyPrintJson = Json {
	prettyPrint = true
}

infix fun Command.assertsIs(string: String) = also { assertsIs(toString(), string) }
infix fun Command.assertsMatches(regex: Regex) = also { assertsMatches(regex, toString()) }

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
	with(stack[2]) {
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
