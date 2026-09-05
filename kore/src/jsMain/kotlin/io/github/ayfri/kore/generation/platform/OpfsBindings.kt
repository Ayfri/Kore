package io.github.ayfri.kore.generation.platform

import org.khronos.webgl.Int8Array
import kotlin.js.Promise

/**
 * Hand-written File System Access API / OPFS bindings: not part of `org.w3c.dom` in the Kotlin stdlib (it's a
 * newer web API), and pulling in a whole external bindings library for a handful of calls would go against the
 * project's zero-dependency bias.
 */
internal external interface FileSystemHandleOptions {
	var create: Boolean?
}

internal external interface FileSystemFileHandle {
	fun getFile(): Promise<JsFile>
	fun createWritable(): Promise<FileSystemWritableFileStream>
}

internal external interface FileSystemDirectoryHandle {
	fun getDirectoryHandle(
		name: String,
		options: FileSystemHandleOptions = definedExternally
	): Promise<FileSystemDirectoryHandle>

	fun getFileHandle(name: String, options: FileSystemHandleOptions = definedExternally): Promise<FileSystemFileHandle>
}

internal external interface FileSystemWritableFileStream {
	fun write(data: Int8Array): Promise<Nothing?>
	fun close(): Promise<Nothing?>
}

/** The DOM `File` object handed back by [FileSystemFileHandle.getFile]. */
internal external interface JsFile {
	fun arrayBuffer(): Promise<org.khronos.webgl.ArrayBuffer>
}

internal external interface StorageManager {
	fun getDirectory(): Promise<FileSystemDirectoryHandle>
}

internal external interface NavigatorWithStorage {
	val storage: StorageManager
}

internal fun fileSystemHandleOptions(create: Boolean): FileSystemHandleOptions {
	val options: dynamic = js("({})")
	options.create = create
	return options.unsafeCast<FileSystemHandleOptions>()
}

internal fun opfsNavigator(): NavigatorWithStorage = js("navigator").unsafeCast<NavigatorWithStorage>()
