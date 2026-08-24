package io.github.ayfri.kore.website.components.playground

import com.varabyte.kobweb.core.AppGlobals
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import kotlin.js.Promise

/** Name of the file the user edits. Diagnostics are reported against it, so it must match the request. */
const val USER_FILE_NAME = "main.kt"

/** Name of the generated file wrapping the user snippet, never shown in the editor. */
const val HARNESS_FILE_NAME = "__harness.kt"

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
/** What the backend names the entry chunk, the one carrying the harness `main`. */
const val ENTRY_CHUNK_NAME = "playground.js"

private const val NEWLINE = '\n'

data class CompiledChunk(
	val name: String,
	val text: String,
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

private fun projectFile(name: String, text: String): dynamic {
	val file = js("({})")
	file.name = name
	file.text = text
	return file
}

private fun parseChunks(jsFiles: dynamic): List<CompiledChunk> {
	val count = (jsFiles?.length as? Int) ?: return emptyList()

	return (0 until count).mapNotNull { index ->
		val chunk = jsFiles[index]
		val text = chunk?.text as? String ?: return@mapNotNull null

		CompiledChunk(chunk.name as? String ?: "chunk-$index.js", text)
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

/**
 * Diagnostics for [code] alone, without producing any JavaScript.
 *
 * `POST /api/compiler/highlight` type-checks the snippet on the **JVM** and answers in well under a second,
 * against many seconds for the JS compile, which is what makes squiggles-while-typing affordable. The
 * harness is not sent, so every message describes [USER_FILE_NAME] at its real line.
 *
 * The two targets can disagree: anything JVM-only passes here and still fails the real compile, so
 * diagnostics coming back from [compilePlayground] always win.
 */
suspend fun highlightPlayground(code: String): List<PlaygroundDiagnostic> {
	val api = playgroundApiUrl ?: error("No compile backend is configured for this deployment.")

	val body = js("({})")
	body.args = ""
	body.confType = "java"
	body.files = arrayOf(projectFile(USER_FILE_NAME, code))

	val response = window.fetch(
		"$api/api/compiler/highlight",
		RequestInit(
			method = "POST",
			headers = js("({ 'Content-Type': 'application/json' })"),
			body = JSON.stringify(body),
		)
	).await()

	if (!response.ok) error("Diagnostics backend answered ${response.status} ${response.statusText}.")

	// The endpoint returns the per-file map directly, the same shape the compile response nests under `errors`.
	return parseDiagnostics(response.json().await().asDynamic())
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

	"output" -> CompileProgress(
		label = "Downloading",
		detail = "${(event.chunks as? Int) ?: 0} modules, ${humanBytes((event.bytes as? Int) ?: 0)} of JavaScript.",
		fraction = 0.0,
	)

	else -> null
}

/**
 * Compiles [code] while reporting what the backend is doing.
 *
 * `POST /api/compiler/translate/js/stream` answers with newline-delimited JSON: progress lines first, the
 * usual compile result last. Every line reaches [onProgress] as it lands, which is what turns a 6-35 s
 * spinner into a named wait. A backend without the endpoint, or a browser without streaming bodies, falls
 * back to [compilePlayground] and the page behaves as it did before.
 */
suspend fun compilePlaygroundStreaming(code: String, onProgress: (CompileProgress) -> Unit): CompileResult {
	compileCache[code]?.let { return it.copy(cached = true) }

	val api = playgroundApiUrl ?: error("No compile backend is configured for this deployment.")

	val body = js("({})")
	body.args = ""
	body.files = arrayOf(projectFile(USER_FILE_NAME, code), projectFile(HARNESS_FILE_NAME, PLAYGROUND_HARNESS))

	val startedAt = window.performance.now()

	val response = window.fetch(
		"$api/api/compiler/translate/js/stream",
		RequestInit(
			method = "POST",
			headers = js("({ 'Content-Type': 'application/json' })"),
			body = JSON.stringify(body),
		)
	).await()

	val stream = response.asDynamic().body
	if (!response.ok || stream == null || stream == undefined) return compilePlayground(code)

	val reader = stream.getReader()
	val decoder = js("new TextDecoder()")
	var pending = ""
	var payload: dynamic = null
	var downloaded = 0
	var total = 0

	while (payload == null) {
		val step = (reader.read() as Promise<dynamic>).await()
		if (step.done as Boolean) break

		downloaded += (step.value.length as? Int) ?: 0
		pending += decoder.decode(step.value, js("({ stream: true })")) as String

		while (pending.contains(NEWLINE)) {
			val line = pending.substringBefore(NEWLINE)
			pending = pending.substringAfter(NEWLINE)
			if (line.isBlank()) continue

			val event = runCatching { JSON.parse<dynamic>(line) }.getOrNull() ?: continue

			if (event.event == "result") {
				payload = event.result
				break
			}

			if (event.event == "busy") error(event.message as? String ?: "The compile queue is full, retry shortly.")
			if (event.event == "output") total = (event.bytes as? Int) ?: 0

			progressOf(event)?.let(onProgress)
		}

		// The result is the last line and by far the largest, so everything arriving after `output` is it.
		if (payload == null && total > 0) onProgress(
			CompileProgress(
				label = "Downloading",
				detail = "${humanBytes(downloaded)} of ${humanBytes(total)}",
				fraction = (downloaded.toDouble() / total).coerceIn(0.0, 1.0),
			)
		)
	}

	runCatching { reader.cancel() }

	// No result line means the backend answered something else; the plain endpoint reports that properly.
	if (payload == null) return compilePlayground(code)

	val result = resultOf(payload, (window.performance.now() - startedAt).toInt())
	cache(code, result)

	return result
}

/**
 * Successful compiles of the last few buffers, so re-running an unchanged snippet costs nothing.
 *
 * A warm server still spends ~6 s and ~1.7 MB on a repeat, and pressing Run twice - or editing and undoing
 * in between - is the common case. Only successes are kept: a failure is cheap to redo and its diagnostics
 * must follow the current buffer. The entries hold every emitted chunk, ~16 MB of strings each, so the
 * cache is deliberately tiny.
 */
private const val COMPILE_CACHE_SIZE = 2

private val compileCache = LinkedHashMap<String, CompileResult>()

private fun cache(code: String, result: CompileResult) {
	if (!result.succeeded) return

	compileCache.remove(code)
	compileCache[code] = result

	while (compileCache.size > COMPILE_CACHE_SIZE) compileCache.remove(compileCache.keys.first())
}

/**
 * Compiles [code] together with the hidden harness into JavaScript.
 *
 * The endpoint is `kotlin-compiler-server`'s `POST /api/compiler/translate/js`, which answers with the
 * compiled modules plus per-file diagnostics. The two files are sent separately so line numbers reported
 * for [USER_FILE_NAME] map straight onto the editor.
 */
suspend fun compilePlayground(code: String): CompileResult {
	compileCache[code]?.let { return it.copy(cached = true) }

	val api = playgroundApiUrl ?: error("No compile backend is configured for this deployment.")

	val body = js("({})")
	body.args = ""
	body.files = arrayOf(projectFile(USER_FILE_NAME, code), projectFile(HARNESS_FILE_NAME, PLAYGROUND_HARNESS))

	val startedAt = window.performance.now()

	val response = window.fetch(
		"$api/api/compiler/translate/js",
		RequestInit(
			method = "POST",
			headers = js("({ 'Content-Type': 'application/json' })"),
			body = JSON.stringify(body),
		)
	).await()

	if (!response.ok) error("Compile backend answered ${response.status} ${response.statusText}.")

	val result = resultOf(response.json().await().asDynamic(), (window.performance.now() - startedAt).toInt())

	cache(code, result)

	return result
}

private fun resultOf(payload: dynamic, durationMs: Int): CompileResult {
	val exception = payload.exception

	return CompileResult(
		jsCode = payload.jsCode as? String,
		chunks = parseChunks(payload.jsFiles),
		diagnostics = parseDiagnostics(payload.errors),
		exception = (exception?.message as? String)?.let { message ->
			(exception.fullName as? String)?.let { "$it: $message" } ?: message
		},
		durationMs = durationMs,
	)
}
