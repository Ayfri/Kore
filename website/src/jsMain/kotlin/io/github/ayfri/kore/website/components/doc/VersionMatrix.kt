package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.updates.GitHubRelease
import io.github.ayfri.kore.website.components.updates.GitHubService
import io.github.ayfri.kore.website.utils.compareMinecraftVersions
import io.github.ayfri.kore.website.utils.extractMainMinecraftVersion
import io.github.ayfri.kore.website.utils.formatDate
import io.github.ayfri.kore.website.utils.paddingX
import io.github.ayfri.kore.website.utils.paddingY
import io.github.ayfri.kore.website.utils.rel
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private const val MAVEN_GROUP = "io.github.ayfri.kore"
private const val MAVEN_ARTIFACT = "kore"

/** A published Kore version and the Minecraft version it targets. */
private data class VersionRow(
	val release: GitHubRelease,
	val koreVersion: String,
	val minecraftVersion: String,
	val gradleCoordinate: String,
	val isStable: Boolean,
)

/** Maps a GitHub release to a [VersionRow], or `null` if its tag doesn't encode both versions. */
private fun GitHubRelease.toVersionRow(): VersionRow? {
	val koreVersion = koreVersion ?: return null
	val minecraftVersion = minecraftVersion ?: return null

	return VersionRow(
		release = this,
		koreVersion = koreVersion,
		minecraftVersion = minecraftVersion,
		gradleCoordinate = tagName.removePrefix("v"),
		isStable = isStableRelease,
	)
}

/** Central Portal artifact page for a released (non-snapshot) coordinate. */
private fun mavenCentralUrl(coordinate: String) = "https://central.sonatype.com/artifact/$MAVEN_GROUP/$MAVEN_ARTIFACT/$coordinate"

/** Central Portal snapshot repository browse URL for a `-SNAPSHOT` coordinate. */
private fun mavenSnapshotUrl(coordinate: String): String {
	val groupPath = MAVEN_GROUP.replace('.', '/')
	return "https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/$groupPath/$MAVEN_ARTIFACT/$coordinate/"
}

/** `true` for Minecraft's year-based scheme (`26.1`, `26.1.1`, ...), where the patch is a hotfix of the same drop. */
private fun isLegacyScheme(minecraftVersion: String) = minecraftVersion.startsWith("1.")

/**
 * Groups hotfix-only Minecraft versions so the matrix keeps one row per meaningful version: same feature drop
 * (`26.1.x`) for the year-based scheme, same Kore version (e.g. `1.21.9`/`1.21.10` both shipped as Kore `1.37.0`)
 * for the legacy scheme.
 */
private fun VersionRow.familyKey() =
	if (isLegacyScheme(minecraftVersion)) "kore:$koreVersion" else "mc:${extractMainMinecraftVersion(minecraftVersion)}"

private val byHighestMinecraftVersion = Comparator<VersionRow> { a, b -> compareMinecraftVersions(a.minecraftVersion, b.minecraftVersion) }

private val allRows by lazy { GitHubService.getReleases().mapNotNull { it.toVersionRow() } }

private val stableRows by lazy {
	allRows
		.filter { it.isStable }
		.groupBy { it.minecraftVersion }
		.map { (_, rows) -> rows.maxBy { it.release.publishedTime } }
		.groupBy { it.familyKey() }
		.map { (_, rows) -> rows.maxWith(byHighestMinecraftVersion) }
		.sortedWith(byHighestMinecraftVersion.reversed())
}

/** The latest release overall, kept only when it is not stable, so it shows up as the `Snapshot` row. */
private val snapshotRow by lazy { allRows.maxByOrNull { it.release.publishedTime }?.takeIf { !it.isStable } }

/**
 * One Kore version per meaningful Minecraft version, the latest tagged pre-release as a separate `Snapshot` row,
 * and the latest continuous `-SNAPSHOT` build published from `master` as `Maven Snapshot`. Generated from GitHub
 * releases and build-time project globals on every website build.
 */
@Composable
fun VersionMatrix() {
	Style(VersionMatrixStyle)

	val mavenSnapshotCoordinate = AppGlobals["projectVersion"]?.let { projectVersion ->
		AppGlobals["minecraftVersion"]?.let { minecraftVersion -> "$projectVersion-$minecraftVersion-SNAPSHOT" }
	}

	Table({
		classes(VersionMatrixStyle.table)
	}) {
		Thead {
			Tr {
				Th { Text("Minecraft version") }
				Th { Text("Kore version") }
				Th { Text("Type") }
				Th { Text("Gradle coordinate") }
				Th { Text("Published") }
			}
		}

		Tbody {
			mavenSnapshotCoordinate?.let { coordinate ->
				MavenSnapshotRowLine(coordinate)
			}
			snapshotRow?.let { VersionRowLine(it, "Snapshot", mavenCentralUrl(it.gradleCoordinate)) }
			stableRows.forEach { VersionRowLine(it, "Stable", mavenCentralUrl(it.gradleCoordinate)) }
		}
	}
}

@Composable
private fun VersionRowLine(row: VersionRow, typeLabel: String, coordinateUrl: String) {
	Tr {
		Td { Text(row.minecraftVersion) }
		Td { Text(row.koreVersion) }
		Td { TypeBadge(typeLabel) }
		Td { CoordinateLink(row.gradleCoordinate, coordinateUrl) }
		Td { Text(formatDate(row.release.publishedAt)) }
	}
}

/** Continuously published build from the latest commit on `master`, not tied to any GitHub release. */
@Composable
private fun MavenSnapshotRowLine(coordinate: String) {
	Tr {
		Td { Text(AppGlobals["minecraftVersion"] ?: "-") }
		Td { Text(AppGlobals["projectVersion"] ?: "-") }
		Td { TypeBadge("Maven Snapshot") }
		Td { CoordinateLink(coordinate, mavenSnapshotUrl(coordinate)) }
		Td { Text("master (continuous)") }
	}
}

@Composable
private fun TypeBadge(label: String) {
	Span({
		classes(VersionMatrixStyle.typeBadge)
		if (label != "Stable") classes(VersionMatrixStyle.snapshotBadge)
	}) {
		Text(label)
	}
}

@Composable
private fun CoordinateLink(coordinate: String, url: String) {
	A(url, {
		classes(VersionMatrixStyle.coordinate)
		target(ATarget.Blank)
		rel("noopener", "noreferrer")
	}) {
		Text("$MAVEN_GROUP:$MAVEN_ARTIFACT:$coordinate")
	}
}

private object VersionMatrixStyle : StyleSheet() {
	val table by style {
		width(100.percent)
	}

	val typeBadge by style {
		backgroundColor(rgba(63, 185, 80, 0.18))
		borderRadius(GlobalStyle.roundingButton)
		color(rgb(63, 185, 80))
		fontSize(0.85.cssRem)
		fontWeight(600)
		paddingX(0.5.cssRem)
		paddingY(0.15.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val snapshotBadge by style {
		backgroundColor(rgba(88, 166, 255, 0.18))
		color(rgb(88, 166, 255))
	}

	val coordinate by style {
		color(GlobalStyle.linkColor)
		fontFamily("Consolas", "Monaco", "Andale Mono", "Ubuntu Mono", "monospace")
		fontSize(0.85.cssRem)
		textDecoration("none")

		hover(self) style {
			color(GlobalStyle.linkColorHover)
			textDecoration("underline")
		}
	}
}
