package io.github.ayfri.kore.website.components.updates

internal fun buildMinecraftChangelogUrl(release: GitHubRelease): String? {
	val mcVersion = release.getMinecraftVersion() ?: return null
	val baseVersion =
		mcVersion.substringBeforeLast("-snapshot-").substringBeforeLast("-pre-").substringBeforeLast("-rc-")
			.substringBefore("-pre").substringBefore("-rc")
	val normalizedBase = baseVersion.replace(".", "-")

	return when {
		release.isSnapshot() -> buildSnapshotChangelogUrl(mcVersion, normalizedBase)
		release.isPreRelease() -> buildPrereleaseChangelogUrl(mcVersion, normalizedBase)
		release.isReleaseCandidate() -> buildReleaseCandidateChangelogUrl(mcVersion, normalizedBase)
		release.isRelease() -> "https://www.minecraft.net/en-us/article/minecraft-java-edition-$normalizedBase"
		else -> null
	}
}

private fun buildSnapshotChangelogUrl(mcVersion: String, normalizedBase: String): String? {
	val weeklySnapshot = Regex("""^\d{2}w\d{2}[a-z]$""")
	if (weeklySnapshot.matches(mcVersion)) {
		return "https://www.minecraft.net/en-us/article/minecraft-snapshot-$mcVersion"
	}

	val snapshotNumber = Regex("""-snapshot-(\d+)$""").find(mcVersion)?.groupValues?.get(1) ?: return null
	return "https://www.minecraft.net/en-us/article/minecraft-$normalizedBase-snapshot-$snapshotNumber"
}

private fun buildPrereleaseChangelogUrl(mcVersion: String, normalizedBase: String): String? {
	val preNumber = Regex("""-pre-?(\d+)$""").find(mcVersion)?.groupValues?.get(1) ?: return null
	return "https://www.minecraft.net/en-us/article/minecraft-$normalizedBase-pre-release-$preNumber"
}

private fun buildReleaseCandidateChangelogUrl(mcVersion: String, normalizedBase: String): String? {
	val rcNumber = Regex("""-rc-?(\d+)$""").find(mcVersion)?.groupValues?.get(1) ?: return null
	return "https://www.minecraft.net/en-us/article/minecraft-$normalizedBase-release-candidate-$rcNumber"
}