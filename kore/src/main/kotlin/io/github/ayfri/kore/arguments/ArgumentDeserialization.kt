package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import kotlinx.serialization.decodeFromString
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.StringifiedNbt
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy

private const val KORE_PACKAGE = "io.github.ayfri.kore"

internal data class ParsedArgument(
	val rawValue: String,
	val namespace: String,
	val name: String,
	val tagged: Boolean,
	val components: ComponentsPatch?,
	val states: MutableMap<String, String>,
	val nbtData: NbtCompound?,
)

private class RawComponentsPatch(private val value: String) : ComponentsPatch() {
	override fun toString() = value
}

private val cachedInterfaces by lazy(::collectArgumentInterfaces)

internal fun createArgumentProxyInternal(value: String): Argument {
	val parsed = parseArgument(value)
	var components = parsed.components
	var rawValue = parsed.rawValue
	lateinit var states: MutableMap<String, String>
	var nbtData = parsed.nbtData

	fun updateItemValue() {
		rawValue = parsed.baseId() + (components?.toString() ?: "")
	}

	fun updateBlockValue() {
		rawValue = parsed.baseId() + states.asStateString() + (nbtData?.toString() ?: "")
	}

	states = observedStates(parsed.states, ::updateBlockValue)

	val handler = InvocationHandler { _, method, args ->
		when (method.name) {
			"asString", "asId", "toString" -> rawValue
			"getName" -> parsed.name
			"getNamespace" -> parsed.namespace
			"getComponents" -> components
			"setComponents" -> {
				components = args?.firstOrNull() as ComponentsPatch?
				updateItemValue()
			}

			"getStates" -> states
			"setStates" -> {
				states = observedStates(
					(args?.firstOrNull() as? Map<*, *>)
						?.mapNotNull { (key, value) ->
							(key as? String)?.let { safeKey ->
								(value as? String)?.let { safeValue -> safeKey to safeValue }
							}
						}
						?.toMap()
						?: emptyMap(),
					::updateBlockValue,
				)
				updateBlockValue()
			}

			"getNbtData" -> nbtData
			"setNbtData" -> {
				nbtData = args?.firstOrNull() as NbtCompound?
				updateBlockValue()
			}

			"hashCode" -> rawValue.hashCode()
			"equals" -> {
				val other = args?.firstOrNull()
				other is Argument && other.asString() == rawValue
			}

			else -> null
		}
	}

	@Suppress("UNCHECKED_CAST")
	return Proxy.newProxyInstance(
		Thread.currentThread().contextClassLoader ?: Argument::class.java.classLoader,
		cachedInterfaces,
		handler
	) as Argument
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

private fun ParsedArgument.baseId() = buildString {
	if (tagged) append('#')
	append(namespace)
	append(':')
	append(name)
}

private fun Map<String, String>.asStateString() = when {
	isEmpty() -> ""
	else -> entries.joinToString(",", prefix = "[", postfix = "]") { "${it.key}=${it.value}" }
}

private fun observedStates(states: Map<String, String>, onChange: () -> Unit): MutableMap<String, String> =
	ObservableMutableMap(states, onChange)

private class ObservableMutableMap(
	states: Map<String, String>,
	private val onChange: () -> Unit,
) : LinkedHashMap<String, String>(states) {
	override fun put(key: String, value: String): String? = super.put(key, value).also { onChange() }

	override fun putAll(from: Map<out String, String>) {
		if (from.isEmpty()) return
		super.putAll(from)
		onChange()
	}

	override fun remove(key: String): String? = super.remove(key).also {
		if (it != null) onChange()
	}

	override fun clear() {
		if (isEmpty()) return
		super.clear()
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

private fun collectArgumentInterfaces(): Array<Class<*>> {
	val result = mutableSetOf<Class<*>>(Argument::class.java)
	val classLoader = Thread.currentThread().contextClassLoader ?: Argument::class.java.classLoader
	try {
		val resourcePath = KORE_PACKAGE.replace('.', '/')
		val urls = classLoader.getResources(resourcePath)
		while (urls.hasMoreElements()) {
			val url = urls.nextElement()
			if (url.protocol == "file") {
				scanDirectory(File(url.toURI()), KORE_PACKAGE, classLoader, result)
			} else if (url.protocol == "jar") {
				scanJar(url, classLoader, result)
			}
		}
	} catch (_: Exception) {
	}
	return result.toTypedArray()
}

private fun scanJar(url: java.net.URL, classLoader: ClassLoader, result: MutableSet<Class<*>>) {
	val jarConnection = url.openConnection() as java.net.JarURLConnection
	val jarFile = jarConnection.jarFile
	val prefix = KORE_PACKAGE.replace('.', '/')
	for (entry in jarFile.entries()) {
		val entryName = entry.name
		if (entryName.startsWith(prefix) && entryName.endsWith(".class")) {
			val className = entryName.removeSuffix(".class").replace('/', '.')
			tryAddArgumentInterface(className, classLoader, result)
		}
	}
}

private fun scanDirectory(dir: File, pkg: String, classLoader: ClassLoader, result: MutableSet<Class<*>>) {
	if (!dir.exists()) return
	for (file in dir.listFiles() ?: return) {
		if (file.isDirectory) {
			scanDirectory(file, "$pkg.${file.name}", classLoader, result)
		} else if (file.name.endsWith(".class")) {
			val className = "$pkg.${file.name.removeSuffix(".class")}"
			tryAddArgumentInterface(className, classLoader, result)
		}
	}
}

private fun tryAddArgumentInterface(className: String, classLoader: ClassLoader, result: MutableSet<Class<*>>) {
	try {
		val cls = Class.forName(className, false, classLoader)
		if (cls.isInterface
			&& Argument::class.java.isAssignableFrom(cls)
			&& !Modifier.isPrivate(cls.modifiers)
			&& !cls.isSealed
		) {
			result.add(cls)
		}
	} catch (_: Exception) {
	}
}