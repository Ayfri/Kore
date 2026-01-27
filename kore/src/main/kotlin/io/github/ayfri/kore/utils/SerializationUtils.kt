package io.github.ayfri.kore.utils

import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

internal inline fun <reified T : @Serializable Any> T.asArg() = Json.encodeToJsonElement(this@asArg).jsonPrimitive.content

fun KAnnotatedElement.getSerialName(): String? =
	findAnnotation<SerialName>()?.value ?: findAnnotation<JsonSerialName>()?.name

fun KProperty1<*, *>.getSerialName() = (this as KAnnotatedElement).getSerialName() ?: name

fun KClass<*>.getSerialName() = (this as KAnnotatedElement).getSerialName() ?: simpleName ?: ""

@Suppress("UNCHECKED_CAST")
fun KAnnotatedElement.getSpecifiedSerializer(): KSerializer<Any?>? {
	val serializable = findAnnotation<Serializable>() ?: return null
	val serializerClass = serializable.with
	if (serializerClass == KSerializer::class) return null
	return (serializerClass.objectInstance ?: serializerClass.createInstance()) as KSerializer<Any?>
}

@Suppress("UNCHECKED_CAST")
fun KProperty1<*, *>.getSerializer(serializersModule: SerializersModule) =
	getSpecifiedSerializer() ?: serializersModule.serializer(returnType)
