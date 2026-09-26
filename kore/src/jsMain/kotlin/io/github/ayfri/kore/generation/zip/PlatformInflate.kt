package io.github.ayfri.kore.generation.zip

internal actual fun platformInflate(compressed: ByteArray, uncompressedSize: Int) = Inflate.inflate(compressed, uncompressedSize)
