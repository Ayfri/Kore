package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.encodeToJsonElement

@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
	allowStructuredMapKeys = true
	classDiscriminatorMode = ClassDiscriminatorMode.NONE
	explicitNulls = false
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

data class Selector(val base: SelectorType) {
	val nbtData = SelectorArguments()
	val isPlayer get() = base.isPlayer

	override fun toString() = when {
		nbtData == SelectorArguments() -> "@${base.value}"
		else -> "@${base.value}[${json.encodeToJsonElement(nbtData).toString().unescape()}]"
	}
}
