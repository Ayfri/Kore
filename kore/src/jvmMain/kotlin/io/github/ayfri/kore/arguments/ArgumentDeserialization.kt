package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import net.benwoodworth.knbt.NbtCompound
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy

private const val KORE_PACKAGE = "io.github.ayfri.kore"

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
