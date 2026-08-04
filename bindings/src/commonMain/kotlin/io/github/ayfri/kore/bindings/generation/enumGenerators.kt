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
private fun createNamespaceProperty(
	namespace: String,
	isConstant: Boolean = false,
	useGetter: Boolean = false,
): KtPropertySpec {
	val modifiers = buildSet {
		if (isConstant) add(KtModifier.CONST)
		add(KtModifier.OVERRIDE)
	}
	val initializer = if (isConstant) kotlinStringLiteral(namespace) else "NAMESPACE"
	return KtPropertySpec(
		name = "namespace",
		type = stringRef,
		modifiers = modifiers,
		initializer = initializer.takeUnless { useGetter },
		getter = "get() = $initializer".takeIf { useGetter },
	)
}

/** Derives `name` from the overridden `asId()` instead of storing it separately, so both stay in sync. */
private fun createNameProperty() = KtPropertySpec(
	name = "name",
	type = stringRef,
	modifiers = setOf(KtModifier.OVERRIDE),
	getter = "get() = asId().substringAfter(\":\").substringAfterLast(\"/\")",
)

private fun createFunctionDirectoryProperty() = KtPropertySpec(
	name = "directory",
	type = stringRef,
	modifiers = setOf(KtModifier.OVERRIDE),
	mutable = true,
	getter = "get() = asId().substringAfter(\":\").substringBeforeLast(\"/\", \"\")",
	setter = $$"set(value) = error(\"Generated function bindings are immutable; cannot set directory to '$value'\")",
)

private fun createMappedAsIdFunction(entries: List<Pair<String, String>>, idPrefix: String = "") = KtFunSpec(
	name = "asId",
	modifiers = setOf(KtModifier.OVERRIDE),
	returnType = stringRef,
	statements = buildList {
		add("return when (this) {")
		entries.forEach { (enumName, path) ->
			add($$"\t$$enumName -> \"$$idPrefix$NAMESPACE:$$path\"")
		}
		add("}")
	},
)

/**
 * Generates a simple enum for functions when there are no nested directories.
 */
fun generateSimpleFunctionsEnum(functions: List<Function>, namespace: String): KtTypeSpec {
	val nameAllocator = KotlinNameAllocator()
	val entriesById = functions.sortedBy(Function::id).associate { function ->
		function.id to nameAllocator.allocate(function.id.substringAfter(":").kotlinEnumName(), "_FUNCTION")
	}
	val entries = functions.map { function ->
		entriesById.getValue(function.id) to function.id.substringAfter(":")
	}

	return KtTypeSpec(
		kind = KtTypeKind.ENUM,
		name = "Functions",
		annotations = listOf(createSerializableAnnotation()),
		superinterfaces = listOf(functionArgumentRef),
		// `name` isn't overridden: `Enum.name` is final and already satisfies FunctionArgument's abstract `name`.
		properties = listOf(createNamespaceProperty(namespace), createFunctionDirectoryProperty()),
		functions = listOf(createMappedAsIdFunction(entries)),
		enumConstants = entries.map { it.first },
	)
}

/**
 * Generates a simple enum for resources when there are no nested directories.
 */
fun generateSimpleResourceEnum(resources: List<Resource>, typeInfo: ResourceTypeInfo, namespace: String): KtTypeSpec {
	val nameAllocator = KotlinNameAllocator()
	val entries = resources.sortedBy(Resource::id).map { resource ->
		nameAllocator.allocate(resource.id.substringAfter(":").kotlinEnumName(), "_RESOURCE") to resource.id.substringAfter(":")
	}

	return KtTypeSpec(
		kind = KtTypeKind.ENUM,
		name = typeInfo.pluralName,
		annotations = listOf(createSerializableAnnotation()),
		superinterfaces = listOf(KtRef(typeInfo.argumentPackage, typeInfo.argumentInterface)),
		properties = listOf(createNamespaceProperty(namespace)),
		functions = listOf(createMappedAsIdFunction(entries)),
		enumConstants = entries.map { it.first },
	)
}

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
	val functionPaths = functions.map { it.id.substringAfter(":") }
	sealedInterface.properties += createNamespaceProperty(namespace, useGetter = true)
	sealedInterface.properties += createNameProperty()
	sealedInterface.properties += createFunctionDirectoryProperty()

	// Allocate directory names before function names so a directory keeps the concise name when
	// `foo.mcfunction` and `foo/...` coexist. The function then becomes `FooFunction`.
	val directories = buildSet {
		functionPaths.forEach { path ->
			val segments = path.split(separator)
			for (index in 1 until segments.size) add(segments.take(index).joinToString(separator))
		}
	}
	val scopeAllocators = mutableMapOf<String, KotlinNameAllocator>()
	fun scopeAllocator(path: String) = scopeAllocators.getOrPut(path) { KotlinNameAllocator() }
	fun parentPath(path: String) = path.substringBeforeLast(separator, "")

	val directoryNames = mutableMapOf<String, String>()
	directories.sortedWith(compareBy<String>({ it.count { char -> char.toString() == separator } }, { it })).forEach { path ->
		val preferredName = path.substringAfterLast(separator).kotlinTypeName()
		directoryNames[path] = scopeAllocator(parentPath(path)).allocate(preferredName, "Group")
	}

	val functionNames = mutableMapOf<String, String>()
	functions.sortedBy(Function::id).forEach { function ->
		val path = function.id.substringAfter(":")
		val parent = parentPath(path)
		val isTopLevel = parent.isEmpty()
		val preferredName = if (isTopLevel) {
			path.kotlinTypeName()
		} else {
			path.substringAfterLast(separator).kotlinEnumName()
		}
		val collisionSuffix = if (isTopLevel) "Function" else "_FUNCTION"
		functionNames[function.id] = scopeAllocator(parent).allocate(preferredName, collisionSuffix)
	}

	data class FunctionEnumGroup(
		val node: MutableTypeNode,
		val entries: MutableList<Pair<String, String>> = mutableListOf(),
	)

	val rootFunctions = mutableListOf<KtTypeSpec>()
	val functionGroups = mutableMapOf<String, FunctionEnumGroup>()
	functions.forEach { function ->
		val path = function.id.substringAfter(":")
		val parent = parentPath(path)
		val kotlinName = functionNames.getValue(function.id)

		if (parent.isEmpty()) {
			rootFunctions += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = kotlinName,
				modifiers = setOf(KtModifier.DATA),
				superinterfaces = listOf(selfRef),
				functions = listOf(
					KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"\$NAMESPACE:$path\""),
					)
				),
			)
		} else {
			val group = functionGroups.getOrPut(parent) {
				FunctionEnumGroup(
					MutableTypeNode(
						kind = KtTypeKind.ENUM,
						name = directoryNames.getValue(parent),
						annotations = listOf(createSerializableAnnotation()),
						superinterfaces = listOf(selfRef),
					)
				)
			}
			group.node.enumConstants += kotlinName
			group.entries += kotlinName to path
		}
	}
	functionGroups.values.forEach { group -> group.node.functions += createMappedAsIdFunction(group.entries) }

	// A directory with direct functions is an enum; a directory used only for nesting is a plain
	// container object and must not pretend to be a callable function itself.
	val directoryNodes = directories.associateWithTo(mutableMapOf()) { path ->
		functionGroups[path]?.node ?: MutableTypeNode(
			kind = KtTypeKind.OBJECT,
			name = directoryNames.getValue(path),
			modifiers = setOf(KtModifier.DATA),
		)
	}
	directories.sortedWith(compareByDescending<String> { it.count { char -> char.toString() == separator } }.thenBy { it })
		.forEach { path ->
			val parent = parentPath(path)
			if (parent.isNotEmpty()) directoryNodes.getValue(parent).nestedTypes += directoryNodes.getValue(path).build()
		}

	directories.filter { parentPath(it).isEmpty() }.sorted().forEach { path ->
		sealedInterface.nestedTypes += directoryNodes.getValue(path).build()
	}
	sealedInterface.nestedTypes += rootFunctions

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
	val resourcePaths = resources.map { it.id.substringAfter(":") }
	sealedInterface.properties += createNamespaceProperty(namespace, useGetter = true)
	sealedInterface.properties += createNameProperty()

	val directories = buildSet {
		resourcePaths.forEach { path ->
			val segments = path.split(separator)
			for (index in 1 until segments.size) add(segments.take(index).joinToString(separator))
		}
	}
	val scopeAllocators = mutableMapOf<String, KotlinNameAllocator>()
	fun scopeAllocator(path: String) = scopeAllocators.getOrPut(path) { KotlinNameAllocator() }
	fun parentPath(path: String) = path.substringBeforeLast(separator, "")

	val directoryNames = mutableMapOf<String, String>()
	directories.sortedWith(compareBy<String>({ it.count { char -> char.toString() == separator } }, { it })).forEach { path ->
		val preferredName = path.substringAfterLast(separator).kotlinTypeName()
		directoryNames[path] = scopeAllocator(parentPath(path)).allocate(preferredName, "Group")
	}

	val resourceNames = mutableMapOf<String, String>()
	resources.sortedBy(Resource::id).forEach { resource ->
		val path = resource.id.substringAfter(":")
		val parent = parentPath(path)
		val isTopLevel = parent.isEmpty()
		val preferredName = if (isTopLevel) path.kotlinTypeName() else path.substringAfterLast(separator).kotlinEnumName()
		val collisionSuffix = if (isTopLevel) "Resource" else "_RESOURCE"
		resourceNames[resource.id] = scopeAllocator(parent).allocate(preferredName, collisionSuffix)
	}

	data class ResourceEnumGroup(
		val node: MutableTypeNode,
		val entries: MutableList<Pair<String, String>> = mutableListOf(),
	)

	val rootResources = mutableListOf<KtTypeSpec>()
	val resourceGroups = mutableMapOf<String, ResourceEnumGroup>()
	resources.forEach { resource ->
		val path = resource.id.substringAfter(":")
		val parent = parentPath(path)
		val kotlinName = resourceNames.getValue(resource.id)

		if (parent.isEmpty()) {
			rootResources += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = kotlinName,
				modifiers = setOf(KtModifier.DATA),
				superinterfaces = listOf(selfRef),
				functions = listOf(
					KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"\$NAMESPACE:$path\""),
					)
				),
			)
		} else {
			val group = resourceGroups.getOrPut(parent) {
				ResourceEnumGroup(
					MutableTypeNode(
						kind = KtTypeKind.ENUM,
						name = directoryNames.getValue(parent),
						annotations = listOf(createSerializableAnnotation()),
						superinterfaces = listOf(selfRef),
					)
				)
			}
			group.node.enumConstants += kotlinName
			group.entries += kotlinName to path
		}
	}
	resourceGroups.values.forEach { group -> group.node.functions += createMappedAsIdFunction(group.entries) }

	// A directory with direct resources is an enum; one used only for nesting stays a plain, non-implementing container.
	val directoryNodes = directories.associateWithTo(mutableMapOf()) { path ->
		resourceGroups[path]?.node ?: MutableTypeNode(
			kind = KtTypeKind.OBJECT,
			name = directoryNames.getValue(path),
			modifiers = setOf(KtModifier.DATA),
		)
	}
	directories.sortedWith(compareByDescending<String> { it.count { char -> char.toString() == separator } }.thenBy { it })
		.forEach { path ->
			val parent = parentPath(path)
			if (parent.isNotEmpty()) directoryNodes.getValue(parent).nestedTypes += directoryNodes.getValue(path).build()
		}

	directories.filter { parentPath(it).isEmpty() }.sorted().forEach { path ->
		sealedInterface.nestedTypes += directoryNodes.getValue(path).build()
	}
	sealedInterface.nestedTypes += rootResources

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
	tagsInterface.properties += createNamespaceProperty(namespace, useGetter = true)

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
		categoryInterface.properties += createNamespaceProperty("", useGetter = true)

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

		val nameAllocator = KotlinNameAllocator()
		val entries = resources.sortedBy(Resource::id).map { resource ->
			nameAllocator.allocate(resource.id.substringAfter(":").kotlinEnumName(), "_TAG") to resource.id.substringAfter(":")
		}
		enumBuilder.enumConstants += entries.map { it.first }
		enumBuilder.functions += createMappedAsIdFunction(entries, idPrefix = "#")

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
	val separator = "/"
	val resourcePaths = resources.map { it.id.substringAfter(":") }
	fun parentPath(path: String) = path.substringBeforeLast(separator, "")

	val directories = buildSet {
		resourcePaths.forEach { path ->
			val segments = path.split(separator)
			for (index in 1 until segments.size) add(segments.take(index).joinToString(separator))
		}
	}
	val scopeAllocators = mutableMapOf<String, KotlinNameAllocator>()
	fun scopeAllocator(path: String) = scopeAllocators.getOrPut(path) { KotlinNameAllocator() }

	val directoryNames = mutableMapOf<String, String>()
	directories.sortedWith(compareBy<String>({ it.count { char -> char == '/' } }, { it })).forEach { path ->
		val preferredName = path.substringAfterLast(separator).kotlinTypeName()
		directoryNames[path] = scopeAllocator(parentPath(path)).allocate(preferredName, "Group")
	}

	val tagNames = mutableMapOf<String, String>()
	resources.sortedBy(Resource::id).forEach { resource ->
		val path = resource.id.substringAfter(":")
		val parent = parentPath(path)
		val isTopLevel = parent.isEmpty()
		val preferredName = if (isTopLevel) path.kotlinTypeName() else path.substringAfterLast(separator).kotlinEnumName()
		val collisionSuffix = if (isTopLevel) "Tag" else "_TAG"
		tagNames[resource.id] = scopeAllocator(parent).allocate(preferredName, collisionSuffix)
	}

	data class TagEnumGroup(
		val node: MutableTypeNode,
		val entries: MutableList<Pair<String, String>> = mutableListOf(),
	)

	val rootTags = mutableListOf<KtTypeSpec>()
	val tagGroups = mutableMapOf<String, TagEnumGroup>()
	resources.forEach { resource ->
		val path = resource.id.substringAfter(":")
		val parent = parentPath(path)
		val kotlinName = tagNames.getValue(resource.id)

		if (parent.isEmpty()) {
			// Self-contained: it doesn't implement the category sealed interface, so it can't inherit `name`/`namespace`.
			rootTags += KtTypeSpec(
				kind = KtTypeKind.OBJECT,
				name = kotlinName,
				modifiers = setOf(KtModifier.DATA),
				superinterfaces = buildSupertypes(tagArgumentInterface),
				properties = listOf(
					KtPropertySpec(name = "namespace", type = stringRef, modifiers = setOf(KtModifier.OVERRIDE), initializer = "NAMESPACE"),
					KtPropertySpec(
						name = "name",
						type = stringRef,
						modifiers = setOf(KtModifier.OVERRIDE),
						initializer = kotlinStringLiteral(path.substringAfterLast(separator)),
					),
				),
				functions = listOf(
					KtFunSpec(
						name = "asId",
						modifiers = setOf(KtModifier.OVERRIDE),
						returnType = stringRef,
						statements = listOf("return \"#\$NAMESPACE:$path\""),
					)
				),
			)
		} else {
			val group = tagGroups.getOrPut(parent) {
				TagEnumGroup(
					MutableTypeNode(
						kind = KtTypeKind.ENUM,
						name = directoryNames.getValue(parent),
						annotations = listOf(createSerializableAnnotation()),
						superinterfaces = buildSupertypes(tagArgumentInterface),
					).apply {
						properties += createNamespaceProperty("")
					}
				)
			}
			group.node.enumConstants += kotlinName
			group.entries += kotlinName to path
		}
	}
	tagGroups.values.forEach { group -> group.node.functions += createMappedAsIdFunction(group.entries, idPrefix = "#") }

	// A directory with direct tags is an enum; one used only for nesting stays a plain, non-implementing container.
	val directoryNodes = directories.associateWithTo(mutableMapOf()) { path ->
		tagGroups[path]?.node ?: MutableTypeNode(
			kind = KtTypeKind.OBJECT,
			name = directoryNames.getValue(path),
			modifiers = setOf(KtModifier.DATA),
		)
	}
	directories.sortedWith(compareByDescending<String> { it.count { char -> char == '/' } }.thenBy { it }).forEach { path ->
		val parent = parentPath(path)
		if (parent.isNotEmpty()) directoryNodes.getValue(parent).nestedTypes += directoryNodes.getValue(path).build()
	}

	directories.filter { parentPath(it).isEmpty() }.sorted().forEach { path ->
		parentBuilder.nestedTypes += directoryNodes.getValue(path).build()
	}
	parentBuilder.nestedTypes += rootTags
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
