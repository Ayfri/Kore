package io.github.ayfri.kore.generation.zip

/** Decompresses a raw RFC 1951 DEFLATE stream of [uncompressedSize] bytes: `java.util.zip` on the JVM, the pure-Kotlin [Inflate] on JS. */
internal expect fun platformInflate(compressed: ByteArray, uncompressedSize: Int): ByteArray
