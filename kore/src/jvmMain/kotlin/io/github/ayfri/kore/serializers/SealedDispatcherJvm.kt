package io.github.ayfri.kore.serializers

actual fun subtypeName(value: Any): String? = value.javaClass.simpleName
