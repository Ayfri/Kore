package io.github.ayfri.kore.bindings.generation

import io.github.ayfri.kore.bindings.Function
import io.github.ayfri.kore.bindings.Resource
import io.github.ayfri.kore.bindings.generation.codegen.KtAnnotationSpec
import io.github.ayfri.kore.bindings.generation.codegen.KtFunSpec
import io.github.ayfri.kore.bindings.generation.codegen.KtModifier
import io.github.ayfri.kore.bindings.generation.codegen.KtPropertySpec
import io.github.ayfri.kore.bindings.generation.codegen.KtRef
import io.github.ayfri.kore.bindings.generation.codegen.KtTypeKind
import io.github.ayfri.kore.bindings.generation.codegen.KtTypeSpec
import io.github.ayfri.kore.bindings.generation.codegen.kotlinStringLiteral

private val stringRef = KtRef("", "String")
private val functionArgumentRef = KtRef("io.github.ayfri.kore.arguments.types.resources", "FunctionArgument")
private val tagArgumentRef = KtRef("io.github.ayfri.kore.arguments.types.resources", "TagArgument")
private val serializableRef = KtRef("kotlinx.serialization", "Serializable")
private val argumentSerializerRef = KtRef("io.github.ayfri.kore.arguments.Argument", "ArgumentSerializer")

/** A mutable, `TypeSpec.Builder`-shaped accumulator - collapsed to an immutable [KtTypeSpec] via [build]. */
private class MutableTypeNode(
	val kind: KtTypeKind,
	val name: String,
	val modifiers: Set<KtModifier> = emptySet(),
	val annotations: List<KtAnnotationSpec> = emptyList(),
	val superinterfaces: List<KtRef> = emptyList(),
) {
	val properties = mutableListOf<KtPropertySpec>()
	val functions = mutableListOf<KtFunSpec>()
	val enumConstants = mutableListOf<String>()
	val nestedTypes = mutableListOf<KtTypeSpec>()

	fun build() = KtTypeSpec(kind, name, modifiers, annotations, superinterfaces, properties, functions, enumConstants, nestedTypes)
}

/**
 * Helper function to create a serializable annotation for enums.
 */
private fun createSerializableAnnotation() = KtAnnotationSpec(
	type = serializableRef,
	member = "with = ${argumentSerializerRef.simpleName}::class",
	referencedTypes = listOf(argumentSerializerRef),
)

/**
 * Helper function to create a namespace property.
 */
private fun createNamespaceProperty(namespace: String, isConstant: Boolean = false): KtPropertySpec {
	val modifiers = buildSet {
		if (isConstant) add(KtModifier.CONST)
		add(KtModifier.OVERRIDE)
	}
	val initializer = if (isConstant) kotlinStringLiteral(namespace) else "NAMESPACE"
	return KtPropertySpec(name = "namespace", type = stringRef, modifiers = modifiers, initializer = initializer)
}

/**
 * Helper function to create an asId() function for simple resources.
 */
private fun createAsIdFunction(pathPrefix: String = "", tagPrefix: String = "") = KtFunSpec(
	name = "asId",
	modifiers = setOf(KtModifier.OVERRIDE),
	returnType = stringRef,
	statements = listOf("return \"$tagPrefix\$NAMESPACE:$pathPrefix\${name.lowercase()}\""),
)

/**
 * Generates a simple enum for functions when there are no nested directories.
 */
fun generateSimpleFunctionsEnum(functions: List<Function>, namespace: String) = KtTypeSpec(
	kind = KtTypeKind.ENUM,
	name = "Functions",
	annotations = listOf(createSerializableAnnotation()),
	superinterfaces = listOf(functionArgumentRef),
	properties = listOf(createNamespaceProperty(namespace)),
	functions = listOf(createAsIdFunction()),
	enumConstants = functions.map { function ->
		function.id.substringAfter(":").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()
	},
)

/**
 * Generates a simple enum for resources when there are no nested directories.
 */
fun generateSimpleResourceEnum(resources: List<Resource>, typeInfo: ResourceTypeInfo, namespace: String) = KtTypeSpec(
	kind = KtTypeKind.ENUM,
	name = typeInfo.pluralName,
	annotations = listOf(createSerializableAnnotation()),
	superinterfaces = listOf(KtRef(typeInfo.argumentPackage, typeInfo.argumentInterface)),
	properties = listOf(createNamespaceProperty(namespace)),
	functions = listOf(createAsIdFunction()),
	enumConstants = resources.map { resource ->
		resource.id.substringAfter(":").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()
	},
)

/**
 * Generates a nested enum tree for functions with subdirectories.
 */
fun generateFunctionsEnumTree(
	functions: List<Function>,
	separator: String,
	packageName: String,
	datapackObjectName: String,
	namespace: String,
): KtTypeSpec {
	val selfRef = KtRef(packageName, "$datapackObjectName.Functions")

	val sealedInterface = MutableTypeNode(
		kind = KtTypeKind.INTERFACE,
		name = "Functions",
		modifiers = setOf(KtModifier.SEALED),
		superinterfaces = listOf(functionArgumentRef),
	)
	sealedInterface.properties += KtPropertySpec(
		name = "namespace",
		type = stringRef,
		modifiers = setOf(KtModifier.OVERRIDE),
		initializer = "NAMESPACE",
	)

	// Group functions by depth and parent
	val functionPaths = functions.map { it.id.substringAfter(":") }
	val maxDepth = functionPaths.maxOfOrNull { it.count { c -> c.toString() == separator } } ?: 0
	val typeBuilders = MutableList(maxDepth + 1) { mutableMapOf<String, MutableTypeNode>() }

	// Build enums from deepest to shallowest
	for (function in functions) {
		val path = function.id.substringAfter(":")
		val depth = path.count { it.toString() == separator }
		val enumValue = path.substringAfterLast(separator).replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()

		if (depth == 0) {
			// Top-level function - create data object
			val objectName = path.pascalCase()
			sealedInterface.nestedTypes += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = objectName,
				modifiers = setOf(KtModifier.DATA),
				superinterfaces = listOf(selfRef),
				functions = listOf(
					KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"\$NAMESPACE:${path.lowercase()}\""),
					)
				),
			)
		} else {
			// Get or create enum for this parent
			val parent = path.substringBeforeLast(separator)
			val enumName = parent.substringAfterLast(separator).pascalCase()

			val enumBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				val node = MutableTypeNode(
					kind = KtTypeKind.ENUM,
					name = enumName,
					annotations = listOf(createSerializableAnnotation()),
					superinterfaces = listOf(selfRef),
				)
				node.functions += KtFunSpec(
					name = "asId",
					modifiers = setOf(KtModifier.OVERRIDE),
					returnType = stringRef,
					statements = listOf("return \"\$NAMESPACE:$parent/\${name.lowercase()}\""),
				)
				node
			}

			enumBuilder.enumConstants += enumValue
		}
	}

	// Nest enums from deepest to shallowest
	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast(separator)
			val objectName = parent.substringAfterLast(separator).pascalCase()

			val parentBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				val node = MutableTypeNode(
					kind = KtTypeKind.OBJECT,
					name = objectName,
					modifiers = setOf(KtModifier.DATA),
					superinterfaces = listOf(selfRef),
				)
				node.functions += KtFunSpec(
					name = "asId",
					modifiers = setOf(KtModifier.OVERRIDE),
					returnType = stringRef,
					statements = listOf("return \"\$NAMESPACE:${parent.lowercase()}\""),
				)
				node
			}

			parentBuilder.nestedTypes += typeBuilder.build()
		}
	}

	// Add top-level enums/objects to sealed interface
	typeBuilders.firstOrNull()?.forEach { (_, builder) -> sealedInterface.nestedTypes += builder.build() }

	return sealedInterface.build()
}

/**
 * Generates a nested enum tree for resources with subdirectories.
 */
fun generateResourceEnumTree(
	resources: List<Resource>,
	typeInfo: ResourceTypeInfo,
	separator: String,
	packageName: String,
	datapackObjectName: String,
	namespace: String,
): KtTypeSpec {
	val supertypeRef = KtRef(typeInfo.argumentPackage, typeInfo.argumentInterface)
	val selfRef = KtRef(packageName, "$datapackObjectName.${typeInfo.pluralName}")

	val sealedInterface = MutableTypeNode(
		kind = KtTypeKind.INTERFACE,
		name = typeInfo.pluralName,
		modifiers = setOf(KtModifier.SEALED),
		superinterfaces = listOf(supertypeRef),
	)
	sealedInterface.properties += KtPropertySpec(
		name = "namespace",
		type = stringRef,
		modifiers = setOf(KtModifier.OVERRIDE),
		initializer = "NAMESPACE",
	)

	// Group resources by depth and parent
	val resourcePaths = resources.map { it.id.substringAfter(":") }
	val maxDepth = resourcePaths.maxOfOrNull { it.count { c -> c.toString() == separator } } ?: 0
	val typeBuilders = MutableList(maxDepth + 1) { mutableMapOf<String, MutableTypeNode>() }

	// Build enums from deepest to shallowest
	for (resource in resources) {
		val path = resource.id.substringAfter(":")
		val depth = path.count { it.toString() == separator }
		val enumValue = path.substringAfterLast(separator).replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()

		if (depth == 0) {
			// Top-level resource - create data object
			val objectName = path.pascalCase()
			sealedInterface.nestedTypes += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = objectName,
				modifiers = setOf(KtModifier.DATA),
				superinterfaces = listOf(selfRef),
				functions = listOf(
					KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"\$NAMESPACE:${path.lowercase()}\""),
					)
				),
			)
		} else {
			// Get or create enum for this parent
			val parent = path.substringBeforeLast(separator)
			val enumName = parent.substringAfterLast(separator).pascalCase()

			val enumBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				MutableTypeNode(
					kind = KtTypeKind.ENUM,
					name = enumName,
					annotations = listOf(createSerializableAnnotation()),
					superinterfaces = listOf(selfRef),
				).apply {
					functions += KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"\$NAMESPACE:$parent/\${name.lowercase()}\""),
					)
				}
			}

			enumBuilder.enumConstants += enumValue
		}
	}

	// Nest enums from deepest to shallowest
	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast(separator)
			val objectName = parent.substringAfterLast(separator).pascalCase()

			val parentBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				MutableTypeNode(
					kind = KtTypeKind.OBJECT,
					name = objectName,
					modifiers = setOf(KtModifier.DATA),
					superinterfaces = listOf(selfRef),
				).apply {
					functions += KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"\$NAMESPACE:${parent.lowercase()}\""),
					)
				}
			}

			parentBuilder.nestedTypes += typeBuilder.build()
		}
	}

	// Add top-level enums/objects to sealed interface
	typeBuilders.firstOrNull()?.forEach { (_, builder) -> sealedInterface.nestedTypes += builder.build() }

	return sealedInterface.build()
}

/**
 * Generates a Worldgen object containing all worldgen resource enums.
 */
fun generateWorldgenObject(resources: List<Resource>, namespace: String): KtTypeSpec {
	val worldgenObject = MutableTypeNode(
		kind = KtTypeKind.OBJECT,
		name = "Worldgen",
		modifiers = setOf(KtModifier.DATA),
	)

	// Group worldgen resources by category (e.g., "biome", "dimension", etc.)
	val resourcesByCategory = resources.groupBy { it.type.substringAfter("worldgen/") }

	resourcesByCategory.forEach { (category, categoryResources) ->
		val typeInfo = getResourceTypeInfo(category)
		val hasNestedResources = categoryResources.any { "/" in it.id.substringAfter(":") }

		worldgenObject.nestedTypes += if (hasNestedResources) {
			generateResourceEnumTree(categoryResources, typeInfo, "/", "", "Worldgen", namespace)
		} else {
			generateSimpleResourceEnum(categoryResources, typeInfo, namespace)
		}
	}

	return worldgenObject.build()
}

/**
 * Generates a Tags sealed interface with nested enums for each tag type.
 * Similar to the main Kore library's Tags structure.
 */
fun generateTagsObject(resources: List<Resource>, namespace: String, packageName: String, datapackObjectName: String): KtTypeSpec {
	val tagsInterface = MutableTypeNode(
		kind = KtTypeKind.INTERFACE,
		name = "Tags",
		modifiers = setOf(KtModifier.SEALED),
		superinterfaces = listOf(tagArgumentRef),
	)
	tagsInterface.properties += createNamespaceProperty(namespace)

	// Group tags by category (e.g., "block", "item", "enchantment", "worldgen/biome", etc.)
	val tagsByCategory = resources.groupBy { it.type.substringAfter("tags/") }

	// Separate worldgen tags from regular tags
	val regularTags = tagsByCategory.filterKeys { !it.startsWith("worldgen/") }
	val worldgenTags = tagsByCategory.filterKeys { it.startsWith("worldgen/") }

	// Generate regular tag enums
	regularTags.toList().sortedBy { (category, _) -> category }.forEach { (category, categoryResources) ->
		tagsInterface.nestedTypes += generateTagEnum(category, categoryResources, packageName, datapackObjectName)
	}

	// Generate worldgen tags if any
	if (worldgenTags.isNotEmpty()) {
		val worldgenObject = MutableTypeNode(kind = KtTypeKind.OBJECT, name = "Worldgen")
		worldgenTags.toList().sortedBy { (category, _) -> category }.forEach { (category, categoryResources) ->
			val subCategory = category.substringAfter("worldgen/")
			worldgenObject.nestedTypes += generateTagEnum(subCategory, categoryResources, packageName, datapackObjectName, isWorldgen = true)
		}
		tagsInterface.nestedTypes += worldgenObject.build()
	}

	return tagsInterface.build()
}

/**
 * Generates a tag enum for a specific category (e.g., "block", "item", "enchantment").
 */
private fun generateTagEnum(
	category: String,
	resources: List<Resource>,
	packageName: String,
	datapackObjectName: String,
	isWorldgen: Boolean = false,
): KtTypeSpec {
	val enumName = category.split("_").joinToString("") { it.pascalCase() }
	val hasNestedTags = resources.any { "/" in it.id.substringAfter(":") }

	// Get the appropriate tag argument interface
	val tagArgumentInterface = getTagArgumentInterface(category, isWorldgen)

	return if (hasNestedTags) {
		// Create sealed interface for nested tags
		val categoryInterface = MutableTypeNode(
			kind = KtTypeKind.INTERFACE,
			name = enumName,
			modifiers = setOf(KtModifier.SEALED),
			superinterfaces = listOf(KtRef(packageName, "$datapackObjectName.Tags")) + buildSupertypes(tagArgumentInterface),
		)
		categoryInterface.properties += createNamespaceProperty("")

		// Build nested enums
		buildNestedTagEnums(categoryInterface, resources, tagArgumentInterface)

		categoryInterface.build()
	} else {
		// Create simple enum for flat tags
		val enumBuilder = MutableTypeNode(
			kind = KtTypeKind.ENUM,
			name = enumName,
			annotations = listOf(createSerializableAnnotation()),
			superinterfaces = listOf(KtRef(packageName, "$datapackObjectName.Tags")) + buildSupertypes(tagArgumentInterface),
		)
		enumBuilder.properties += createNamespaceProperty("")

		resources.forEach { resource ->
			enumBuilder.enumConstants += resource.id.substringAfter(":").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()
		}

		enumBuilder.functions += createAsIdFunction(tagPrefix = "#")

		enumBuilder.build()
	}
}

/**
 * Gets the appropriate tag argument interface for a category.
 */
private fun getTagArgumentInterface(category: String, isWorldgen: Boolean): String? = when {
	isWorldgen -> when (category) {
		"biome" -> "io.github.ayfri.kore.generated.arguments.worldgen.tagged.BiomeTagArgument"
		"structure" -> "io.github.ayfri.kore.generated.arguments.worldgen.tagged.ConfiguredStructureTagArgument"
		"flat_level_generator_preset" -> "io.github.ayfri.kore.generated.arguments.worldgen.tagged.FlatLevelGeneratorPresetTagArgument"
		"world_preset" -> "io.github.ayfri.kore.generated.arguments.worldgen.tagged.WorldPresetTagArgument"
		else -> null
	}

	else -> when (category) {
		"block" -> "io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument"
		"item" -> "io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument"
		"entity_type" -> "io.github.ayfri.kore.generated.arguments.tagged.EntityTypeTagArgument"
		"fluid" -> "io.github.ayfri.kore.generated.arguments.tagged.FluidTagArgument"
		"game_event" -> "io.github.ayfri.kore.generated.arguments.tagged.GameEventTagArgument"
		"banner_pattern" -> "io.github.ayfri.kore.generated.arguments.tagged.BannerPatternTagArgument"
		"damage_type" -> "io.github.ayfri.kore.generated.arguments.tagged.DamageTypeTagArgument"
		"dialog" -> "io.github.ayfri.kore.generated.arguments.tagged.DialogTagArgument"
		"enchantment" -> "io.github.ayfri.kore.generated.arguments.tagged.EnchantmentTagArgument"
		"instrument" -> "io.github.ayfri.kore.generated.arguments.tagged.InstrumentTagArgument"
		"painting_variant" -> "io.github.ayfri.kore.generated.arguments.tagged.PaintingVariantTagArgument"
		"point_of_interest_type" -> "io.github.ayfri.kore.generated.arguments.tagged.PointOfInterestTypeTagArgument"
		else -> null
	}
}

/**
 * Builds nested tag enums for tags with subdirectories and adds them to the parent interface.
 */
private fun buildNestedTagEnums(
	parentBuilder: MutableTypeNode,
	resources: List<Resource>,
	tagArgumentInterface: String?,
) {
	val resourcePaths = resources.map { it.id.substringAfter(":") }
	val maxDepth = resourcePaths.maxOfOrNull { it.count { c -> c == '/' } } ?: 0
	val typeBuilders = MutableList(maxDepth + 1) { mutableMapOf<String, MutableTypeNode>() }

	// Build enums from deepest to shallowest
	for (resource in resources) {
		val path = resource.id.substringAfter(":")
		val depth = path.count { it == '/' }
		val enumValue = path.substringAfterLast("/").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()

		if (depth == 0) {
			// Top-level tag - create data object
			val objectName = path.pascalCase()
			parentBuilder.nestedTypes += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = objectName,
				modifiers = setOf(KtModifier.DATA),
				superinterfaces = buildSupertypes(tagArgumentInterface),
				functions = listOf(createAsIdFunction(tagPrefix = "#")),
			)
		} else {
			// Nested tag - add to enum
			val parent = path.substringBeforeLast("/")
			val enumName = parent.substringAfterLast("/").split("_").joinToString("") { it.pascalCase() }

			val enumBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				MutableTypeNode(
					kind = KtTypeKind.ENUM,
					name = enumName,
					annotations = listOf(createSerializableAnnotation()),
					superinterfaces = buildSupertypes(tagArgumentInterface),
				).apply {
					properties += createNamespaceProperty("")
					functions += KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"#\$NAMESPACE:$parent/\${name.lowercase()}\""),
					)
				}
			}

			enumBuilder.enumConstants += enumValue
		}
	}

	// Nest enums from deepest to shallowest
	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast("/")
			val objectName = parent.substringAfterLast("/").split("_").joinToString("") { it.pascalCase() }

			val parentNode = typeBuilders[depth - 1].getOrPut(parent) {
				MutableTypeNode(
					kind = KtTypeKind.OBJECT,
					name = objectName,
					modifiers = setOf(KtModifier.DATA),
					superinterfaces = buildSupertypes(tagArgumentInterface),
				).apply {
					functions += KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"#\$NAMESPACE:${parent.lowercase()}\""),
					)
				}
			}

			parentNode.nestedTypes += typeBuilder.build()
		}
	}

	// Add top-level enums/objects to parent
	typeBuilders.firstOrNull()?.forEach { (_, builder) -> parentBuilder.nestedTypes += builder.build() }
}

/**
 * Builds a list of supertype class references for tag types.
 */
private fun buildSupertypes(tagArgumentInterface: String?): List<KtRef> {
	if (tagArgumentInterface == null) return emptyList()
	val packageName = tagArgumentInterface.substringBeforeLast(".")
	val simpleName = tagArgumentInterface.substringAfterLast(".")
	return listOf(KtRef(packageName, simpleName))
}
