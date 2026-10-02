package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.core.AppGlobals
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Promise

/** Name of the file the user edits. Diagnostics are reported against it, so it must match the request. */
const val USER_FILE_NAME = "main.kt"

/** Name of the generated file wrapping the user snippet, never shown in the editor. */
const val HARNESS_FILE_NAME = "__harness.kt"

/** What the backend names the entry chunk, the one carrying the harness `main`. */
const val ENTRY_CHUNK_NAME = "playground.js"

private const val NEWLINE = '\n'

/**
 * Compile backend, injected as a Kobweb global so dev and prod point at different deployments.
 *
 * `null` when no backend is configured: the UI then explains that compiling is unavailable instead of
 * firing requests at nothing.
 */
val playgroundApiUrl: String?
	get() = AppGlobals["playgroundApiUrl"]?.takeIf { it.isNotBlank() }?.trimEnd('/')

enum class DiagnosticSeverity {
	ERROR,
	INFO,
	WARNING;

	companion object {
		fun from(value: String?) = when (value?.uppercase()) {
			"ERROR" -> ERROR
			"WARNING" -> WARNING
			else -> INFO
		}
	}
}

/** A compiler message, already translated to editor coordinates: the server counts from 0, Monaco from 1. */
data class PlaygroundDiagnostic(
	val file: String,
	val severity: DiagnosticSeverity,
	val message: String,
	val startLine: Int,
	val startColumn: Int,
	val endLine: Int,
	val endColumn: Int,
)

/**
 * One emitted JS module, UMD with a `globalThis` fallback.
 *
 * Under the IR build cache the backend emits a module per library instead of a single bundle, and returns
 * them already topologically sorted, so evaluating [CompileResult.chunks] top to bottom needs no bundler.
 */
data class CompiledChunk(
	val name: String,
	val text: String,
	val hash: String? = null,
)

data class CompileResult(
	val jsCode: String?,
	val chunks: List<CompiledChunk>,
	val diagnostics: List<PlaygroundDiagnostic>,
	val exception: String?,
	val durationMs: Int,
	val cached: Boolean = false,
) {
	val errors get() = diagnostics.filter { it.severity == DiagnosticSeverity.ERROR }
	val succeeded get() = exception == null && errors.isEmpty() && !jsCode.isNullOrBlank()

	/**
	 * Everything that has to be evaluated, in order, for the harness to run.
	 *
	 * [jsCode] is the entry chunk in both modes, so it is the whole program when the backend answers with a
	 * single bundle and the last element of [chunks] when it answers per module.
	 */
	val evaluationOrder
		get() = chunks.takeIf { it.isNotEmpty() } ?: listOfNotNull(jsCode).map { CompiledChunk(ENTRY_CHUNK_NAME, it) }
}

/**
 * A step of the round-trip, as it happens, ready to be shown as-is.
 *
 * [fraction] is filled only where real progress is known - the download, and the chunk-by-chunk evaluation
 * in the worker. A compile has no such measure, so the page falls back to elapsed time against what past
 * compiles took.
 */
data class CompileProgress(
	val label: String,
	val detail: String? = null,
	val fraction: Double? = null,
)

/** The backend's compile queue is full. A compile started on idle drops it silently, a Run reports it. */
class CompileBusyException(message: String) : Exception(message)

/** One compile, shared by everyone asking for the same buffer while it runs: a compile started on idle and the Run after it. */
class CompileJob(val code: String) {
	/** What the backend last reported, observable so the page can show the wait of a compile it joined rather than started. */
	var progress by mutableStateOf(CompileProgress("Compiling", "Waiting for the compile backend."))
		internal set

	internal val result = CompletableDeferred<CompileResult>()
}

/**
 * The page's single compile lane.
 *
 * The backend compiles one snippet at a time for everyone, so a tab never stacks a second request behind its own: a
 * compile for another buffer waits for the running one, and one for the same buffer joins it, which is what makes the
 * Run after an idle compile free. Jobs live in their own scope, so a caller giving up - a keystroke cancelling the idle
 * compile - never abandons a half-read response, and the finished result still lands in the cache.
 */
object PlaygroundCompiler {
	/** Enough for an undo history's worth of buffers, each a short list of diagnostics. */
	private const val TYPE_CHECK_CACHE_SIZE = 32

	private val scope = MainScope()
	private val typeChecks = LinkedHashMap<String, List<PlaygroundDiagnostic>>()

	var running by mutableStateOf<CompileJob?>(null)
		private set

	suspend fun compile(code: String): CompileResult {
		while (true) {
			CompileMemo.get(code)?.let { return it }
			val job = running ?: break
			if (job.code == code) return job.result.await()
			job.result.join()
		}

		val job = CompileJob(code)
		running = job

		scope.launch {
			runCatching {
				try {
					stream(job, ChunkStore.knownHashes())
				} catch (_: MissingChunkException) {
					stream(job, emptySet())
				}
			}
				.onSuccess { job.result.complete(it) }
				.onFailure { job.result.completeExceptionally(it) }

			running = null
		}

		return job.result.await()
	}

	/** Whether the last type-check of exactly [code] still held reported an error. */
	fun failedTypeCheck(code: String) = typeChecks[code]?.any { it.severity == DiagnosticSeverity.ERROR } == true

	fun persist(hash: String, text: String, inUse: Set<String>) {
		scope.launch { ChunkStore.put(hash, text, inUse) }
	}

	/**
	 * Diagnostics for [code] alone, without producing any JavaScript, from memory when this buffer was already checked.
	 *
	 * `POST /api/compiler/highlight` type-checks the snippet on the **JVM** and answers in well under a second,
	 * against many seconds for the JS compile, which is what makes squiggles-while-typing affordable. The
	 * harness is not sent, so every message describes [USER_FILE_NAME] at its real line.
	 *
	 * The two targets can disagree: anything JVM-only passes here and still fails the real compile, so
	 * diagnostics coming back from a compile always win.
	 */
	suspend fun typeCheck(code: String): List<PlaygroundDiagnostic> {
		val diagnostics = typeChecks.remove(code) ?: highlight(code)
		typeChecks[code] = diagnostics
		while (typeChecks.size > TYPE_CHECK_CACHE_SIZE) typeChecks.remove(typeChecks.keys.first())
		return diagnostics
	}
}

private fun projectFile(name: String, text: String): dynamic {
	val file = js("({})")
	file.name = name
	file.text = text
	return file
}

private suspend fun post(url: String, body: dynamic): Response = window.fetch(
	url,
	RequestInit(
		method = "POST",
		headers = js("({ 'Content-Type': 'application/json' })"),
		body = JSON.stringify(body),
	)
).await()

private fun compileBody(code: String): dynamic {
	val body = js("({})")
	body.args = ""
	body.files = arrayOf(projectFile(USER_FILE_NAME, code), projectFile(HARNESS_FILE_NAME, PLAYGROUND_HARNESS))
	return body
}

/** Thrown when the backend left out a chunk the store said it held but no longer finds, so the compile is asked again. */
internal class MissingChunkException : Exception()

/**
 * The chunks of a response, texts left out by the backend filled back in from [ChunkStore].
 *
 * New library texts are stored without waiting: writing 14 MB to Cache Storage must not delay the run. The entry chunk
 * changes with every buffer, so it stays in [CompileMemo] and never pushes a library chunk out of [ChunkStore].
 */
private suspend fun resolveChunks(jsFiles: dynamic): List<CompiledChunk> {
	val count = (jsFiles?.length as? Int) ?: return emptyList()
	val hashes = (0 until count).mapNotNullTo(mutableSetOf()) { jsFiles[it].hash as? String }

	return (0 until count).map { index ->
		val chunk = jsFiles[index]
		val name = chunk.name as? String ?: "chunk-$index.js"
		val hash = chunk.hash as? String
		val text = chunk.text as? String

		when {
			text != null -> text.also { if (hash != null && name != ENTRY_CHUNK_NAME) PlaygroundCompiler.persist(hash, it, hashes) }
			hash != null -> ChunkStore.get(hash) ?: throw MissingChunkException()
			else -> throw MissingChunkException()
		}.let { CompiledChunk(name, it, hash) }
	}
}

private fun parseDiagnostics(errors: dynamic): List<PlaygroundDiagnostic> {
	if (errors == null || errors == undefined) return emptyList()

	val fileNames = js("Object.keys")(errors).unsafeCast<Array<String>>()

	return fileNames.flatMap { fileName ->
		val fileErrors = errors[fileName]
		val count = (fileErrors?.length as? Int) ?: 0

		(0 until count).map { index ->
			val error = fileErrors[index]
			val start = error.interval?.start
			val end = error.interval?.end

			PlaygroundDiagnostic(
				file = fileName,
				severity = DiagnosticSeverity.from(error.severity as? String),
				message = error.message as? String ?: "Unknown compiler error",
				startLine = ((start?.line as? Int) ?: 0) + 1,
				startColumn = ((start?.ch as? Int) ?: 0) + 1,
				endLine = ((end?.line as? Int) ?: (start?.line as? Int) ?: 0) + 1,
				endColumn = ((end?.ch as? Int) ?: ((start?.ch as? Int) ?: 0)) + 1,
			)
		}
	}
}

private suspend fun highlight(code: String): List<PlaygroundDiagnostic> {
	val api = playgroundApiUrl ?: error("No compile backend is configured for this deployment.")

	val body = js("({})")
	body.args = ""
	body.confType = "java"
	body.files = arrayOf(projectFile(USER_FILE_NAME, code))

	val response = post("$api/api/compiler/highlight", body)
	if (!response.ok) error("Diagnostics backend answered ${response.status} ${response.statusText}.")

	// The endpoint returns the per-file map directly, the same shape the compile response nests under `errors`.
	return parseDiagnostics(response.json().await().asDynamic())
}

private fun humanBytes(bytes: Int) = when {
	bytes >= 1024 * 1024 -> "${(bytes / 104_857.6).toInt() / 10.0} MB"
	else -> "${bytes / 1024} kB"
}

private fun seconds(ms: Int) = "${ms / 100 / 10.0}s"

/** Turns one backend progress line into something worth reading. */
private fun progressOf(event: dynamic): CompileProgress? = when (event.event as? String) {
	"queued" -> {
		val ahead = (event.ahead as? Int) ?: 1
		CompileProgress("Queued", "$ahead compile${if (ahead > 1) "s" else ""} ahead of yours on the backend.")
	}

	"started" -> CompileProgress("Compiling", "The snippet reached the compiler.")

	"phase" -> when (event.name as? String) {
		"klib" -> CompileProgress("Compiling Kotlin", "Type-checking the snippet against Kore and building its klib.")

		"js" -> CompileProgress(
			label = "Linking JavaScript",
			detail = "Kotlin compiled in ${seconds((event.previousMs as? Int) ?: 0)}. Linking Kore's modules is the long half.",
		)

		else -> CompileProgress("Collecting output", "JavaScript linked in ${seconds((event.previousMs as? Int) ?: 0)}.")
	}

	"output" -> {
		val reused = (event.reused as? Int) ?: 0
		CompileProgress(
			label = "Downloading",
			detail = "${(event.chunks as? Int) ?: 0} modules, ${humanBytes((event.bytes as? Int) ?: 0)} of JavaScript" +
				if (reused > 0) ", $reused reused from earlier runs." else ".",
			fraction = 0.0,
		)
	}

	else -> null
}

/** The query telling the backend which chunk texts it can leave out. */
private fun knownQuery(known: Set<String>) = if (known.isEmpty()) "" else "?known=${known.joinToString(",")}"

/**
 * Compiles [job]'s buffer while reporting what the backend is doing, [known] listing the chunk hashes already held.
 *
 * `POST /api/compiler/translate/js/stream` answers with newline-delimited JSON: progress lines first, the usual
 * compile result last. A backend without the endpoint, or a browser without streaming bodies, falls back to
 * [compilePlain].
 *
 * Each network chunk is scanned once for line ends and a line is joined once: the result line alone is ~17 MB, and
 * appending to one string then searching it from the start on every read made the download quadratic.
 */
private suspend fun stream(job: CompileJob, known: Set<String>): CompileResult {
	val api = playgroundApiUrl ?: error("No compile backend is configured for this deployment.")
	val startedAt = window.performance.now()
	val response = post("$api/api/compiler/translate/js/stream${knownQuery(known)}", compileBody(job.code))

	val stream = response.asDynamic().body
	if (!response.ok || stream == null || stream == undefined) return compilePlain(job.code, known)

	val reader = stream.getReader()
	val decoder = js("new TextDecoder()")
	val line = mutableListOf<String>()
	var downloaded = 0
	var total = 0

	while (true) {
		val step = (reader.read() as Promise<dynamic>).await()
		if (step.done as Boolean) break

		downloaded += (step.value.length as? Int) ?: 0
		val text = decoder.decode(step.value, js("({ stream: true })")) as String
		var start = 0

		while (true) {
			val end = text.indexOf(NEWLINE, start)
			if (end < 0) {
				line += text.substring(start)
				break
			}

			line += text.substring(start, end)
			start = end + 1
			val event = runCatching { JSON.parse<dynamic>(line.joinToString("")) }.getOrNull()
			line.clear()
			if (event == null) continue

			when (event.event as? String) {
				"result" -> {
					runCatching { reader.cancel() }
					return memoized(job.code, event.result, (window.performance.now() - startedAt).toInt())
				}

				"busy" -> throw CompileBusyException(event.message as? String ?: "The compile queue is full, retry shortly.")
				"error" -> error(event.message as? String ?: "The compile backend failed.")
				"output" -> total = (event.bytes as? Int) ?: 0
			}

			progressOf(event)?.let { job.progress = it }
		}

		// The result is the last line and by far the largest, so everything arriving after `output` is it.
		if (total > 0) job.progress = CompileProgress(
			label = "Downloading",
			detail = "${humanBytes(downloaded)} of ${humanBytes(total)}",
			fraction = (downloaded.toDouble() / total).coerceIn(0.0, 1.0),
		)
	}

	// No result line means the backend answered something else; the plain endpoint reports that properly.
	return compilePlain(job.code, known)
}

/**
 * Compiles [code] together with the hidden harness through `POST /api/compiler/translate/js`, which answers with
 * the compiled modules plus per-file diagnostics. The two files are sent separately so line numbers reported
 * for [USER_FILE_NAME] map straight onto the editor.
 */
private suspend fun compilePlain(code: String, known: Set<String>): CompileResult {
	val api = playgroundApiUrl ?: error("No compile backend is configured for this deployment.")
	val startedAt = window.performance.now()
	val response = post("$api/api/compiler/translate/js${knownQuery(known)}", compileBody(code))

	if (response.status.toInt() == 429) throw CompileBusyException("The compile queue is full, retry shortly.")
	if (!response.ok) error("Compile backend answered ${response.status} ${response.statusText}.")

	return memoized(code, response.json().await().asDynamic(), (window.performance.now() - startedAt).toInt())
}

private suspend fun memoized(code: String, payload: dynamic, durationMs: Int) =
	resultOf(payload, durationMs).also { if (it.succeeded) CompileMemo.put(code, payload) }

internal suspend fun resultOf(payload: dynamic, durationMs: Int): CompileResult {
	val exception = payload.exception

	return CompileResult(
		jsCode = payload.jsCode as? String,
		chunks = resolveChunks(payload.jsFiles),
		diagnostics = parseDiagnostics(payload.errors),
		exception = (exception?.message as? String)?.let { message ->
			(exception.fullName as? String)?.let { "$it: $message" } ?: message
		},
		durationMs = durationMs,
	)
}
