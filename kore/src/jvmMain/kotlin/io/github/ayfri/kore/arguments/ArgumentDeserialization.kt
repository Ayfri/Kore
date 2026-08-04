package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import net.benwoodworth.knbt.NbtCompound
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

// All `Argument` subinterfaces live under these two packages.
private val ARGUMENT_SCAN_PACKAGES = listOf(
	"io.github.ayfri.kore.arguments",
	"io.github.ayfri.kore.generated.arguments",
)

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

private fun collectArgumentInterfaces(): Array<Class<*>> {
	val classLoader = Thread.currentThread().contextClassLoader ?: Argument::class.java.classLoader
	val classNames = mutableSetOf<String>()

	for (pkg in ARGUMENT_SCAN_PACKAGES) {
		try {
			val resourcePath = pkg.replace('.', '/')
			val urls = classLoader.getResources(resourcePath)
			while (urls.hasMoreElements()) {
				val url = urls.nextElement()
				if (url.protocol == "file") {
					collectClassNames(File(url.toURI()), pkg, classNames)
				} else if (url.protocol == "jar") {
					collectClassNamesFromJar(url, pkg, classNames)
				}
			}
		} catch (_: Exception) {
		}
	}

	val result = ConcurrentHashMap.newKeySet<Class<*>>()
	result += Argument::class.java
	classNames.parallelStream().forEach { tryAddArgumentInterface(it, classLoader, result) }
	return result.toTypedArray()
}

private fun collectClassNamesFromJar(url: java.net.URL, pkg: String, result: MutableSet<String>) {
	val jarConnection = url.openConnection() as java.net.JarURLConnection
	val jarFile = jarConnection.jarFile
	val prefix = pkg.replace('.', '/')
	for (entry in jarFile.entries()) {
		val entryName = entry.name
		if (entryName.startsWith(prefix) && entryName.endsWith(".class")) {
			result += entryName.removeSuffix(".class").replace('/', '.')
		}
	}
}

private fun collectClassNames(dir: File, pkg: String, result: MutableSet<String>) {
	if (!dir.exists()) return
	val dirPath = dir.toPath()
	dir.walkTopDown()
		.filter { it.isFile && it.extension == "class" }
		.mapTo(result) { file ->
			val relative = dirPath.relativize(file.toPath()).joinToString(".") { it.toString().removeSuffix(".class") }
			"$pkg.$relative"
		}
}

private fun tryAddArgumentInterface(className: String, classLoader: ClassLoader, result: MutableCollection<Class<*>>) {
	try {
		val cls = Class.forName(className, false, classLoader)
		if (cls.isInterface
			&& Argument::class.java.isAssignableFrom(cls)
			&& !Modifier.isPrivate(cls.modifiers)
			&& !cls.isSealed
		) {
			result += cls
		}
	} catch (_: Exception) {
	}
}
