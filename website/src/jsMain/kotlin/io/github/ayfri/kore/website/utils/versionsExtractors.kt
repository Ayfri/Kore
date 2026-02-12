package io.github.ayfri.kore.website.utils

/**
 * Enum defining different Minecraft version patterns with their regex patterns
 */
enum class MinecraftVersionPattern(val regex: Regex, val description: String) {
	SNAPSHOT("""^(\d{2}w\d{2}[a-z])$""".toRegex(), "Minecraft snapshots (e.g., 24w11a)"),
	PRE_RELEASE("""^(1\.\d+(?:\.\d+)?-pre\d+)$""".toRegex(), "Minecraft pre-releases (e.g., 1.21.4-pre1)"),
	RELEASE_CANDIDATE("""^(1\.\d+(?:\.\d+)?-rc\d+)$""".toRegex(), "Release candidates (e.g., 1.21.4-rc1)"),
	RELEASE("""^(1\.\d+(?:\.\d+)?)$""".toRegex(), "Direct version pattern");

	fun extract(input: String) = regex.find(input)?.value
	fun matches(input: String) = regex.matches(input)
}

/**
 * Extracts the Minecraft version from a release tag using defined patterns.
 * @param tag The name or tag of the release
 * @return The Minecraft version or null if no version is found
 */
fun extractMinecraftVersion(tag: String): String? {
	val version = tag.substringAfter('-')

	// Try snapshot pattern first
	return MinecraftVersionPattern.SNAPSHOT.extract(version) ?:
		// Try pre-release pattern
		MinecraftVersionPattern.PRE_RELEASE.extract(version) ?:
		// Try release candidate pattern
		MinecraftVersionPattern.RELEASE_CANDIDATE.extract(version) ?:
		// Try direct version pattern
		MinecraftVersionPattern.RELEASE.extract(version)
}

/**
 * Extracts the main Minecraft version (major.minor) from a version string.
 * Example: "1.21.4-rc3" becomes "1.21"
 * @param version The complete version string
 * @return The major.minor version or null if the version is null or invalid
 */
fun extractMainMinecraftVersion(version: String?): String? {
	if (version == null) return null

	val mainVersionRegex = Regex("""(\d+\.\d+)(?:\.\d+)?(?:-\w+)?""")
	return mainVersionRegex.find(version)?.groupValues?.get(1)
}

/**
 * Extracts the base Minecraft version (major.minor.patch) from a version string.
 * Example: "1.21.4-rc3" becomes "1.21.4"
 * @param version The complete version string
 * @return The major.minor.patch version or null if the version is null or invalid
 */
fun extractBaseMinecraftVersion(version: String?): String? {
	if (version == null) return null

	val baseVersionRegex = Regex("""(\d+\.\d+(?:\.\d+)?)""")
	return baseVersionRegex.find(version)?.groupValues?.get(1)
}

/**
 * Extracts the Kore version from a release tag.
 * @param tagName The tag of the release
 * @return The Kore version or null if it cannot be extracted
 */
fun extractKoreVersion(tagName: String): String? {
	val versionRegex = Regex("""v?(\d+\.\d+\.\d+)(?:-.+)?$""")
	return versionRegex.find(tagName)?.groupValues?.get(1)
}

data class MinecraftVersionKey(
	val numbers: List<Int>,
	val suffixRank: Int,
	val suffixNumber: Int,
)

fun compareMinecraftVersions(left: String, right: String): Int {
	val leftKey = buildMinecraftVersionKey(left)
	val rightKey = buildMinecraftVersionKey(right)

	val maxSize = maxOf(leftKey.numbers.size, rightKey.numbers.size, 3)
	val leftNumbers = leftKey.numbers + List(maxSize - leftKey.numbers.size) { 0 }
	val rightNumbers = rightKey.numbers + List(maxSize - rightKey.numbers.size) { 0 }

	for (index in 0 until maxSize) {
		val comparison = leftNumbers[index].compareTo(rightNumbers[index])
		if (comparison != 0) return comparison
	}

	val suffixComparison = leftKey.suffixRank.compareTo(rightKey.suffixRank)
	if (suffixComparison != 0) return suffixComparison

	return leftKey.suffixNumber.compareTo(rightKey.suffixNumber)
}

private fun buildMinecraftVersionKey(version: String): MinecraftVersionKey {
	val numbers = Regex("\\d+").findAll(version).mapNotNull { it.value.toIntOrNull() }.toList()
	val suffixRank = when {
		MinecraftVersionPattern.RELEASE.matches(version) -> 3
		MinecraftVersionPattern.RELEASE_CANDIDATE.matches(version) -> 2
		MinecraftVersionPattern.PRE_RELEASE.matches(version) -> 1
		MinecraftVersionPattern.SNAPSHOT.matches(version) -> 0
		else -> 0
	}
	val suffixNumber = Regex("""-(?:pre|rc)(\d+)""").find(version)?.groupValues?.get(1)?.toIntOrNull() ?: 0

	return MinecraftVersionKey(numbers, suffixRank, suffixNumber)
}
