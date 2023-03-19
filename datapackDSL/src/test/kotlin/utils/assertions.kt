package utils

import commands.Command

private val alreadyPrinted = mutableSetOf<Int>()

infix fun Command.assertsIs(string: String) {
	if (toString() == string || !alreadyPrinted.add(hashCode())) return

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}
	System.err.println(generateDiffString(string, toString()))
}

infix fun Command.assertsMatches(regex: Regex) {
	if (regex.matches(toString()) || !alreadyPrinted.add(hashCode())) return

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}
	System.err.println(generateDiffString(regex, toString()))
}
