package io.github.ayfri.kore.utils

fun String.snakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()

fun String.ifNotEmpty(block: (String) -> String) = if (isNotEmpty()) block(this) else this
