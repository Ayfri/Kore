package io.github.ayfri.kore.bindings.download

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/** Result of a [httpRequest] call: the raw response body plus its HTTP status code. */
internal data class HttpResponse(val statusCode: Int, val bytes: ByteArray)

/**
 * The only network primitive datapack downloading needs, implemented per platform: `HttpURLConnection` on the
 * JVM, `fetch` in the browser and on Node.js (both `bindings` JS targets compile to the same source set and
 * branch at runtime, mirroring `kore`'s `PlatformIOJs.kt`).
 */
internal expect suspend fun httpRequest(
	url: String,
	method: String = "GET",
	headers: Map<String, String> = emptyMap(),
	body: ByteArray? = null,
): HttpResponse

/** Reads an environment variable. Always `null` in the browser, where there is no such concept. */
internal expect fun platformEnvVar(name: String): String?

/** Reads a JVM system property. Always `null` on JS (Node and browser alike). */
internal expect fun platformSystemProperty(name: String): String?

/** True only when downloading must go through a real async runtime (currently: the browser's `fetch`/OPFS). */
internal expect val downloadRequiresSuspension: Boolean

/**
 * Runs a `suspend` block that is known not to truly suspend (JVM/Node.js network + disk I/O never does) to
 * completion, synchronously, with zero coroutine library dependency - keeps the public `importDatapacks {}` DSL
 * a plain blocking call on those platforms. Throws if the block actually suspends across a real async boundary
 * (e.g. this is called in the browser, where [downloadRequiresSuspension] is `true`).
 */
internal fun <T> runDownloadBlocking(block: suspend () -> T): T {
	check(!downloadRequiresSuspension) {
		"Downloading requires a real asynchronous runtime on this platform; use the suspend entry point instead."
	}

	var result: Result<T>? = null
	block.startCoroutine(Continuation(EmptyCoroutineContext) { result = it })
	val completed =
		result ?: error("Suspended across a real async boundary despite downloadRequiresSuspension being false.")
	return completed.getOrThrow()
}
