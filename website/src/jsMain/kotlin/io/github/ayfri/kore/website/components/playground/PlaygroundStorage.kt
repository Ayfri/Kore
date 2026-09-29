package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Editor state that survives a reload, kept in `localStorage` under one prefix.
 *
 * Only what is cheap and safe to restore: the buffer the visitor was editing, recent compile times, which chunks
 * the last run used, and the [Persisted] preferences of [PlaygroundSettings] and [PlaygroundLayout]. The chunks
 * themselves live in [ChunkStore]: a compile is ~16 MB of JavaScript, far past the 5 MB origin quota of `localStorage`.
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

	/** The example the draft started from, what Reset goes back to once the draft no longer matches any example. */
	var exampleSlug: String?
		get() = read("example")
		set(value) = if (value == null) remove("example") else write("example", value)

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

	/**
	 * Names and hashes of the library chunks of the last run, in evaluation order: the texts sit in [ChunkStore], and
	 * the next visit loads them into the worker as soon as an edit starts a compile.
	 */
	var libraries: List<Pair<String, String>>
		get() = read("libraries")?.split(',')?.mapNotNull { entry -> entry.split(':').takeIf { it.size == 2 }?.let { it[0] to it[1] } }.orEmpty()
		set(value) = write("libraries", value.joinToString(",") { (name, hash) -> "$name:$hash" })

	fun read(key: String) = runCatching { localStorage[KEY_PREFIX + key] }.getOrNull()

	fun remove(key: String) {
		runCatching { localStorage.removeItem(KEY_PREFIX + key) }
	}

	fun write(key: String, value: String) {
		runCatching { localStorage[KEY_PREFIX + key] = value }
	}
}

/**
 * A Compose state mirrored to `localStorage` under [key], so a preference is observable and kept across visits.
 *
 * The stored text goes through [decode], and anything it rejects, a value from an older page included, falls back to
 * [default] instead of breaking the page.
 */
class Persisted<T>(private val key: String, default: T, decode: (String) -> T?) : ReadWriteProperty<Any?, T> {
	private var value by mutableStateOf(PlaygroundStorage.read(key)?.let(decode) ?: default)

	override fun getValue(thisRef: Any?, property: KProperty<*>) = value

	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		this.value = value
		PlaygroundStorage.write(key, value.toString())
	}
}

fun persisted(key: String, default: Boolean) = Persisted(key, default) { it.toBooleanStrictOrNull() }

fun persisted(key: String, default: Double, range: ClosedFloatingPointRange<Double>) =
	Persisted(key, default) { stored -> stored.toDoubleOrNull()?.takeIf { it in range } }

fun persisted(key: String, default: Int, range: IntRange) = Persisted(key, default) { stored -> stored.toIntOrNull()?.takeIf { it in range } }

inline fun <reified E : Enum<E>> persisted(key: String, default: E) = Persisted(key, default) { stored -> enumValues<E>().firstOrNull { it.name == stored } }
