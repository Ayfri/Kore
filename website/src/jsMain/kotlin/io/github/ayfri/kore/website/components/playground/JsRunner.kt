package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.window
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.coroutines.resume

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
 * Loads the chunks one at a time, announcing each before it runs, then reads the globals the harness left.
 *
 * `importScripts` is synchronous and evaluates in the worker's global scope, which is exactly what
 * concatenating the chunks used to do - except the loop can report between modules. That matters because
 * `Kore-kore.js` alone is 13.9 MB and takes most of the run.
 */
private const val WORKER_LOADER = """
self.onmessage = function (event) {
	var chunks = event.data.chunks;

	for (var index = 0; index < chunks.length; index++) {
		self.postMessage({ loading: { index: index, total: chunks.length, name: chunks[index].name } });

		try {
			importScripts(chunks[index].url);
		} catch (error) {
			self.postMessage({ files: null, error: 'Loading ' + chunks[index].name + ' failed: ' + String(error) });
			return;
		}
	}

	try {
		self.postMessage({ files: globalThis.__koreFiles || null, error: globalThis.__koreError || null });
	} catch (error) {
		self.postMessage({ files: null, error: String(error) });
	}
};
"""

/**
 * Runs the compiled program in a Web Worker and collects the datapack it produced.
 *
 * [chunks] are the emitted modules **in evaluation order**, the order the backend topologically sorted them
 * into - never a hardcoded one, since it is neither alphabetical nor layered. Each is UMD with a
 * `globalThis` fallback, and a worker has neither `define` nor `exports`, so they take that fallback and
 * register themselves on `globalThis`: loading them in order is a complete program, no bundler involved.
 * The last one runs `mainWrapper()` on evaluation, which is what leaves the harness result behind.
 *
 * A worker rather than an iframe: the run is isolated, the UI thread stays responsive while a heavy pack
 * builds, and Kore needs no DOM. [timeoutMs] guards against a snippet that never returns - the worker is
 * terminated either way, so nothing survives a run.
 */
suspend fun runCompiledPack(
	chunks: List<CompiledChunk>,
	timeoutMs: Int = 10_000,
	onProgress: (CompileProgress) -> Unit = {},
): RunResult = suspendCancellableCoroutine { continuation ->
	val chunkUrls = chunks.map { URL.createObjectURL(Blob(arrayOf(it.text), BlobPropertyBag(type = "text/javascript"))) }
	val loaderUrl = URL.createObjectURL(Blob(arrayOf(WORKER_LOADER), BlobPropertyBag(type = "text/javascript")))
	val worker = Worker(loaderUrl)
	val startedAt = window.performance.now()
	var settled = false

	fun release() {
		worker.terminate()
		chunkUrls.forEach { URL.revokeObjectURL(it) }
		URL.revokeObjectURL(loaderUrl)
	}

	fun finish(result: RunResult) {
		if (settled) return
		settled = true
		release()
		continuation.resume(result)
	}

	// The watchdog is armed per step rather than once: loading 16 MB of modules is slow but bounded, and it
	// is the snippet's own code - the last chunk, and only it - that can loop forever.
	var watchdog = 0

	fun arm() {
		window.clearTimeout(watchdog)

		watchdog = window.setTimeout({
			finish(RunResult.Failure("The snippet did not finish within ${timeoutMs / 1000}s and was stopped."))
		}, timeoutMs)
	}

	arm()

	worker.onmessage = { event: MessageEvent ->
		val data = event.data.asDynamic()
		val loading = data.loading

		if (loading != null && loading != undefined) {
			arm()
			val index = (loading.index as? Int) ?: 0
			val total = (loading.total as? Int) ?: chunks.size

			onProgress(
				CompileProgress(
					label = "Running",
					detail = "Loading ${loading.name as? String ?: "a module"} (${index + 1}/$total)",
					fraction = (index + 1).toDouble() / total,
				)
			)
		} else {
			window.clearTimeout(watchdog)
			val error = data.error as? String

			when {
				error != null -> finish(RunResult.Failure(error))
				data.files == null -> finish(RunResult.Failure("The snippet produced no datapack. Does it define `fun playground()`?"))

				else -> {
					val files = js("Object.keys")(data.files).unsafeCast<Array<String>>()
						.map { GeneratedFile(it, data.files[it] as String) }
						.sortedWith(compareBy({ it.path != "pack.mcmeta" }, { it.path }))

					finish(RunResult.Success(files, (window.performance.now() - startedAt).toInt()))
				}
			}
		}
	}

	worker.onerror = { event ->
		window.clearTimeout(watchdog)
		finish(RunResult.Failure(event.asDynamic().message as? String ?: "The snippet crashed while running."))
	}

	continuation.invokeOnCancellation {
		window.clearTimeout(watchdog)

		if (!settled) {
			settled = true
			release()
		}
	}

	val message = js("({})")
	message.chunks = chunks.mapIndexed { index, chunk ->
		val entry = js("({})")
		entry.name = chunk.name
		entry.url = chunkUrls[index]
		entry
	}.toTypedArray()

	worker.postMessage(message)
}
