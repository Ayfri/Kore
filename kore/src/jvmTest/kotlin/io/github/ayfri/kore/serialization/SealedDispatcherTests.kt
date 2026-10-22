package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.timelines.easingTypeSealedSerializer
import io.github.ayfri.kore.serializers.EnumLikeSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.serializers.SealedDispatcher
import io.kotest.core.spec.style.FunSpec
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.name
import kotlin.io.path.relativeTo
import kotlin.io.path.toPath
import kotlin.io.path.walk

class SealedDispatcherTests : FunSpec({
	val classes = SealedDispatcher::class.java.protectionDomain.codeSource.location.toURI().toPath()

	fun Path.className() = relativeTo(classes).invariantSeparatorsPathString.removeSuffix(".class").replace('/', '.')

	val dispatchers = classes.walk().filter { it.name.endsWith("SealedSerializerKt.class") }.map { file ->
		Class.forName(file.className()).declaredMethods.single().invoke(null) as SealedDispatcher<*>
	}.toList()

	test("every generated sealed family lists its subtypes under distinct serial names") {
		("AdvancementTriggerCondition" in dispatchers.map { it.serialName }) assertsIs true
		dispatchers.forEach { (it.serializersBySerialName.isNotEmpty()) assertsIs true }
	}

	test("every sealed family writes its subtypes under distinct names") {
		val families = classes.walk().filter { it.name.endsWith("Serializer.class") }.mapNotNull { file ->
			val instance = Class.forName(file.className()).declaredFields.singleOrNull { it.name == "INSTANCE" }
			when (val serializer = instance?.apply { isAccessible = true }?.get(null)) {
				is NamespacedPolymorphicSerializer<*> -> serializer.checkSubtypeNames()
				is EnumLikeSerializer<*> -> serializer.checkCaseNames()
				else -> null
			}
		}.toList() + EnumLikeSerializer(easingTypeSealedSerializer()).checkCaseNames()

		// ChatComponent resolves its subtypes by their discriminator, not by a written name
		families.sorted() assertsIs dispatchers.map { it.serialName }.filter { it != "ChatComponent" }.sorted()
	}
})
