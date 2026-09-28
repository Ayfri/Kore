package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

/** One entry of the generated datapack, exactly as `exportAsStrings()` produced it. */
data class GeneratedFile(
	val path: String,
	val content: String,
) {
	val name = path.substringAfterLast('/')
	val directory = path.substringBeforeLast('/', "")
	val extension = name.substringAfterLast('.', "")
}

sealed interface RunResult {
	data class Success(val files: List<GeneratedFile>, val durationMs: Int) : RunResult
	data class Failure(val message: String) : RunResult
}

/**
 * `load` evaluates the library chunks one at a time, announcing each before it runs; `run` evaluates an entry chunk
 * and reads the globals the harness left. `importScripts` is synchronous and evaluates in the worker's global scope,
 * which is exactly what concatenating the chunks would do, except the loop can report between modules.
 */
private const val WORKER_SCRIPT = """
self.onmessage = function (event) {
	var data = event.data;

	if (data.kind === 'load') {
		for (var index = 0; index < data.chunks.length; index++) {
			self.postMessage({ id: data.id, loading: { index: index, total: data.chunks.length, name: data.chunks[index].name } });

			try {
				importScripts(data.chunks[index].url);
			} catch (error) {
				self.postMessage({ id: data.id, error: 'Loading ' + data.chunks[index].name + ' failed: ' + String(error) });
				return;
			}
		}

		self.postMessage({ id: data.id, done: true });
		return;
	}

	globalThis.__koreFiles = null;
	globalThis.__koreError = null;

	try {
		importScripts(data.entry.url);
		self.postMessage({ id: data.id, done: true, files: globalThis.__koreFiles, error: globalThis.__koreError });
	} catch (error) {
		self.postMessage({ id: data.id, error: 'Running ' + data.entry.name + ' failed: ' + String(error) });
	}
};
"""

private fun blobUrl(text: String) = URL.createObjectURL(Blob(arrayOf(text), BlobPropertyBag(type = "text/javascript")))

private fun message(id: Int, kind: String, block: (dynamic) -> Unit): dynamic {
	val message = js("({})")
	message.id = id
	message.kind = kind
	block(message)
	return message
}

private fun chunkRef(name: String, url: String): dynamic {
	val chunk = js("({})")
	chunk.name = name
	chunk.url = url
	return chunk
}

/**
 * Runs compiled packs in a Web Worker kept warm between runs.
 *
 * The chunks are UMD with a `globalThis` fallback, come in evaluation order, and the last one runs the harness
 * `main()` when evaluated: loading them in order is a complete program, no bundler involved. The library chunks are
 * 16 MB of JavaScript and, with the backend's anchor module, byte-identical from one compile to the next, so they are
 * evaluated once per worker and each run only evaluates the snippet's own chunk: a few milliseconds instead of the
 * ~1 s of parsing 16 MB again. A pack generated this way is identical to one from a fresh worker, checked across the
 * examples; Kore keeps no global state between two `dataPack` calls beyond the public `Configuration.DEFAULT`.
 *
 * A worker rather than an iframe: the run is isolated from the page, the UI stays responsive while a heavy pack
 * builds, and Kore needs no DOM. The watchdog terminates the worker when a step exceeds its budget, the snippet's own
 * code being the only thing that can loop forever, and a new set of library hashes, or chunks without hashes from an
 * older backend, start a fresh one.
 */
object PackRunner {
	private val mutex = Mutex()
	private val workerUrl by lazy { blobUrl(WORKER_SCRIPT) }
	private val pending = mutableMapOf<Int, (dynamic) -> Unit>()

	private var worker: Worker? = null
	private var librariesKey: String? = null
	private var nextId = 0

	/** Loads [libraries] ahead of a run, typically while their compile is still on the backend. */
	suspend fun prewarm(libraries: List<CompiledChunk>) = mutex.withLock { ensureLoaded(libraries, 30_000) {} }

	suspend fun run(chunks: List<CompiledChunk>, timeoutMs: Int = 10_000, onProgress: (CompileProgress) -> Unit = {}) = mutex.withLock {
		val startedAt = window.performance.now()
		val entry = chunks.lastOrNull() ?: return@withLock RunResult.Failure("The compile returned no JavaScript.")
		ensureLoaded(chunks.dropLast(1), timeoutMs, onProgress)?.let { return@withLock RunResult.Failure(it) }

		val url = blobUrl(entry.text)
		val outcome = request("run", timeoutMs, { it.entry = chunkRef(entry.name, url) })
		URL.revokeObjectURL(url)

		val files = outcome.data?.files
		when {
			outcome.error != null -> RunResult.Failure(outcome.error).also { if (outcome.timedOut) reset() }
			outcome.data?.error != null -> RunResult.Failure(outcome.data.error as String)
			files == null || files == undefined -> RunResult.Failure("The snippet produced no datapack. Does it define `fun playground()`?")

			else -> RunResult.Success(
				files = js("Object.keys")(files).unsafeCast<Array<String>>()
					.map { GeneratedFile(it, files[it] as String) }
					.sortedWith(compareBy({ it.path != "pack.mcmeta" }, { it.path })),
				durationMs = (window.performance.now() - startedAt).toInt(),
			)
		}
	}

	/** Makes the worker hold exactly [libraries], starting a fresh one unless it already does. The error, if any. */
	private suspend fun ensureLoaded(libraries: List<CompiledChunk>, timeoutMs: Int, onProgress: (CompileProgress) -> Unit): String? {
		// A single bundle, or chunks from a backend that sends no hashes, never reuses a worker.
		val key = libraries.takeIf { list -> list.isNotEmpty() && list.all { it.hash != null } }?.joinToString(",") { it.hash!! }
		if (worker != null && key != null && key == librariesKey) return null

		reset()
		worker = Worker(workerUrl).also { it.onmessage = { event: MessageEvent -> dispatch(event.data.asDynamic()) } }

		val urls = libraries.map { blobUrl(it.text) }
		val loaded = request("load", timeoutMs, { it.chunks = libraries.mapIndexed { index, chunk -> chunkRef(chunk.name, urls[index]) }.toTypedArray() }) { loading ->
			val index = (loading.index as? Int) ?: 0
			val total = (loading.total as? Int) ?: libraries.size
			onProgress(CompileProgress("Running", "Loading ${loading.name as? String ?: "a module"} (${index + 1}/$total)", (index + 1).toDouble() / total))
		}
		urls.forEach(URL::revokeObjectURL)

		if (loaded.error != null) reset() else librariesKey = key
		return loaded.error
	}

	private class Outcome(val data: dynamic, val error: String?, val timedOut: Boolean = false)

	/** Posts one request and waits for its answer, re-arming the watchdog on every progress message. */
	private suspend fun request(kind: String, timeoutMs: Int, fill: (dynamic) -> Unit, onLoading: (dynamic) -> Unit = {}): Outcome {
		val id = nextId++
		val answer = CompletableDeferred<Outcome>()
		var watchdog = 0

		fun arm() {
			window.clearTimeout(watchdog)
			watchdog = window.setTimeout({
				answer.complete(Outcome(null, "The snippet did not finish within ${timeoutMs / 1000}s and was stopped.", timedOut = true))
			}, timeoutMs)
		}

		pending[id] = { data ->
			val loading = data.loading
			if (loading != null && loading != undefined) {
				arm()
				onLoading(loading)
			} else {
				answer.complete(Outcome(data, (data.error as? String)?.takeIf { data.done != true }))
			}
		}

		worker!!.onerror = { event -> answer.complete(Outcome(null, event.asDynamic().message as? String ?: "The snippet crashed while running.", timedOut = true)) }
		arm()
		worker!!.postMessage(message(id, kind, fill))

		return try {
			answer.await()
		} finally {
			window.clearTimeout(watchdog)
			pending.remove(id)
		}
	}

	private fun dispatch(data: dynamic) {
		pending[(data.id as? Int) ?: return]?.invoke(data)
	}

	private fun reset() {
		worker?.terminate()
		worker = null
		librariesKey = null
		pending.clear()
	}
}
