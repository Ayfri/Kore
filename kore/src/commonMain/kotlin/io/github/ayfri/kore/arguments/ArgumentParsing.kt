package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import kotlinx.serialization.decodeFromString
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.StringifiedNbt

internal data class ParsedArgument(
	val rawValue: String,
	val namespace: String,
	val name: String,
	val tagged: Boolean,
	val components: ComponentsPatch?,
	val states: MutableMap<String, String>,
	val nbtData: NbtCompound?,
)

internal class RawComponentsPatch(private val value: String) : ComponentsPatch() {
	override fun toString() = value
}

internal fun parseArgument(value: String): ParsedArgument {
	val tagged = value.startsWith("#")
	val unprefixedValue = value.removePrefix("#")
	val id = unprefixedValue.substringBefore("[").substringBefore("{")
	val normalizedValue = when {
		":" in id -> value
		tagged -> "#minecraft:$unprefixedValue"
		else -> "minecraft:$value"
	}
	val namespace = if (":" in id) id.substringBefore(":") else "minecraft"
	val name = if (":" in id) id.substringAfter(":") else id
	val bracketContent = value.substringAfter('[', "").substringBeforeLast(']', "")
	val components = bracketContent.takeIf(String::isNotEmpty)?.let { RawComponentsPatch("[$it]") }
	val states = parseStates(bracketContent)
	val nbtData = parseNbt(value)

	return ParsedArgument(normalizedValue, namespace, name, tagged, components, states, nbtData)
}

internal fun ParsedArgument.baseId() = buildString {
	if (tagged) append('#')
	append(namespace)
	append(':')
	append(name)
}

internal fun Map<String, String>.asStateString() = when {
	isEmpty() -> ""
	else -> entries.joinToString(",", prefix = "[", postfix = "]") { "${it.key}=${it.value}" }
}

internal fun observedStates(states: Map<String, String>, onChange: () -> Unit): MutableMap<String, String> =
	ObservableMutableMap(states, onChange)

internal class ObservableMutableMap private constructor(
	private val delegate: LinkedHashMap<String, String>,
	private val onChange: () -> Unit,
) : MutableMap<String, String> by delegate {
	constructor(states: Map<String, String>, onChange: () -> Unit) : this(LinkedHashMap(states), onChange)

	override fun put(key: String, value: String): String? = delegate.put(key, value).also { onChange() }

	override fun putAll(from: Map<out String, String>) {
		if (from.isEmpty()) return
		delegate.putAll(from)
		onChange()
	}

	override fun remove(key: String): String? = delegate.remove(key).also {
		if (it != null) onChange()
	}

	override fun clear() {
		if (delegate.isEmpty()) return
		delegate.clear()
		onChange()
	}
}

private fun parseStates(content: String) = content
	.takeIf(String::isNotBlank)
	?.split(',')
	?.mapNotNull { entry ->
		val separatorIndex = entry.indexOf('=')
		if (separatorIndex < 0) return@mapNotNull null
		val key = entry.take(separatorIndex).trim()
		val value = entry.substring(separatorIndex + 1).trim()
		key to value
	}
	?.toMap()
	?.toMutableMap()
	?: mutableMapOf()

private fun parseNbt(value: String): NbtCompound? {
	val nbtStart = value.indexOf('{')
	if (nbtStart < 0) return null

	return runCatching {
		StringifiedNbt.decodeFromString<NbtCompound>(value.substring(nbtStart))
	}.getOrNull()
}
