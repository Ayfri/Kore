package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Response
import kotlin.js.Promise

/**
 * Compiled chunks by content hash, kept across compiles and across visits.
 *
 * Most chunks come out of the backend byte-identical from one compile to the next, `Kore-kore.js` (14 MB) included
 * as long as the snippet sticks to what the backend's anchor module uses, so the page tells it which hashes it holds
 * and only downloads the rest. Texts live in memory for the page and in Cache Storage between visits, which has room
 * for them where `localStorage` stops at 5 MB. Every Cache Storage call is allowed to fail: the store then only
 * remembers the current page, and the backend sends whatever it is not told about.
 */
object ChunkStore {
	private const val CACHE_NAME = "kore-playground-chunks-v1"

	/** Twelve library chunks plus a few `Kore-kore.js` variants, the only big one. Entry chunks live in [CompileMemo]. */
	private const val MAX_PERSISTED = 16

	private const val MAX_IN_MEMORY = 16

	private val texts = LinkedHashMap<String, String>()
	private var persisted: MutableSet<String>? = null

	private fun keyOf(hash: String) = "/__kore-playground-chunks/$hash"

	private suspend fun cache(): dynamic = runCatching {
		(window.asDynamic().caches.open(CACHE_NAME) as Promise<dynamic>).await()
	}.getOrNull()

	private suspend fun persistedHashes(): MutableSet<String> = persisted ?: runCatching {
		val requests = (cache().keys() as Promise<Array<dynamic>>).await()
		requests.mapTo(mutableSetOf()) { (it.url as String).substringAfterLast('/') }
	}.getOrDefault(mutableSetOf()).also { persisted = it }

	/** Every hash a compile does not need to receive again. */
	suspend fun knownHashes(): Set<String> = texts.keys + persistedHashes()

	suspend fun get(hash: String): String? {
		texts.remove(hash)?.let { return it.also { texts[hash] = it } }

		val text = runCatching {
			val response = (cache().match(keyOf(hash)) as Promise<Response?>).await() ?: return null
			response.text().await()
		}.getOrNull() ?: return null

		remember(hash, text)
		return text
	}

	/** Stores [hash] and evicts the oldest stored chunks, never one of [inUse], the chunks of the compile it comes from. */
	suspend fun put(hash: String, text: String, inUse: Set<String>) {
		remember(hash, text)
		val hashes = persistedHashes()
		if (hash in hashes) return

		runCatching {
			val cache = cache()
			(cache.put(keyOf(hash), Response(text)) as Promise<Unit>).await()
			hashes += hash

			// Cache Storage keeps insertion order and a reused chunk is never written again, so the oldest unused go first.
			val requests = (cache.keys() as Promise<Array<dynamic>>).await()
			val stale = requests.filter { (it.url as String).substringAfterLast('/') !in inUse }
			stale.take((requests.size - MAX_PERSISTED).coerceAtLeast(0)).forEach { request ->
				(cache.delete(request) as Promise<Boolean>).await()
				hashes -= (request.url as String).substringAfterLast('/')
			}
		}
	}

	private fun remember(hash: String, text: String) {
		texts.remove(hash)
		texts[hash] = text
		while (texts.size > MAX_IN_MEMORY) texts.remove(texts.keys.first())
	}
}
