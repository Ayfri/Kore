package io.github.ayfri.kore.bindings.download

import kotlinx.io.files.Path

internal interface Downloader {
	suspend fun download(reference: String, skipCache: Boolean = false): Pair<Path, String>
	fun match(source: String): Boolean
}
