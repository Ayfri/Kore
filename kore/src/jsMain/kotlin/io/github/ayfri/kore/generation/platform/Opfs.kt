package io.github.ayfri.kore.generation.platform

import kotlinx.io.files.Path
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

/** Origin Private File System (OPFS) access for the browser, built on the bindings in `OpfsBindings.kt`. */
internal object Opfs {
	private fun segments(path: Path) = path.toString().replace('\\', '/').split('/').filter { it.isNotEmpty() }

	private suspend fun root(): FileSystemDirectoryHandle = opfsNavigator().storage.getDirectory().await()

	private suspend fun directory(segments: List<String>, create: Boolean): FileSystemDirectoryHandle {
		var handle = root()
		for (segment in segments) handle = handle.getDirectoryHandle(segment, fileSystemHandleOptions(create)).await()
		return handle
	}

	suspend fun createDirectories(path: Path) {
		directory(segments(path), create = true)
	}

	suspend fun writeFile(path: Path, content: ByteArray) {
		val parts = segments(path)
		val parent = directory(parts.dropLast(1), create = true)
		val fileHandle = parent.getFileHandle(parts.last(), fileSystemHandleOptions(true)).await()
		val writable = fileHandle.createWritable().await()
		writable.write(content.toInt8Array()).await()
		writable.close().await()
	}

	suspend fun readFile(path: Path): ByteArray? {
		val parts = segments(path)
		return try {
			val parent = directory(parts.dropLast(1), create = false)
			val fileHandle = parent.getFileHandle(parts.last(), fileSystemHandleOptions(false)).await()
			val file = fileHandle.getFile().await()
			file.arrayBuffer().await().toByteArray()
		} catch (_: Throwable) {
			null
		}
	}

	suspend fun exists(path: Path): Boolean {
		val parts = segments(path)
		if (parts.isEmpty()) return true

		return try {
			val parent = directory(parts.dropLast(1), create = false)
			try {
				parent.getFileHandle(parts.last(), fileSystemHandleOptions(false)).await()
				true
			} catch (_: Throwable) {
				parent.getDirectoryHandle(parts.last(), fileSystemHandleOptions(false)).await()
				true
			}
		} catch (_: Throwable) {
			false
		}
	}
}

private suspend fun <T> Promise<T>.await(): T = suspendCoroutine { continuation ->
	then({ continuation.resume(it) }, { continuation.resumeWithException(it) })
}

private fun ByteArray.toInt8Array(): Int8Array {
	val array = Int8Array(size)
	for (i in indices) array[i] = this[i]
	return array
}

private fun ArrayBuffer.toByteArray(): ByteArray {
	val view = Int8Array(this)
	val array = ByteArray(view.length)
	for (i in array.indices) array[i] = view[i]
	return array
}
