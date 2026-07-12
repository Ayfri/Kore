package io.github.ayfri.kore.utils

import kotlinx.io.files.Path

/** Windows drive-letter (`C:\...`) or POSIX-rooted (`/...`) prefix, checked without [Path.isAbsolute]. */
private fun String.looksAbsolute() = startsWith("/") || startsWith("\\") || (length >= 2 && this[1] == ':')

/**
 * Pure string-based path joining, kept out of [filesUtils.kt] on purpose: unlike [Path.resolveSafe], it never
 * touches [kotlinx.io.files.SystemPathSeparator]/[kotlinx.io.files.SystemFileSystem]/[Path.isAbsolute], nor the
 * multi-segment [Path] constructor - all of which require Node's `path` module on Kotlin/JS and would crash in
 * the browser. Only the single-string [Path] constructor is safe there.
 */
fun Path.resolve(vararg paths: String): Path = Path(paths.fold(toString()) { acc, part ->
	if (part.looksAbsolute()) part else "$acc/$part"
})

fun Path.resolve(vararg paths: Path): Path = resolve(*paths.map(Path::toString).toTypedArray())
