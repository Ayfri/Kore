package io.github.ayfri.kore.serializers

import kotlinx.serialization.SerialInfo

@SerialInfo
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class JsonSerialName(val name: String)
