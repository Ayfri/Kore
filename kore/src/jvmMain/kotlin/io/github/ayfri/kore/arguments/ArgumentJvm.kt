package io.github.ayfri.kore.arguments

actual fun createArgumentProxy(value: String): Argument = createArgumentProxyInternal(value)

actual val canDeserializeTypedArguments: Boolean = true
