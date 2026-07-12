package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.RemappingState
import io.github.ayfri.kore.bindings.generation.*
import io.github.ayfri.kore.bindings.generation.codegen.KtFile
import io.github.ayfri.kore.bindings.generation.codegen.KtModifier
import io.github.ayfri.kore.bindings.generation.codegen.KtPropertySpec
import io.github.ayfri.kore.bindings.generation.codegen.KtRef
import io.github.ayfri.kore.bindings.generation.codegen.KtTypeKind
import io.github.ayfri.kore.bindings.generation.codegen.KtTypeSpec
import io.github.ayfri.kore.bindings.generation.codegen.kotlinStringLiteral
import io.github.ayfri.kore.bindings.generation.codegen.renderKtFile

private val stringRef = KtRef("", "String")
private val functionArgumentRef = KtRef("io.github.ayfri.kore.arguments.types.resources", "FunctionArgument")
private val tagArgumentRef = KtRef("io.github.ayfri.kore.arguments.types.resources", "TagArgument")

/**
 * Normalizes a datapack's raw name into a Kotlin-safe base name (used for the package name and,
 * unless overridden by [RemappingState.objectName], the generated object name).
 */
internal fun normalizedDatapackName(rawName: String): String {
	val baseName = rawName.removeSuffix(".zip")
	val decodedName = try {
		urlDecode(baseName)
	} catch (_: Exception) {
		baseName
	}
	return decodedName
		.replace(".", "-")
		.replace(Regex("[^a-zA-Z0-9_-]"), "")
}

/**
 * Renders the generated Kotlin bindings source for [datapack] with no I/O involved - the caller
 * decides what to do with the resulting text (write to disk, download as a file, display in a browser...).
 * Returns the generated object's name (used as the conventional file name, `$objectName.kt`) alongside the source text.
 */
fun renderDatapackFile(
	datapack: Datapack,
	packageNameOverride: String? = null,
	remappings: RemappingState = RemappingState(),
): Pair<String, String> {
	val baseName = normalizedDatapackName(datapack.name)

	val normalizedName = remappings.objectName ?: baseName
	val packageName = packageNameOverride ?: "kore.dependencies.${normalizedName.lowercase().replace(Regex("[^a-z0-9]"), "")}"

	val datapackObjectName = normalizedName
		.replace(".", "-")
		.split(Regex("[_-]"))
		.joinToString("") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }

	// Collect all namespaces from functions and resources
	val functionNamespaces = datapack.functions.map { it.id.substringBefore(":") }.toSet()
	val resourceNamespaces = datapack.resources.values.flatten().map { it.id.substringBefore(":") }.toSet()
	val allNamespaces = (functionNamespaces + resourceNamespaces).sorted()

	val separator = "/"

	val properties = mutableListOf<KtPropertySpec>()
	val nestedTypes = mutableListOf<KtTypeSpec>()

	properties += KtPropertySpec(
		name = "PATH",
		type = stringRef,
		modifiers = setOf(KtModifier.CONST),
		initializer = kotlinStringLiteral(datapack.path.toString().replace("\\", "/")),
	)

	datapack.pack?.let { packMeta -> properties += generatePackProperty(packMeta) }

	fun addResources(
		target: MutableList<KtTypeSpec>,
		functions: List<Function>,
		resources: Map<String, List<Resource>>,
		namespace: String,
		selfObjectName: String,
	) {
		if (functions.isNotEmpty()) {
			val hasNestedFunctions = functions.any { separator in it.id.substringAfter(":") }
			target += if (hasNestedFunctions) {
				generateFunctionsEnumTree(functions, separator, packageName, selfObjectName, namespace)
			} else {
				generateSimpleFunctionsEnum(functions, namespace)
			}
		}

		val worldgenResources = mutableListOf<Resource>()
		val tagResources = mutableListOf<Resource>()

		resources.forEach { (resourceType, resourceList) ->
			when {
				resourceType.startsWith("worldgen/") -> worldgenResources += resourceList
				resourceType.startsWith("tags/") -> tagResources += resourceList
				else -> {
					val typeInfo = getResourceTypeInfo(resourceType)
					val hasNestedResources = resourceList.any { separator in it.id.substringAfter(":") }
					target += if (hasNestedResources) {
						generateResourceEnumTree(resourceList, typeInfo, separator, packageName, selfObjectName, namespace)
					} else {
						generateSimpleResourceEnum(resourceList, typeInfo, namespace)
					}
				}
			}
		}

		if (worldgenResources.isNotEmpty()) {
			target += generateWorldgenObject(worldgenResources, namespace)
		}

		if (tagResources.isNotEmpty()) {
			target += generateTagsObject(tagResources, namespace, packageName, selfObjectName)
		}
	}

	// If single namespace, generate content directly in the main object
	if (allNamespaces.size == 1) {
		val namespace = allNamespaces.first()
		properties += KtPropertySpec(
			name = "NAMESPACE",
			type = stringRef,
			modifiers = setOf(KtModifier.CONST),
			initializer = kotlinStringLiteral(namespace),
		)
		addResources(nestedTypes, datapack.functions, datapack.resources, namespace, datapackObjectName)
	} else {
		// Multiple namespaces - create intermediate objects for each namespace
		allNamespaces.forEach { namespace ->
			val namespaceObjectName = remappings.namespaces[namespace] ?: namespace
				.replace(Regex("[^a-zA-Z0-9_]"), "_")
				.pascalCase()

			val namespaceProperties = mutableListOf(
				KtPropertySpec(
					name = "NAMESPACE",
					type = stringRef,
					modifiers = setOf(KtModifier.CONST),
					initializer = kotlinStringLiteral(namespace),
				)
			)
			val namespaceNestedTypes = mutableListOf<KtTypeSpec>()

			val namespaceFunctions = datapack.functions.filter { it.id.startsWith("$namespace:") }
			val namespaceResources = datapack.resources.mapValues { (_, resourceList) ->
				resourceList.filter { it.id.startsWith("$namespace:") }
			}.filterValues { it.isNotEmpty() }

			addResources(
				namespaceNestedTypes,
				namespaceFunctions,
				namespaceResources,
				namespace,
				"$datapackObjectName.$namespaceObjectName",
			)

			nestedTypes += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = namespaceObjectName,
				modifiers = setOf(KtModifier.DATA),
				properties = namespaceProperties,
				nestedTypes = namespaceNestedTypes,
			)
		}
	}

	val dataObject = KtTypeSpec(
		kind = KtTypeKind.OBJECT,
		name = datapackObjectName,
		modifiers = setOf(KtModifier.DATA),
		properties = properties,
		nestedTypes = nestedTypes,
	)

	val file = KtFile(
		packageName = packageName,
		fileComment = "Automatically generated from datapack: ${datapack.name}",
		types = listOf(dataObject),
	)

	return datapackObjectName to renderKtFile(file)
}

/** Minimal UTF-8 percent-decoder (`java.net.URLDecoder` equivalent), so this stays pure `commonMain`. */
internal fun urlDecode(value: String): String {
	val bytes = mutableListOf<Byte>()
	var i = 0
	while (i < value.length) {
		val c = value[i]
		when {
			c == '%' && i + 2 < value.length -> {
				bytes += value.substring(i + 1, i + 3).toInt(16).toByte()
				i += 3
			}
			c == '+' -> {
				bytes += ' '.code.toByte()
				i++
			}
			else -> {
				bytes += c.code.toByte()
				i++
			}
		}
	}
	return bytes.toByteArray().decodeToString()
}
