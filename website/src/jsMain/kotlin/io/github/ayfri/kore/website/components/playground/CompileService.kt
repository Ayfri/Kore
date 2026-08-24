package io.github.ayfri.kore.website.components.playground

import com.varabyte.kobweb.core.AppGlobals
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit

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
) {
	val errors get() = diagnostics.filter { it.severity == DiagnosticSeverity.ERROR }
	val succeeded get() = exception == null && errors.isEmpty() && !jsCode.isNullOrBlank()

	/**
	 * Everything that has to be evaluated, in order, for the harness to run.
	 *
	 * [jsCode] is the entry chunk in both modes, so it is the whole program when the backend answers with a
	 * single bundle and the last element of [chunks] when it answers per module.
	 */
	val evaluationOrder get() = chunks.takeIf { it.isNotEmpty() }?.map { it.text } ?: listOfNotNull(jsCode)
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
 * Compiles [code] together with the hidden harness into JavaScript.
 *
 * The endpoint is `kotlin-compiler-server`'s `POST /api/compiler/translate/js`, which answers with the
 * compiled modules plus per-file diagnostics. The two files are sent separately so line numbers reported
 * for [USER_FILE_NAME] map straight onto the editor.
 */
suspend fun compilePlayground(code: String): CompileResult {
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

	val payload = response.json().await().asDynamic()
	val exception = payload.exception

	return CompileResult(
		jsCode = payload.jsCode as? String,
		chunks = parseChunks(payload.jsFiles),
		diagnostics = parseDiagnostics(payload.errors),
		exception = (exception?.message as? String)?.let { message ->
			(exception.fullName as? String)?.let { "$it: $message" } ?: message
		},
		durationMs = (window.performance.now() - startedAt).toInt(),
	)
}
