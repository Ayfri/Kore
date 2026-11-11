package website

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

/** The body of [url], null when the request fails: the fetch tasks then keep the files of their previous run. */
private fun DefaultTask.download(url: String): ByteArray? = try {
	val connection = URI(url).toURL().openConnection() as HttpURLConnection
	connection.connectTimeout = 10_000
	connection.readTimeout = 30_000
	connection.setRequestProperty("User-Agent", "KoreWebsite/1.0 (+https://kore.ayfri.com)")
	if (url.startsWith("https://api.github.com/")) {
		connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
		/** `GITHUB_TOKEN` lifts the limit of 60 requests an hour. */
		System.getenv("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }?.let { connection.setRequestProperty("Authorization", "token $it") }
	}
	if (connection.responseCode == 200) connection.inputStream.use { it.readBytes() }
	else null.also { logger.error("$url returned ${connection.responseCode}. Body: ${connection.errorStream?.bufferedReader()?.use { it.readText() }}") }
} catch (e: IOException) {
	null.also { logger.error("Failed to fetch $url.", e) }
}

abstract class FetchGitHubReleases : DefaultTask() {
	@get:OutputFile
	abstract val latestTagFile: RegularFileProperty

	/** `tag<TAB>prerelease<TAB>published_at` per published release, newest first, for the llms.txt versions and the home page version matrix. */
	@get:OutputFile
	abstract val releasesFile: RegularFileProperty

	/** The Kotlin source with every release, read by the updates page. */
	@get:OutputFile
	abstract val sourceFile: RegularFileProperty

	init {
		group = "kore"
		description = "Fetches GitHub releases and generates a Kotlin file with the data"
	}

	@TaskAction
	fun fetch() {
		val releases = mutableListOf<Map<*, *>>()
		var page = 1
		do {
			@Suppress("UNCHECKED_CAST")
			val pageReleases = download("https://api.github.com/repos/Ayfri/Kore/releases?per_page=100&page=${page++}")
				?.let { JsonSlurper().parseText(it.decodeToString()) as List<Map<*, *>> }
			if (pageReleases == null && outputs.files.all { it.exists() }) return logger.warn("Keeping the previous GitHub releases files.")
			releases += pageReleases.orEmpty()
		} while (pageReleases?.size == 100)
		/** A draft, listed for a token with write access, has no publication date. */
		releases.removeAll { it["published_at"] == null }

		/** Same pick as `GitHubService.latestRelease`, ISO timestamps sort chronologically as strings. */
		val newestFirst = releases.sortedByDescending { it["published_at"] as String }
		latestTagFile.get().asFile.writeText(newestFirst.firstOrNull()?.get("tag_name") as String? ?: "")
		releasesFile.get().asFile.writeText(newestFirst.joinToString("") { "${it["tag_name"]}\t${it["prerelease"]}\t${it["published_at"]}\n" })
		sourceFile.get().asFile.writeText(releasesSource(releases))

		logger.lifecycle("Generated GitHub releases file with ${releases.size} releases → ${sourceFile.get().asFile.path}")
	}

	private fun String.escapeForKotlinRawString() = replace("\"\"\"", "\\\"\\\"\\\"").replace("$", "${'$'}{'$'}")

	private fun Map<*, *>.quoted(key: String) = "\"${(this[key] as String? ?: "").replace("\"", "\\\"").replace("$", "\\$")}\""

	private fun releasesSource(releases: List<Map<*, *>>) = buildString {
		appendLine("// This file is generated. Do not modify directly.")
		appendLine("package io.github.ayfri.kore.website")
		appendLine()
		appendLine("import io.github.ayfri.kore.website.components.updates.GitHubAsset")
		appendLine("import io.github.ayfri.kore.website.components.updates.GitHubRelease")
		appendLine()
		appendLine("val gitHubReleases = listOf<GitHubRelease>(")
		releases.forEach { release ->
			@Suppress("UNCHECKED_CAST")
			val assets = (release["assets"] as List<Map<*, *>>?).orEmpty().joinToString { asset ->
				"GitHubAsset(id = ${asset["id"]}, name = ${asset.quoted("name")}, browserDownloadUrl = ${asset.quoted("browser_download_url")}, " +
					"contentType = ${asset.quoted("content_type")}, size = ${asset["size"]}, downloadCount = ${asset["download_count"]})"
			}
			appendLine("\tGitHubRelease(")
			appendLine("\t\tid = ${release["id"]},")
			appendLine("\t\tname = ${release.quoted("name")},")
			appendLine("\t\ttagName = ${release.quoted("tag_name")},")
			appendLine("\t\thtmlUrl = ${release.quoted("html_url")},")
			appendLine("\t\turl = ${release.quoted("url")},")
			appendLine("\t\tcreatedAt = ${release.quoted("created_at")},")
			appendLine("\t\tpublishedAt = ${release.quoted("published_at")},")
			appendLine("\t\tbody = \"\"\"${(release["body"] as String? ?: "").escapeForKotlinRawString()}\"\"\",")
			appendLine("\t\tisPrerelease = ${release["prerelease"]},")
			appendLine("\t\tassets = ${if (assets.isEmpty()) "emptyList()" else "listOf($assets)"},")
			appendLine("\t),")
		}
		appendLine(")")
	}
}

abstract class FetchGitHubStars : DefaultTask() {
	/** The Kotlin source with the star count, read by the site header. */
	@get:OutputFile
	abstract val sourceFile: RegularFileProperty

	/** The star count alone, read by the Open Graph cards. */
	@get:OutputFile
	abstract val starsFile: RegularFileProperty

	init {
		group = "kore"
		description = "Fetches the GitHub repository star count and generates a Kotlin file with the data"
	}

	@TaskAction
	fun fetch() {
		val repository = download("https://api.github.com/repos/Ayfri/Kore")?.let { JsonSlurper().parseText(it.decodeToString()) as Map<*, *> }
		if (repository == null && outputs.files.all { it.exists() }) return logger.warn("Keeping the previous GitHub stars files.")
		val stars = repository?.get("stargazers_count") as? Int

		starsFile.get().asFile.writeText(stars?.toString().orEmpty())
		sourceFile.get().asFile.writeText(buildString {
			appendLine("// This file is generated. Do not modify directly.")
			appendLine("package io.github.ayfri.kore.website")
			appendLine("")
			appendLine("val gitHubStars: Int? = $stars")
		})

		logger.lifecycle("Generated GitHub stars file (stars = $stars) → ${sourceFile.get().asFile.path}")
	}
}

abstract class FetchMinecraftVersions : DefaultTask() {
	@get:OutputFile
	abstract val versionsFile: RegularFileProperty

	init {
		group = "kore"
		description = "Fetches the pack format and Java version of every Minecraft version, read by the doc placeholders"
	}

	@TaskAction
	fun fetch() {
		val versions = download("https://raw.githubusercontent.com/PixiGeko/Minecraft-generated-data/master/versions_data.json") ?: return
		versionsFile.get().asFile.writeBytes(versions)
		logger.lifecycle("Fetched Minecraft versions data → ${versionsFile.get().asFile.path}")
	}
}
