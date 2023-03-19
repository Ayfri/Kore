package utils

import commands.Command

private val alreadyPrinted = mutableSetOf<Int>()

infix fun Command.assertsIs(string: String): Command {
	if (toString() == string || !alreadyPrinted.add(hashCode())) return this

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}
	System.err.println(generateDiffString(string, toString()))

	return this
}

infix fun Command.assertsMatches(regex: Regex): Command {
	if (regex.matches(toString()) || !alreadyPrinted.add(hashCode())) return this

	val stack = Thread.currentThread().stackTrace
	with(stack[2]) {
		System.err.println("\nat $className.$methodName($fileName:$lineNumber)")
	}
	System.err.println(generateDiffString(regex, toString()))

	return this
}
