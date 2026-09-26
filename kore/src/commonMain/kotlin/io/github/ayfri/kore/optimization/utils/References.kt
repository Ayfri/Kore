package io.github.ayfri.kore.optimization.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function

/** A whole `namespace:path` token, so `ns:foo` never matches inside `ns:foo/bar` or `xns:foo`. */
internal val namespacedIdToken = Regex("""[\w.-]+:[\w/.-]+""")

/** Every namespaced id mentioned in [text]. */
internal fun mentionedIds(text: String) = namespacedIdToken.findAll(text).map { it.value }

/**
 * Serializes every generator once and keeps the ids appearing in it, the only reliable way to spot a function
 * referenced from a resource rather than from a command, since a function tag, an advancement reward or an item
 * modifier all store the id in their own shape.
 */
internal fun DataPack.idsReferencedByResources() = generators.flatten().flatMapTo(HashSet()) { mentionedIds(it.generateJson(this)) }

/** Maps each namespaced id to the functions whose lines mention it, built in a single scan of every line. */
internal fun DataPack.functionsById(): Map<String, Set<Function>> {
	val mentions = HashMap<String, MutableSet<Function>>()
	(functions + generatedFunctions).forEach { function ->
		function.lines.forEach { line -> mentionedIds(line).forEach { mentions.getOrPut(it, ::HashSet) += function } }
	}
	return mentions
}
