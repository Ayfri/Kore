package website

import java.io.File

/** A doc page read from its front matter, `slugs` being the route split on `/` after the leading empty segment. */
data class DocEntry(
	val date: String,
	val dateModified: String,
	val desc: String,
	val file: File,
	val keywords: List<String>,
	val navTitle: String,
	val position: Int?,
	val slugs: List<String>,
	val title: String,
) {
	val route = slugs.joinToString("/")
}

private val stableMinecraftVersionRegex = Regex("""\d+\.\d+(\.\d+)?""")

/** A GitHub release read back from `releases.tsv`, tagged `v<kore version>-<minecraft version>`. */
data class ReleaseEntry(val prerelease: Boolean, val published: String, val tag: String) {
	val coordinate = tag.removePrefix("v")
	val koreVersion = coordinate.substringBefore('-')
	val minecraftVersion = coordinate.substringAfter('-', "")

	/** Same rule as the site's version matrix: a plain Minecraft release (`26.2`, `1.21.4`), never a snapshot, pre-release or release candidate. */
	val isStable = !prerelease && stableMinecraftVersionRegex.matches(minecraftVersion)

	/** Zero-padded numbers, so plain string comparison orders `26.10` after `26.2`. */
	val minecraftSortKey = minecraftVersion.split('.').joinToString(".") { it.padStart(4, '0') }
}

/** The releases of `releases.tsv`, newest first. */
class ReleaseHistory(val entries: List<ReleaseEntry>) {
	val latestStable = entries.firstOrNull { it.isStable }

	/** The newest release overall, when it is not stable, is the pre-release newer than the latest stable one. */
	val latestPreRelease = entries.firstOrNull()?.takeIf { !it.isStable }

	/** One row per Minecraft drop like the home page widget: hotfixes fold into their `26.x`, or into the same Kore version before 26. */
	val stableRows = entries.filter { it.isStable }
		.distinctBy { it.minecraftVersion }
		.groupBy { if (it.minecraftVersion.startsWith("1.")) "kore:${it.koreVersion}" else "mc:${it.minecraftVersion.split('.').take(2).joinToString(".")}" }
		.map { (_, rows) -> rows.maxBy { it.minecraftSortKey } }
		.sortedByDescending { it.minecraftSortKey }
}
