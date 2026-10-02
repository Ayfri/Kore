package io.github.ayfri.kore.website.components.playground

import com.varabyte.kobweb.core.AppGlobals
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.fetch.Response
import kotlin.js.Promise

/**
 * Successful compiles by buffer, kept across visits: undo, a reload restoring the draft or a share link opened again
 * never reach the backend.
 *
 * An entry is the backend's payload without the library chunk texts, which [ChunkStore] holds, so a few kB: the entry
 * chunk and the diagnostics. A library chunk gone from [ChunkStore] makes the entry a miss. Keys hash the buffer with
 * the harness, the backend URL and the Kore and Minecraft versions, so a new release never reuses an old compile.
 */
object CompileMemo {
	private const val CACHE_NAME = "kore-playground-compiles-v1"
	private const val MAX_ENTRIES = 64

	private val payloads = LinkedHashMap<String, String>()
	private val scope = MainScope()

	private suspend fun cache(): dynamic = runCatching {
		(window.asDynamic().caches.open(CACHE_NAME) as Promise<dynamic>).await()
	}.getOrNull()

	private suspend fun keyOf(code: String): String {
		val source = listOf(AppGlobals["projectVersion"], AppGlobals["minecraftVersion"], playgroundApiUrl, PLAYGROUND_HARNESS, code)
		val bytes = js("new TextEncoder()").encode(source.joinToString("\u0000"))
		val digest = Uint8Array((window.asDynamic().crypto.subtle.digest("SHA-256", bytes) as Promise<ArrayBuffer>).await())
		return "/__kore-playground-compiles/" + (0 until digest.length).joinToString("") { digest[it].toUByte().toString(16).padStart(2, '0') }
	}

	suspend fun get(code: String): CompileResult? {
		val payload = payloads[code] ?: runCatching {
			val response = (cache().match(keyOf(code)) as Promise<Response?>).await() ?: return null
			response.text().await()
		}.getOrNull() ?: return null

		remember(code, payload)

		return try {
			resultOf(JSON.parse(payload), 0).copy(cached = true)
		} catch (_: MissingChunkException) {
			null
		}
	}

	/** Keeps a successful per-module compile, written to Cache Storage without delaying the run. */
	fun put(code: String, payload: dynamic) {
		val files = payload.jsFiles ?: return
		for (index in 0 until (files.length as Int)) if (files[index].name != ENTRY_CHUNK_NAME) files[index].text = null

		val text = JSON.stringify(payload)
		remember(code, text)

		scope.launch {
			runCatching {
				val cache = cache()
				(cache.put(keyOf(code), Response(text)) as Promise<Unit>).await()

				val requests = (cache.keys() as Promise<Array<dynamic>>).await()
				requests.take((requests.size - MAX_ENTRIES).coerceAtLeast(0)).forEach { (cache.delete(it) as Promise<Boolean>).await() }
			}
		}
	}

	private fun remember(code: String, payload: String) {
		payloads.remove(code)
		payloads[code] = payload
		while (payloads.size > MAX_ENTRIES) payloads.remove(payloads.keys.first())
	}
}
