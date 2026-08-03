package io.github.ayfri.kore.bindings.generation


/** Converts a Minecraft path segment into a valid, conventional Kotlin type name. */
internal fun String.kotlinTypeName(): String {
	val words = replace(Regex("[^a-zA-Z0-9]+"), "_")
		.split("_")
		.filter(String::isNotEmpty)
	val baseName = words.joinToString("") { word ->
		word.lowercase().replaceFirstChar { it.titlecase() }
	}.ifEmpty { "Unnamed" }

	return if (baseName.first().isDigit()) "N$baseName" else baseName
}

/** Converts a Minecraft path segment into a valid Kotlin enum-entry name. */
internal fun String.kotlinEnumName(): String {
	val baseName = replace(Regex("[^a-zA-Z0-9_]"), "_")
		.snakeCase()
		.uppercase()
		.ifEmpty { "UNNAMED" }

	return if (baseName.first().isDigit()) "_$baseName" else baseName
}

/** Allocates unique Kotlin identifiers within one declaration scope. */
internal class KotlinNameAllocator {
	private val allocatedNames = mutableSetOf<String>()

	fun allocate(preferredName: String, collisionSuffix: String): String {
		if (allocatedNames.add(preferredName)) return preferredName

		val suffixedName = preferredName + collisionSuffix
		if (allocatedNames.add(suffixedName)) return suffixedName

		var index = 2
		while (!allocatedNames.add(suffixedName + index)) index++
		return suffixedName + index
	}
}
