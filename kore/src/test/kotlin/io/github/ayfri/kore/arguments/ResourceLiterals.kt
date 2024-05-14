package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.types.resources.block
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.utils.set

fun resourceLiteralsTests() {
	blockTests()
}

fun blockTests() {
	val block = block("test")
	block.asString() assertsIs "minecraft:test"

	val blockWithNamespace = block("test", "namespace")
	blockWithNamespace.asString() assertsIs "namespace:test"

	val blockWithStates = block("test", states = mapOf("state" to "value"))
	blockWithStates.asString() assertsIs "minecraft:test[state=value]"

	val blockWithNbt = block("test") {
		this["key"] = "value"
	}
	blockWithNbt.asString() assertsIs "minecraft:test{key:\"value\"}"

	val blockWithEmptyNbt = block("test") {}
	blockWithEmptyNbt.asString() assertsIs "minecraft:test"
}
