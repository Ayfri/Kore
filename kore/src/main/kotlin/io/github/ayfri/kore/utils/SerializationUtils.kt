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
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

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

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.createInstance(values: Map<String, Any?>): T {
	val constructor = primaryConstructor ?: constructors.first()
	constructor.isAccessible = true
	val params = constructor.parameters.mapNotNull { param ->
		if (values.containsKey(param.name)) {
			param to values[param.name]
		} else if (param.isOptional) {
			null
		} else {
			param to null
		}
	}.toMap()

	val instance = constructor.callBy(params)

	values.forEach { (name, value) ->
		val property = memberProperties.find { it.name == name }
		if (property is KMutableProperty1<*, *> && constructor.parameters.none { it.name == name }) {
			property.isAccessible = true
			(property as KMutableProperty1<Any, Any?>).set(instance, value)
		}
	}

	return instance
}
