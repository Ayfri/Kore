package io.github.ayfri.kore.website.components.playground

/**
 * The hidden second file compiled next to the user snippet.
 *
 * Kore exports nothing with `@JsExport`, so the only way to reach a `DataPack` from JS is to compile a
 * `main` we control alongside the editor buffer. The contract is a single `fun playground(): DataPack`.
 *
 * The result is handed over as a plain JS object rather than serialized: the compile backend ships the
 * kotlinx-serialization compiler plugin for JVM only, so `@Serializable` in harness code would not compile
 * for JS. Kore's own serializers are unaffected, they are precompiled into the published klib.
 *
 * Only `exportAsStrings()` is called here. It is pure in-memory, leaves `DataPack.generated` untouched and
 * needs no filesystem, unlike `generate()` / `generateZip()` / `generateJar()`, which throw in a browser.
 * The `.zip` is assembled client-side from these same strings, see [buildZip].
 */
val PLAYGROUND_HARNESS = """
	import io.github.ayfri.kore.exportAsStrings

	fun main() {
		val globals = js("globalThis")

		try {
			val files = js("({})")
			playground().exportAsStrings().forEach { (path, content) -> files[path] = content }
			globals.__koreFiles = files
		} catch (throwable: Throwable) {
			globals.__koreError = throwable.stackTraceToString()
		}
	}
""".trimIndent()
