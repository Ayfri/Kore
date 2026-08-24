package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Editor state that survives a reload, kept in `localStorage` under one prefix.
 *
 * Only what is cheap and safe to restore: the buffer the visitor was editing, the splitter position and
 * the JSON preview mode. Compiled chunks are deliberately not stored - a single compile is ~16 MB of
 * JavaScript, far past the 5 MB origin quota.
 *
 * Every access goes through [runCatching]: `localStorage` throws outright in a Safari private window and
 * when storage is disabled, and losing a draft must never take the page down with it.
 */
object PlaygroundStorage {
	private const val KEY_PREFIX = "kore.playground."

	/** Past this the draft is a paste bomb rather than a snippet, and writing it risks a quota error. */
	private const val MAX_DRAFT_LENGTH = 128 * 1024

	/** Enough for a median that follows the backend's current state without dragging old numbers along. */
	private const val MAX_REMEMBERED_COMPILES = 5

	/** The buffer the visitor last edited, restored on the next visit unless a shared link overrides it. */
	var draft: String?
		get() = read("draft")?.takeIf { it.isNotBlank() }
		set(value) = when {
			value == null || value.isBlank() || value.length > MAX_DRAFT_LENGTH -> remove("draft")
			else -> write("draft", value)
		}

	/** Share of the workspace width given to the editor; out-of-range values are ignored, not clamped. */
	var splitFraction: Double?
		get() = read("split")?.toDoubleOrNull()?.takeIf { it > 0.0 && it < 1.0 }
		set(value) = if (value == null) remove("split") else write("split", value.toString())

	/**
	 * How long the last few genuine compiles took, newest last.
	 *
	 * The wait is 6-35 s depending on what the backend's IR cache already holds, so the only honest
	 * estimate is what this visitor has actually measured. It feeds the progress bar and the hint shown
	 * while compiling.
	 */
	var compileDurations: List<Int>
		get() = read("compiles")?.split(',')?.mapNotNull { it.toIntOrNull() }.orEmpty()
		set(value) = write("compiles", value.takeLast(MAX_REMEMBERED_COMPILES).joinToString(","))

	/** Median of [compileDurations], the number a progress bar can be built from. */
	val typicalCompileMs: Int?
		get() = compileDurations.sorted().takeIf { it.isNotEmpty() }?.let { it[it.size / 2] }

	fun recordCompile(durationMs: Int) {
		compileDurations = compileDurations + durationMs
	}

	/** Whether the output preview pretty-prints JSON. */
	var prettyJson: Boolean
		get() = read("pretty") != "false"
		set(value) = write("pretty", value.toString())

	private fun read(key: String) = runCatching { localStorage[KEY_PREFIX + key] }.getOrNull()

	private fun remove(key: String) {
		runCatching { localStorage.removeItem(KEY_PREFIX + key) }
	}

	private fun write(key: String, value: String) {
		runCatching { localStorage[KEY_PREFIX + key] = value }
	}
}
