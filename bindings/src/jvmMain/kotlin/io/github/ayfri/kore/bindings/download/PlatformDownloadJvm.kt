package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.utils.ensureParents
import io.github.ayfri.kore.utils.exists
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.toSource
import io.github.ayfri.kore.utils.write
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import java.net.HttpURLConnection
import java.net.URI

internal actual val downloadRequiresSuspension: Boolean = false

internal actual fun platformEnvVar(name: String): String? = System.getenv(name)

internal actual fun platformSystemProperty(name: String): String? = System.getProperty(name)

internal actual suspend fun httpRequest(
	url: String,
	method: String,
	headers: Map<String, String>,
	body: ByteArray?,
): HttpResponse {
	val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
	connection.connectTimeout = 10_000
	connection.readTimeout = 60_000
	connection.requestMethod = method
	headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

	if (body != null) {
		connection.doOutput = true
		connection.setRequestProperty("Content-Length", body.size.toString())
		connection.outputStream.use { it.write(body) }
	}

	connection.connect()
	val statusCode = connection.responseCode
	val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
	val bytes = stream?.use { it.readBytes() } ?: ByteArray(0)
	return HttpResponse(statusCode, bytes)
}

private val jvmCacheDir: Path by lazy {
	val os = (platformSystemProperty("os.name") ?: "").lowercase()
	val userHome = platformSystemProperty("user.home") ?: "."
	val platformCacheBase = when {
		os.contains("win") -> platformEnvVar("LOCALAPPDATA") ?: "$userHome/AppData/Local"
		os.contains("mac") -> "$userHome/Library/Caches"
		else -> platformEnvVar("XDG_CACHE_HOME") ?: "$userHome/.cache"
	}

	val base = platformEnvVar("KORE_CACHE_HOME") ?: platformSystemProperty("kore.cache.home") ?: "$platformCacheBase/kore"
	Path(base, "datapacks").also { it.makeDirectories() }
}

internal actual suspend fun cacheDir(): Path = jvmCacheDir

internal actual suspend fun cacheReadFile(path: Path): ByteArray? =
	if (path.exists()) path.toSource().buffered().readByteArray() else null

internal actual suspend fun cacheWriteFile(path: Path, content: ByteArray) {
	path.ensureParents()
	path.write(content)
}

internal actual suspend fun cacheFileExists(path: Path): Boolean = path.exists()
