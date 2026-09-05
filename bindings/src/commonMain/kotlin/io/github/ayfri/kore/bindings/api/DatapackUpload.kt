package io.github.ayfri.kore.bindings.api

import io.github.ayfri.kore.bindings.Datapack
import io.github.ayfri.kore.bindings.explore
import io.github.ayfri.kore.bindings.readZipDatapack
import io.github.ayfri.kore.bindings.renderDatapackFile
import kotlinx.io.files.Path

/**
 * Result of importing an uploaded datapack: the conventional Kotlin file name (`$objectName.kt`) and its
 * rendered source. No filesystem I/O involved - safe to call in a browser (e.g. a website upload widget).
 */
data class GeneratedDatapackSource(val objectName: String, val source: String)

/**
 * Explores a datapack zip's raw bytes (e.g. from a browser `<input type="file">` upload, or any in-memory zip)
 * into a [Datapack]. Works identically on the JVM, Node.js, and the browser: unzipping goes through the
 * multiplatform `kompress` library via [readZipDatapack], never a filesystem.
 *
 * @param bytes the raw `.zip` file content
 * @param displayName the datapack's file name (e.g. `"my_datapack.zip"`), used to derive the package/object name
 */
fun exploreDatapackZip(bytes: ByteArray, displayName: String): Datapack {
	val inMemory = readZipDatapack(bytes)
	return explore(inMemory, displayName, Path(displayName))
}

/**
 * Explores an uploaded datapack zip and renders its Kotlin bindings as source text, without writing to disk.
 * Use [exploreDatapackZip] directly if you only need the [Datapack] model (e.g. to preview its contents before
 * generating code).
 *
 * Docs: [Bindings](https://kore.ayfri.com/docs/advanced/bindings)
 */
fun importDatapackZip(
	bytes: ByteArray,
	displayName: String,
	packageNameOverride: String? = null,
	remappings: RemappingState = RemappingState(),
): GeneratedDatapackSource {
	val datapack = exploreDatapackZip(bytes, displayName)
	val (objectName, source) = renderDatapackFile(datapack, packageNameOverride, remappings)
	return GeneratedDatapackSource(objectName, source)
}
