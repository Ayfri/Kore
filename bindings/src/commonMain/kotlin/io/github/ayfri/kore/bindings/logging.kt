package io.github.ayfri.kore.bindings

internal const val yellow = "\u001B[33m"
internal const val reset = "\u001B[0m"

internal fun warn(message: String) = println("${yellow}Warning: $message$reset")
