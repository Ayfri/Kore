package utils

fun String.snakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
