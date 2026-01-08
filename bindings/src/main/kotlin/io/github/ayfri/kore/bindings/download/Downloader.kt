package io.github.ayfri.kore.bindings.download

import java.nio.file.Path

interface Downloader {
	fun download(reference: String, skipCache: Boolean = false): Pair<Path, String>
	fun match(source: String): Boolean
}
