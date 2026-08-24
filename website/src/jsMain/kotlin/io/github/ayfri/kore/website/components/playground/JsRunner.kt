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
 * Reads the globals the harness leaves behind and hands them back to the page, then stops the worker.
 * Appended to the compiled bundle, so it runs right after the generated `main`.
 */
private const val WORKER_BOOTSTRAP = """
;(function () {
	try {
		self.postMessage({ files: globalThis.__koreFiles || null, error: globalThis.__koreError || null });
	} catch (error) {
		self.postMessage({ files: null, error: String(error) });
	}
})();
"""

/**
 * Runs the compiled program in a Web Worker and collects the datapack it produced.
 *
 * [chunks] are the emitted modules **in evaluation order**, the order the backend topologically sorted them
 * into - never a hardcoded one, since it is neither alphabetical nor layered. Each is UMD with a
 * `globalThis` fallback, and a worker has neither `define` nor `exports`, so they take that fallback and
 * register themselves on `globalThis`: concatenating them is a complete program, no bundler involved. The
 * last one runs `mainWrapper()` on evaluation, which is what leaves the harness result behind.
 *
 * A worker rather than an iframe: the run is isolated, the UI thread stays responsive while a heavy pack
 * builds, and Kore needs no DOM. [timeoutMs] guards against a snippet that never returns - the worker is
 * terminated either way, so nothing survives a run.
 */
suspend fun runCompiledPack(chunks: List<String>, timeoutMs: Int = 10_000): RunResult =
	suspendCancellableCoroutine { continuation ->
		// A newline and a semicolon between modules: a chunk that ends mid-line must not glue onto the next.
		val parts = (chunks + WORKER_BOOTSTRAP).flatMap { listOf<Any>(it, "\n;\n") }.toTypedArray()
		val blob = Blob(parts, BlobPropertyBag(type = "text/javascript"))
		val blobUrl = URL.createObjectURL(blob)
		val worker = Worker(blobUrl)
		val startedAt = window.performance.now()
		var settled = false

		fun finish(result: RunResult) {
			if (settled) return
			settled = true
			worker.terminate()
			URL.revokeObjectURL(blobUrl)
			continuation.resume(result)
		}

		val watchdog = window.setTimeout({
			finish(RunResult.Failure("The snippet did not finish within ${timeoutMs / 1000}s and was stopped."))
		}, timeoutMs)

		worker.onmessage = { event: MessageEvent ->
			window.clearTimeout(watchdog)
			val data = event.data.asDynamic()
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

		worker.onerror = { event ->
			window.clearTimeout(watchdog)
			finish(RunResult.Failure(event.asDynamic().message as? String ?: "The snippet crashed while running."))
		}

		continuation.invokeOnCancellation {
			window.clearTimeout(watchdog)
			if (!settled) {
				settled = true
				worker.terminate()
				URL.revokeObjectURL(blobUrl)
			}
		}
	}
