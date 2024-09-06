package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.arguments.types.resources.GameEventArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class SetBlockProperties(
	var properties: Map<String, String> = emptyMap(),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect()

fun SetBlockProperties.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

fun SetBlockProperties.properties(vararg properties: Pair<String, String>) {
	this.properties = properties.toMap()
}

fun SetBlockProperties.properties(block: MutableMap<String, String>.() -> Unit) {
	properties = buildMap(block)
}
