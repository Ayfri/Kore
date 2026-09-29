package io.github.ayfri.kore.serializers

internal actual fun subtypeName(value: Any): String? = value.javaClass.simpleName
