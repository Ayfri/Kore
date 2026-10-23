package io.github.ayfri.kore.serializers

import kotlinx.serialization.SerializationStrategy

internal actual fun <T> SerializationStrategy<T>.canonicalNumbers(): SerializationStrategy<T> = this
