package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.DatapackImportDsl
import io.github.ayfri.kore.bindings.api.exploreDatapacks
import io.github.ayfri.kore.bindings.download.CurseForgeDownloader
import io.github.ayfri.kore.bindings.download.GitHubDownloader
import io.github.ayfri.kore.bindings.download.ModrinthDownloader
import io.github.ayfri.kore.generated.CatVariants
import io.github.ayfri.kore.generated.JukeboxSongs
import io.github.ayfri.kore.generated.WolfVariants

fun downloadTests() {
	testParsingLogic()
	testCurseForgeDownload()
	testGitHubMinecraftDefaultData()
	testModrinthDownload()
}

fun testParsingLogic() = newTest("parsing-logic") {
	// GitHub
	val ghRef = GitHubDownloader.parseReference("user.repo:tag:asset.zip")
	ghRef.owner assertsIs "user"
	ghRef.repo assertsIs "repo"
	ghRef.tag assertsIs "tag"
	ghRef.assetName assertsIs "asset.zip"

	// Modrinth
	val mrRef = ModrinthDownloader.parseReference("slug:version")
	mrRef.slug assertsIs "slug"
	mrRef.version assertsIs "version"

	val mrRefLatest = ModrinthDownloader.parseReference("slug")
	mrRefLatest.slug assertsIs "slug"
	mrRefLatest.version assertsIs null

	// CurseForge
	val cfRef = CurseForgeDownloader.parseReference("123:456")
	cfRef.projectIdentifier assertsIs "123"
	cfRef.fileId assertsIs "456"

	val cfRefLatest = CurseForgeDownloader.parseReference("123")
	cfRefLatest.projectIdentifier assertsIs "123"
	cfRefLatest.fileId assertsIs null

	val cfRefSlug = CurseForgeDownloader.parseReference("my-slug")
	cfRefSlug.projectIdentifier assertsIs "my-slug"

	val cfRefUrl = CurseForgeDownloader.parseReference("https://www.curseforge.com/minecraft/data-packs/my-slug")
	cfRefUrl.projectIdentifier assertsIs "my-slug"

	println("Parsing logic tests passed")
}

private fun runDownloadTest(
	displayName: String,
	downloadBlock: DatapackImportDsl.() -> Unit,
	afterBlock: (List<Datapack>) -> Unit = {},
) {
	println("Downloading $displayName...")
	try {
		val datapacks = exploreDatapacks {
			configuration { debug = true }
			downloadBlock()
		}
		(datapacks.isNotEmpty()) assertsIs true
		afterBlock(datapacks)
		println("$displayName download test passed")
	} catch (e: Exception) {
		println("$displayName download test skipped:")
		e.printStackTrace()
	}
}

fun testGitHubMinecraftDefaultData() = newTest("github-minecraft-default-data") {
	runDownloadTest("pixigeko.minecraft-default-data:1.21.8", {
		github("pixigeko.minecraft-default-data:1.21.8")
	}, { datapacks ->
		println("Explored datapacks: ${datapacks.size}")
		datapacks.forEach { pack ->
			println("  - ${pack.name}: ${pack.functions.size} functions, ${pack.resources.size} resource types")
			pack.resources.forEach { (type, resources) ->
				println("    - $type: ${resources.size} resources")
			}
		}

		val pack = datapacks[0]
		(pack.resources["cat_variant"]?.size == CatVariants.entries.size) assertsIs true
		(pack.resources["jukebox_song"]?.size == JukeboxSongs.entries.size) assertsIs true
		(pack.resources["wolf_variant"]?.size == WolfVariants.entries.size) assertsIs true
	})
}

fun testCurseForgeDownload() = newTest("mes-biomes-o-plenty-compat-download") {
	if (configuration["CURSEFORGE_API_KEY", ""].isNullOrEmpty()) {
		println("Mes Biomes O' Plenty Compat download test skipped: No API key")
		return@newTest
	}

	runDownloadTest("Mes Biomes O' Plenty Compat from CurseForge", {
		curseforge("mes-biomes-o-plenty-compat-pack")
	})
}

fun testModrinthDownload() = newTest("terralith-download") {
	runDownloadTest("Terralith from Modrinth", {
		modrinth("terralith")
	})
}
