package io.github.ayfri.kore.bindings.download

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.js.Promise

/**
 * Hand-written OPFS + `fetch` bindings: not part of the Kotlin/JS stdlib (newer web APIs), and pulling in a
 * whole external bindings library for a handful of calls would go against the project's zero-dependency bias.
 * Mirrors `kore`'s own `OpfsBindings.kt`, duplicated here since that one is `internal` to the `kore` module.
 */
internal external interface FileSystemHandleOptions {
	var create: Boolean?
}

internal external interface FileSystemFileHandle {
	fun getFile(): Promise<JsFile>
	fun createWritable(): Promise<FileSystemWritableFileStream>
}

internal external interface FileSystemDirectoryHandle {
	fun getDirectoryHandle(name: String, options: FileSystemHandleOptions = definedExternally): Promise<FileSystemDirectoryHandle>
	fun getFileHandle(name: String, options: FileSystemHandleOptions = definedExternally): Promise<FileSystemFileHandle>
}

internal external interface FileSystemWritableFileStream {
	fun write(data: Int8Array): Promise<Nothing?>
	fun close(): Promise<Nothing?>
}

internal external interface JsFile {
	fun arrayBuffer(): Promise<ArrayBuffer>
}

internal external interface StorageManager {
	fun getDirectory(): Promise<FileSystemDirectoryHandle>
}

internal external interface NavigatorWithStorage {
	val storage: StorageManager
}

internal external interface FetchResponse {
	val status: Int
	fun arrayBuffer(): Promise<ArrayBuffer>
}

internal fun fileSystemHandleOptions(create: Boolean): FileSystemHandleOptions {
	val options: dynamic = js("({})")
	options.create = create
	return options.unsafeCast<FileSystemHandleOptions>()
}

internal fun opfsNavigator(): NavigatorWithStorage = js("navigator").unsafeCast<NavigatorWithStorage>()

internal suspend fun <T> Promise<T>.await(): T = kotlin.coroutines.suspendCoroutine { continuation ->
	then({ continuation.resumeWith(Result.success(it)) }, { continuation.resumeWith(Result.failure(it)) })
}

internal fun ByteArray.toInt8Array(): Int8Array {
	val array = Int8Array(size)
	for (i in indices) array[i] = this[i]
	return array
}

internal fun ArrayBuffer.toByteArray(): ByteArray {
	val view = Int8Array(this)
	return ByteArray(view.length) { view[it] }
}

/** Performs an HTTP request via the global `fetch` (available in browsers and Node.js 18+). */
internal fun jsFetch(url: String, method: String, headers: Map<String, String>, body: ByteArray?): Promise<FetchResponse> {
	val init: dynamic = js("({})")
	init.method = method
	val headersObj: dynamic = js("({})")
	headers.forEach { (key, value) -> headersObj[key] = value }
	init.headers = headersObj
	if (body != null) init.body = body.toInt8Array()

	val fetchFn = js("fetch")
	return fetchFn(url, init).unsafeCast<Promise<FetchResponse>>()
}
