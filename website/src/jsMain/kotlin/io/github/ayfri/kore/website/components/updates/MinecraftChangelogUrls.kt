package io.github.ayfri.kore.website.components.updates

private const val ARTICLE_BASE = "https://www.minecraft.net/en-us/article"

private val weeklySnapshotRegex = Regex("""^\d{2}w\d{2}[a-z]$""")
private val snapshotNumberRegex = Regex("""-snapshot-(\d+)$""")
private val preReleaseNumberRegex = Regex("""-pre-?(\d+)$""")
private val releaseCandidateNumberRegex = Regex("""-rc-?(\d+)$""")

private fun Regex.numberIn(version: String) = find(version)?.groupValues?.get(1)

internal fun buildMinecraftChangelogUrl(release: GitHubRelease): String? {
	val version = release.minecraftVersion ?: return null
	val base = version.substringBefore("-").replace(".", "-")

	return when {
		release.isSnapshot -> when {
			weeklySnapshotRegex.matches(version) -> "$ARTICLE_BASE/minecraft-snapshot-$version"
			else -> snapshotNumberRegex.numberIn(version)?.let { "$ARTICLE_BASE/minecraft-$base-snapshot-$it" }
		}

		release.isPreReleaseVersion -> preReleaseNumberRegex.numberIn(version)
			?.let { "$ARTICLE_BASE/minecraft-$base-pre-release-$it" }

		release.isReleaseCandidate -> releaseCandidateNumberRegex.numberIn(version)
			?.let { "$ARTICLE_BASE/minecraft-$base-release-candidate-$it" }

		release.isStableRelease -> "$ARTICLE_BASE/minecraft-java-edition-$base"

		else -> null
	}
}
