package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.bindings.getFromCacheOrDownload

data object UrlDownloader : Downloader {
	override fun match(source: String) = source.startsWith("http://") || source.startsWith("https://")

	override fun download(reference: String, skipCache: Boolean) = getFromCacheOrDownload(reference, skipCache)
}
