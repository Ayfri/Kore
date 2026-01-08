package io.github.ayfri.kore.bindings.download

import java.nio.file.Path

data object Downloaders {
	private val downloaders = mutableListOf<Downloader>()

	init {
		register(GitHubDownloader)
		register(ModrinthDownloader)
		register(CurseForgeDownloader)
		register(UrlDownloader)
		register(LocalDownloader)
	}

	fun register(downloader: Downloader) {
		// LocalDownloader should be last because it matches everything
		val localIndex = downloaders.indexOf(LocalDownloader)
		if (localIndex != -1) {
			downloaders.add(localIndex, downloader)
		} else {
			downloaders.add(downloader)
		}
	}

	fun download(source: String, skipCache: Boolean = false): Pair<Path, String> {
		val downloader = downloaders.find { it.match(source) }
			?: throw IllegalArgumentException("No downloader found for source: $source")
		return downloader.download(source, skipCache)
	}
}
