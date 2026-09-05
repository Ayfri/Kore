package io.github.ayfri.kore.optimization.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function

/**
 * Serializes every generator once, the only reliable way to spot a function referenced from a resource rather than
 * from a command, since a function tag, an advancement reward or an item modifier all store the id in their own shape.
 */
internal fun DataPack.generatorsJson() = generators.flatten().joinToString("\n") { it.generateJson(this) }

/** Matches [id] only when it is not the prefix of a longer function id, so `ns:foo` never matches `ns:foo/bar`. */
internal fun functionIdPattern(id: String) = Regex("""${Regex.escape(id)}(?![\w/.-])""")

/** Whether any function other than [function] itself mentions its id. */
internal fun DataPack.isCalled(function: Function): Boolean {
	val pattern = functionIdPattern(function.asId())
	return (functions + generatedFunctions).any { it !== function && it.lines.any(pattern::containsMatchIn) }
}
