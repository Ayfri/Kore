package io.github.ayfri.kore.website.utils

/**
 * Enum defining different Minecraft version patterns with their regex patterns.
 * Declaration order matters: [MinecraftVersionPattern.of] returns the first matching entry.
 */
enum class MinecraftVersionPattern(val regex: Regex, val description: String) {
	SNAPSHOT(
		"""^(?:\d{2}w\d{2}[a-z]|\d{2,}\.\d+(?:\.\d+)?-snapshot-\d+)$""".toRegex(),
		"Minecraft snapshots (e.g., 24w11a or 26.1-snapshot-8)"
	),
	PRE_RELEASE(
		"""^(?:1\.\d+(?:\.\d+)?-pre\d+|\d{2,}\.\d+(?:\.\d+)?-pre-\d+)$""".toRegex(),
		"Minecraft pre-releases (e.g., 1.21.4-pre1 or 26.1-pre-1)"
	),
	RELEASE_CANDIDATE(
		"""^(?:1\.\d+(?:\.\d+)?-rc\d+|\d{2,}\.\d+(?:\.\d+)?-rc-\d+)$""".toRegex(),
		"Release candidates (e.g., 1.21.4-rc1 or 26.1-rc-1)"
	),
	RELEASE("""^(?:1\.\d+(?:\.\d+)?|\d{2,}\.\d+(?:\.\d+)?)$""".toRegex(), "Direct version pattern");

	fun extract(input: String) = regex.find(input)?.value
	fun matches(input: String) = regex.matches(input)

	companion object {
		/** The pattern describing [version], or null when it is not a recognized Minecraft version. */
		fun of(version: String) = entries.firstOrNull { it.matches(version) }
	}
}

private val mainVersionRegex = Regex("""(\d+\.\d+)(?:\.\d+)?(?:-\w+)?""")
private val baseVersionRegex = Regex("""(\d+\.\d+(?:\.\d+)?)""")
private val koreVersionRegex = Regex("""v?(\d+\.\d+\.\d+)(?:-.+)?$""")
private val numberRegex = Regex("""\d+""")
private val suffixNumberRegex = Regex("""-(?:pre|rc)-?(\d+)""")

/**
 * Extracts the Minecraft version from a release tag using defined patterns.
 * @param tag The name or tag of the release
 * @return The Minecraft version or null if no version is found
 */
fun extractMinecraftVersion(tag: String): String? {
	val version = tag.substringAfter('-')
	return MinecraftVersionPattern.entries.firstNotNullOfOrNull { it.extract(version) }
}

/**
 * Extracts the main Minecraft version (major.minor) from a version string.
 * Example: "1.21.4-rc3" becomes "1.21"
 * @param version The complete version string
 * @return The major.minor version or null if the version is null or invalid
 */
fun extractMainMinecraftVersion(version: String?) = version?.let { mainVersionRegex.find(it)?.groupValues?.get(1) }

/**
 * Extracts the base Minecraft version (major.minor.patch) from a version string.
 * Example: "1.21.4-rc3" becomes "1.21.4"
 * @param version The complete version string
 * @return The major.minor.patch version or null if the version is null or invalid
 */
fun extractBaseMinecraftVersion(version: String?) = version?.let { baseVersionRegex.find(it)?.groupValues?.get(1) }

/**
 * Extracts the Kore version from a release tag.
 * @param tagName The tag of the release
 * @return The Kore version or null if it cannot be extracted
 */
fun extractKoreVersion(tagName: String) = koreVersionRegex.find(tagName)?.groupValues?.get(1)

private data class MinecraftVersionKey(val numbers: List<Int>, val suffixRank: Int, val suffixNumber: Int)

private fun buildMinecraftVersionKey(version: String) = MinecraftVersionKey(
	numbers = numberRegex.findAll(version).mapNotNull { it.value.toIntOrNull() }.toList(),
	suffixRank = MinecraftVersionPattern.of(version)?.ordinal?.let { MinecraftVersionPattern.entries.size - it } ?: 0,
	suffixNumber = suffixNumberRegex.find(version)?.groupValues?.get(1)?.toIntOrNull() ?: 0,
)

fun compareMinecraftVersions(left: String, right: String): Int {
	val leftKey = buildMinecraftVersionKey(left)
	val rightKey = buildMinecraftVersionKey(right)

	repeat(maxOf(leftKey.numbers.size, rightKey.numbers.size)) { index ->
		val comparison = leftKey.numbers.getOrElse(index) { 0 }.compareTo(rightKey.numbers.getOrElse(index) { 0 })
		if (comparison != 0) return comparison
	}

	return compareValuesBy(leftKey, rightKey, { it.suffixRank }, { it.suffixNumber })
}
