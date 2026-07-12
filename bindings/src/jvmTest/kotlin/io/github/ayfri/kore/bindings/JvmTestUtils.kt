package io.github.ayfri.kore.bindings

fun java.nio.file.Path.toKotlinxIoPath(): kotlinx.io.files.Path = kotlinx.io.files.Path(toString())
