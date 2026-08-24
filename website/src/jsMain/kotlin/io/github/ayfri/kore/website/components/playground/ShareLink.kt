package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.js.Promise

/**
 * Snippets travel in the URL fragment, never in the query string: the fragment is not sent to the server,
 * so sharing a playground link stores nothing anywhere.
 *
 * The buffer is deflated with the native `CompressionStream`, in line with the project's zero-dependency
 * bias. Browsers without it (none current) fall back to plain base64, marked by the `~` prefix so both
 * forms keep decoding.
 */
private const val HASH_KEY = "code="
private const val UNCOMPRESSED_MARKER = '~'

private val compressionSupported = js("typeof CompressionStream !== 'undefined'") as Boolean

private fun deflate(text: String): Promise<ArrayBuffer> =
	js("new Response(new Blob([text]).stream().pipeThrough(new CompressionStream('deflate-raw'))).arrayBuffer()")
		.unsafeCast<Promise<ArrayBuffer>>()

private fun inflate(bytes: Uint8Array): Promise<String> =
	js("new Response(new Blob([bytes]).stream().pipeThrough(new DecompressionStream('deflate-raw'))).text()")
		.unsafeCast<Promise<String>>()

private fun toBase64Url(buffer: ArrayBuffer): String {
	val bytes = Uint8Array(buffer)
	val builder = StringBuilder(bytes.length)

	for (index in 0 until bytes.length) builder.append(bytes[index].toInt().and(0xFF).toChar())

	return window.btoa(builder.toString()).replace('+', '-').replace('/', '_').trimEnd('=')
}

private fun fromBase64Url(value: String): Uint8Array {
	val padded = value.replace('-', '+').replace('_', '/').padEnd((value.length + 3) / 4 * 4, '=')
	val binary = window.atob(padded)
	val bytes = Uint8Array(binary.length)

	binary.forEachIndexed { index, char -> bytes.asDynamic()[index] = char.code and 0xFF }

	return bytes
}

/** Encodes [code] into the shareable fragment of the current URL. */
suspend fun shareUrl(code: String): String {
	val payload = when {
		compressionSupported -> toBase64Url(deflate(code).await())
		else -> UNCOMPRESSED_MARKER + window.btoa(js("unescape")(js("encodeURIComponent")(code)) as String)
	}

	val url = window.location.href.substringBefore('#')
	return "$url#$HASH_KEY$payload"
}

/** Reads back a snippet shared through the URL fragment, or `null` when the fragment holds none. */
suspend fun sharedCode(): String? {
	val hash = window.location.hash.removePrefix("#")
	if (!hash.startsWith(HASH_KEY)) return null

	val payload = hash.removePrefix(HASH_KEY)
	if (payload.isEmpty()) return null

	return runCatching {
		when (payload.first()) {
			UNCOMPRESSED_MARKER -> js("decodeURIComponent")(js("escape")(window.atob(payload.drop(1)))) as String
			else -> inflate(fromBase64Url(payload)).await()
		}
	}.getOrNull()
}
