package io.github.ayfri.kore.bindings

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.ayfri.kore.bindings.generation.*
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute

/**
 * Main entry point for generating Kotlin bindings from a datapack.
 * Normalizes the datapack name and delegates to generateDatapackFile.
 */
fun writeFiles(datapack: Datapack, outputPath: Path) {
	val baseName = datapack.name.removeSuffix(".zip")
	// Decode URL-encoded characters and remove invalid characters
	val decodedName = URLDecoder.decode(baseName, "UTF-8")
	val normalizedName = decodedName
		.replace(".", "-")
		.replace(Regex("[^a-zA-Z0-9_-]"), "")
	val packageName = "kore.dependencies.${normalizedName.lowercase().replace(Regex("[^a-z0-9]"), "")}"

	generateDatapackFile(datapack, outputPath, packageName, normalizedName)
}

/**
 * Generates the main datapack Kotlin file with all resources.
 * Creates a single data object containing all functions, resources, and pack metadata.
 */
fun generateDatapackFile(datapack: Datapack, outputDir: Path, packageNameOverride: String? = null, normalizedNameOverride: String? = null) {
	// Decode and normalize the datapack name
	val baseName = datapack.name.removeSuffix(".zip")
	val decodedName = try {
		URLDecoder.decode(baseName, "UTF-8")
	} catch (_: Exception) {
		baseName // If decoding fails, use the original name
	}

	val normalizedName = normalizedNameOverride ?: decodedName
		.replace(".", "-")
		.replace(Regex("[^a-zA-Z0-9_-]"), "")

	val packageName = packageNameOverride
		?: "kore.dependencies.${normalizedName.lowercase().replace(Regex("[^a-z0-9]"), "")}"

	val datapackObjectName = normalizedName
		.replace(".", "-")
		.split(Regex("[_-]"))
		.joinToString("") { word ->
			word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
		}

	// Collect all namespaces from functions and resources
	val functionNamespaces = datapack.functions.map { it.id.substringBefore(":") }.toSet()
	val resourceNamespaces = datapack.resources.values.flatten().map { it.id.substringBefore(":") }.toSet()
	val allNamespaces = (functionNamespaces + resourceNamespaces).sorted()

	val separator = "/"
	val startTime = System.currentTimeMillis()

	val fileSpec = FileSpec.builder(packageName, datapackObjectName)
		.addFileComment("Automatically generated from datapack: %S", datapack.name)
		.apply {
			addImport("io.github.ayfri.kore.arguments", "Argument")
			addImport("io.github.ayfri.kore.arguments.types.resources", "FunctionArgument")
			addImport("kotlinx.serialization", "Serializable")

			val dataObject = TypeSpec.objectBuilder(datapackObjectName)
				.addModifiers(KModifier.DATA)
				.addProperty(
					PropertySpec.builder("PATH", String::class)
						.addModifiers(KModifier.CONST)
						.initializer("%S", datapack.path.toString().replace("\\", "/"))
						.build()
				)

			// Add pack metadata
			datapack.pack?.let { packMeta ->
				addImport("io.github.ayfri.kore.pack", "PackSection", "packFormat")
				addImport("io.github.ayfri.kore.arguments.chatcomponents", "textComponent")

				dataObject.addProperty(generatePackProperty(packMeta))
			}

			// If single namespace, generate content directly in the main object
			if (allNamespaces.size == 1) {
				val namespace = allNamespaces.first()

				dataObject.addProperty(
					PropertySpec.builder("NAMESPACE", String::class)
						.addModifiers(KModifier.CONST)
						.initializer("%S", namespace)
						.build()
				)

				// Add Functions
				if (datapack.functions.isNotEmpty()) {
					val hasNestedFunctions = datapack.functions.any { separator in it.id.substringAfter(":") }
					dataObject.addType(
						if (hasNestedFunctions) {
							generateFunctionsEnumTree(datapack.functions, separator, packageName, datapackObjectName, namespace)
						} else {
							generateSimpleFunctionsEnum(datapack.functions, namespace)
						}
					)
				}

				// Add Resources, Tags, and Worldgen
				val worldgenResources = mutableListOf<Resource>()
				val tagResources = mutableListOf<Resource>()

				datapack.resources.forEach { (resourceType, resources) ->
					when {
						resourceType.startsWith("worldgen/") -> worldgenResources.addAll(resources)
						resourceType.startsWith("tags/") -> tagResources.addAll(resources)
						else -> {
							val typeInfo = getResourceTypeInfo(resourceType)
							addImport(typeInfo.argumentPackage, typeInfo.argumentInterface)

							val hasNestedResources = resources.any { separator in it.id.substringAfter(":") }
							dataObject.addType(
								if (hasNestedResources) {
									generateResourceEnumTree(resources, typeInfo, separator, packageName, datapackObjectName, namespace)
								} else {
									generateSimpleResourceEnum(resources, typeInfo, namespace)
								}
							)
						}
					}
				}

				if (worldgenResources.isNotEmpty()) {
					dataObject.addType(generateWorldgenObject(worldgenResources, namespace))
				}

				if (tagResources.isNotEmpty()) {
					addImport("io.github.ayfri.kore.arguments.types.resources", "TagArgument")
					dataObject.addType(generateTagsObject(tagResources, namespace, packageName, datapackObjectName))
				}
			} else {
				// Multiple namespaces - create intermediate objects for each namespace
				allNamespaces.forEach { namespace ->
					val namespaceObjectName = namespace.pascalCase()
					val namespaceObject = TypeSpec.objectBuilder(namespaceObjectName)
						.addModifiers(KModifier.DATA)
						.addProperty(
							PropertySpec.builder("NAMESPACE", String::class)
								.addModifiers(KModifier.CONST)
								.initializer("%S", namespace)
								.build()
						)

					// Filter functions for this namespace
					val namespaceFunctions = datapack.functions.filter { it.id.startsWith("$namespace:") }
					if (namespaceFunctions.isNotEmpty()) {
						val hasNestedFunctions = namespaceFunctions.any { separator in it.id.substringAfter(":") }
						namespaceObject.addType(
							if (hasNestedFunctions) {
								generateFunctionsEnumTree(namespaceFunctions, separator, packageName, "$datapackObjectName.$namespaceObjectName", namespace)
							} else {
								generateSimpleFunctionsEnum(namespaceFunctions, namespace)
							}
						)
					}

					// Filter resources for this namespace
					val worldgenResources = mutableListOf<Resource>()
					val tagResources = mutableListOf<Resource>()
					datapack.resources.forEach { (resourceType, resources) ->
						val namespaceResources = resources.filter { it.id.startsWith("$namespace:") }
						if (namespaceResources.isEmpty()) return@forEach

						when {
							resourceType.startsWith("worldgen/") -> worldgenResources.addAll(namespaceResources)
							resourceType.startsWith("tags/") -> tagResources.addAll(namespaceResources)
							else -> {
								val typeInfo = getResourceTypeInfo(resourceType)
								addImport(typeInfo.argumentPackage, typeInfo.argumentInterface)

								val hasNestedResources = namespaceResources.any { separator in it.id.substringAfter(":") }
								namespaceObject.addType(
									if (hasNestedResources) {
										generateResourceEnumTree(namespaceResources, typeInfo, separator, packageName, "$datapackObjectName.$namespaceObjectName", namespace)
									} else {
										generateSimpleResourceEnum(namespaceResources, typeInfo, namespace)
									}
								)
							}
						}
					}

					if (worldgenResources.isNotEmpty()) {
						namespaceObject.addType(generateWorldgenObject(worldgenResources, namespace))
					}

					if (tagResources.isNotEmpty()) {
						addImport("io.github.ayfri.kore.arguments.types.resources", "TagArgument")
						namespaceObject.addType(generateTagsObject(tagResources, namespace, packageName, "$datapackObjectName.$namespaceObjectName"))
					}

					dataObject.addType(namespaceObject.build())
				}
			}

			addType(dataObject.build())
		}
		.build()

	Files.createDirectories(outputDir)
	val targetFile = outputDir.resolve("$datapackObjectName.kt").absolute()
	Files.writeString(targetFile, fileSpec.toString())

	val elapsedTime = System.currentTimeMillis() - startTime
	println("Generated bindings '$datapackObjectName' in ${elapsedTime}ms in: $targetFile")
}
