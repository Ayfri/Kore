package io.github.ayfri.kore.website.utils

/**
 * Extracts the Minecraft version from a name or release tag.
 * Supports different formats like "v1.23.0-1.21.4-rc3" or "1.23.0 For 1.21.4-rc3".
 * @param nameOrTag The name or tag of the release
 * @return The Minecraft version or null if no version is found
 */
fun extractMinecraftVersion(nameOrTag: String): String? {
    // Format "v1.23.0-1.21.4-rc3" (tagName)
    val dashSeparator = nameOrTag.indexOf('-', nameOrTag.indexOf('.'))
    if (dashSeparator > 0 && dashSeparator + 1 < nameOrTag.length) {
        val afterDash = nameOrTag.substring(dashSeparator + 1)
        val versionPattern = """^\d+\.\d+(?:\.\d+)?(?:-\w+)?""".toRegex()
        versionPattern.find(afterDash)?.value?.let { return it }
    }

    // Format "1.23.0 For 1.21.4-rc3" (name) or with underscore
    val forIndex = nameOrTag.indexOf(" for ", ignoreCase = true)
    if (forIndex > 0) {
        val afterFor = nameOrTag.substring(forIndex + 5)
        val versionPattern = """^\d+\.\d+(?:\.\d+)?(?:-\w+)?""".toRegex()
        versionPattern.find(afterFor)?.value?.let { return it }
    }

    // Format "... Minecraft 1.21.4 ..."
    val mcIndex = nameOrTag.indexOf("Minecraft ", ignoreCase = true)
    if (mcIndex >= 0) {
        val afterMc = nameOrTag.substring(mcIndex + 10)
        val versionPattern = """^\d+\.\d+(?:\.\d+)?(?:-\w+)?""".toRegex()
        versionPattern.find(afterMc)?.value?.let { return it }
    } else {
        val mcAbbrevIndex = nameOrTag.indexOf("MC ", ignoreCase = true)
        if (mcAbbrevIndex >= 0) {
            val afterMc = nameOrTag.substring(mcAbbrevIndex + 3)
            val versionPattern = """^\d+\.\d+(?:\.\d+)?(?:-\w+)?""".toRegex()
            versionPattern.find(afterMc)?.value?.let { return it }
        }
    }

    return null
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
 * Extracts the Kore version from a release tag.
 * @param tagName The tag of the release
 * @return The Kore version or null if it cannot be extracted
 */
fun extractKoreVersion(tagName: String): String? {
	val versionRegex = Regex("""v?(\d+\.\d+\.\d+)(?:-.+)?$""")
	return versionRegex.find(tagName)?.groupValues?.get(1)
}

/**
 * Checks if a Minecraft version is a snapshot version.
 * Snapshot versions typically contain "snapshot", "pre", "rc", or other pre-release indicators.
 * @param version The Minecraft version string
 * @return True if the version is a snapshot version, false otherwise
 */
fun isSnapshotVersion(version: String?): Boolean {
	if (version == null) return false
	
	val snapshotIndicators = listOf("snapshot", "pre", "rc", "experimental", "beta", "alpha")
	return snapshotIndicators.any { version.lowercase().contains(it) }
}
