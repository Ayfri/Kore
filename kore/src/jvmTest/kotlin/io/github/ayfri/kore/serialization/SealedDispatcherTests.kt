package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.serializers.SealedDispatcher
import io.kotest.core.spec.style.FunSpec
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.name
import kotlin.io.path.relativeTo
import kotlin.io.path.toPath
import kotlin.io.path.walk

class SealedDispatcherTests : FunSpec({
	test("every generated sealed family lists its subtypes under distinct serial names") {
		val classes = SealedDispatcher::class.java.protectionDomain.codeSource.location.toURI().toPath()
		val dispatchers = classes.walk().filter { it.name.endsWith("SealedSerializerKt.class") }.map { file ->
			val className = file.relativeTo(classes).invariantSeparatorsPathString.removeSuffix(".class").replace('/', '.')
			Class.forName(className).declaredMethods.single().invoke(null) as SealedDispatcher<*>
		}.toList()

		("AdvancementTriggerCondition" in dispatchers.map { it.serialName }) assertsIs true
		dispatchers.forEach { (it.serializersBySerialName.isNotEmpty()) assertsIs true }
	}
})
