package io.github.ayfri.kore.bindings.generation

import com.squareup.kotlinpoet.*
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.TagArgument
import io.github.ayfri.kore.bindings.Function
import io.github.ayfri.kore.bindings.Resource
import kotlinx.serialization.Serializable

/**
 * Helper function to create a serializable annotation for enums.
 */
private fun createSerializableAnnotation() = AnnotationSpec.builder(Serializable::class)
	.addMember("with = %T::class", Argument.ArgumentSerializer::class.asClassName())
	.build()

/**
 * Helper function to create a namespace property.
 */
private fun createNamespaceProperty(namespace: String, isConstant: Boolean = false) = PropertySpec.builder("namespace", String::class)
	.apply {
		if (isConstant) {
			addModifiers(KModifier.CONST)
		}
		initializer(if (isConstant) "%S" else "NAMESPACE", namespace)
		addModifiers(KModifier.OVERRIDE)
	}
	.build()

/**
 * Helper function to create an asId() function for simple resources.
 */
private fun createAsIdFunction(pathPrefix: String = "", tagPrefix: String = "") = FunSpec.builder("asId")
	.addModifiers(KModifier.OVERRIDE)
	.returns(String::class)
	.addStatement("return %P", "$tagPrefix\$NAMESPACE:$pathPrefix\${name.lowercase()}")
	.build()

/**
 * Generates a simple enum for functions when there are no nested directories.
 */
fun generateSimpleFunctionsEnum(functions: List<Function>, namespace: String) = TypeSpec.enumBuilder("Functions")
	.addAnnotation(createSerializableAnnotation())
	.addSuperinterface(FunctionArgument::class)
	.addProperty(createNamespaceProperty(namespace))
	.apply {
		functions.forEach { function ->
			val enumName = function.id.substringAfter(":").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()
			addEnumConstant(enumName)
		}
		addFunction(createAsIdFunction())
	}
	.build()

/**
 * Generates a simple enum for resources when there are no nested directories.
 */
fun generateSimpleResourceEnum(resources: List<Resource>, typeInfo: ResourceTypeInfo, namespace: String) =
	TypeSpec.enumBuilder(typeInfo.pluralName)
		.addAnnotation(createSerializableAnnotation())
		.addSuperinterface(ClassName(typeInfo.argumentPackage, typeInfo.argumentInterface))
		.addProperty(createNamespaceProperty(namespace))
		.apply {
			resources.forEach { resource ->
				val enumName = resource.id.substringAfter(":").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()
				addEnumConstant(enumName)
			}
			addFunction(createAsIdFunction())
		}
		.build()

/**
 * Generates a nested enum tree for functions with subdirectories.
 */
fun generateFunctionsEnumTree(
	functions: List<Function>,
	separator: String,
	packageName: String,
	datapackObjectName: String,
	namespace: String,
): TypeSpec {
	val sealedInterface = TypeSpec.interfaceBuilder("Functions")
		.addModifiers(KModifier.SEALED)
		.addSuperinterface(FunctionArgument::class)
		.addProperty(
			PropertySpec.builder("namespace", String::class)
				.initializer("NAMESPACE")
				.addModifiers(KModifier.OVERRIDE)
				.build()
		)

	// Group functions by depth and parent
	val functionPaths = functions.map { it.id.substringAfter(":") }
	val maxDepth = functionPaths.maxOfOrNull { it.count { c -> c.toString() == separator } } ?: 0
	val typeBuilders = MutableList(maxDepth + 1) { mutableMapOf<String, TypeSpec.Builder>() }

	// Build enums from deepest to shallowest
	for (function in functions) {
		val path = function.id.substringAfter(":")
		val depth = path.count { it.toString() == separator }
		val enumValue = path.substringAfterLast(separator).replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()

		if (depth == 0) {
			// Top-level function - create data object
			val objectName = path.pascalCase()
			sealedInterface.addType(
				TypeSpec.objectBuilder(objectName)
					.addModifiers(KModifier.DATA)
					.addSuperinterface(ClassName(packageName, datapackObjectName, "Functions"))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "\$NAMESPACE:${path.lowercase()}")
							.build()
					)
					.build()
			)
		} else {
			// Get or create enum for this parent
			val parent = path.substringBeforeLast(separator)
			val enumName = parent.substringAfterLast(separator).pascalCase()

			val enumBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.enumBuilder(enumName)
					.addAnnotation(createSerializableAnnotation())
					.addSuperinterface(ClassName(packageName, datapackObjectName, "Functions"))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "\$NAMESPACE:$parent/\${name.lowercase()}")
							.build()
					)
			}

			enumBuilder.addEnumConstant(enumValue)
		}
	}

	// Nest enums from deepest to shallowest
	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast(separator)
			val objectName = parent.substringAfterLast(separator).pascalCase()

			typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.objectBuilder(objectName)
					.addModifiers(KModifier.DATA)
					.addSuperinterface(ClassName(packageName, datapackObjectName, "Functions"))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "\$NAMESPACE:${parent.lowercase()}")
							.build()
					)
			}.addType(typeBuilder.build())
		}
	}

	// Add top-level enums/objects to sealed interface
	typeBuilders.firstOrNull()?.forEach { (_, builder) ->
		sealedInterface.addType(builder.build())
	}

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
): TypeSpec {
	val sealedInterface = TypeSpec.interfaceBuilder(typeInfo.pluralName)
		.addModifiers(KModifier.SEALED)
		.addSuperinterface(ClassName(typeInfo.argumentPackage, typeInfo.argumentInterface))
		.addProperty(
			PropertySpec.builder("namespace", String::class)
				.initializer("NAMESPACE")
				.addModifiers(KModifier.OVERRIDE)
				.build()
		)

	// Group resources by depth and parent
	val resourcePaths = resources.map { it.id.substringAfter(":") }
	val maxDepth = resourcePaths.maxOfOrNull { it.count { c -> c.toString() == separator } } ?: 0
	val typeBuilders = MutableList(maxDepth + 1) { mutableMapOf<String, TypeSpec.Builder>() }

	// Build enums from deepest to shallowest
	for (resource in resources) {
		val path = resource.id.substringAfter(":")
		val depth = path.count { it.toString() == separator }
		val enumValue = path.substringAfterLast(separator).replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()

		if (depth == 0) {
			// Top-level resource - create data object
			val objectName = path.pascalCase()
			sealedInterface.addType(
				TypeSpec.objectBuilder(objectName)
					.addModifiers(KModifier.DATA)
					.addSuperinterface(ClassName(packageName, datapackObjectName, typeInfo.pluralName))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "\$NAMESPACE:${path.lowercase()}")
							.build()
					)
					.build()
			)
		} else {
			// Get or create enum for this parent
			val parent = path.substringBeforeLast(separator)
			val enumName = parent.substringAfterLast(separator).pascalCase()

			val enumBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.enumBuilder(enumName)
					.addAnnotation(createSerializableAnnotation())
					.addSuperinterface(ClassName(packageName, datapackObjectName, typeInfo.pluralName))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "\$NAMESPACE:$parent/\${name.lowercase()}")
							.build()
					)
			}

			enumBuilder.addEnumConstant(enumValue)
		}
	}

	// Nest enums from deepest to shallowest
	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast(separator)
			val objectName = parent.substringAfterLast(separator).pascalCase()

			typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.objectBuilder(objectName)
					.addModifiers(KModifier.DATA)
					.addSuperinterface(ClassName(packageName, datapackObjectName, typeInfo.pluralName))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "\$NAMESPACE:${parent.lowercase()}")
							.build()
					)
			}.addType(typeBuilder.build())
		}
	}

	// Add top-level enums/objects to sealed interface
	typeBuilders.firstOrNull()?.forEach { (_, builder) ->
		sealedInterface.addType(builder.build())
	}

	return sealedInterface.build()
}

/**
 * Generates a Worldgen object containing all worldgen resource enums.
 */
fun generateWorldgenObject(resources: List<Resource>, namespace: String): TypeSpec {
	val worldgenObject = TypeSpec.objectBuilder("Worldgen")
		.addModifiers(KModifier.DATA)

	// Group worldgen resources by category (e.g., "biome", "dimension", etc.)
	val resourcesByCategory = resources.groupBy {
		it.type.substringAfter("worldgen/")
	}

	resourcesByCategory.forEach { (category, categoryResources) ->
		val typeInfo = getResourceTypeInfo(category)
		val hasNestedResources = categoryResources.any { "/" in it.id.substringAfter(":") }

		if (hasNestedResources) {
			worldgenObject.addType(generateResourceEnumTree(categoryResources, typeInfo, "/", "", "Worldgen", namespace))
		} else {
			worldgenObject.addType(generateSimpleResourceEnum(categoryResources, typeInfo, namespace))
		}
	}

	return worldgenObject.build()
}

/**
 * Generates a Tags sealed interface with nested enums for each tag type.
 * Similar to the main Kore library's Tags structure.
 */
fun generateTagsObject(resources: List<Resource>, namespace: String, packageName: String, datapackObjectName: String): TypeSpec {
	val tagsInterface = TypeSpec.interfaceBuilder("Tags")
		.addModifiers(KModifier.SEALED)
		.addSuperinterface(TagArgument::class)
		.addProperty(createNamespaceProperty(namespace))

	// Group tags by category (e.g., "block", "item", "enchantment", "worldgen/biome", etc.)
	val tagsByCategory = resources.groupBy { it.type.substringAfter("tags/") }

	// Separate worldgen tags from regular tags
	val regularTags = tagsByCategory.filterKeys { !it.startsWith("worldgen/") }
	val worldgenTags = tagsByCategory.filterKeys { it.startsWith("worldgen/") }

	// Generate regular tag enums
	regularTags.toSortedMap().forEach { (category, categoryResources) ->
		tagsInterface.addType(generateTagEnum(category, categoryResources, packageName, datapackObjectName))
	}

	// Generate worldgen tags if any
	if (worldgenTags.isNotEmpty()) {
		val worldgenObject = TypeSpec.objectBuilder("Worldgen")
		worldgenTags.toSortedMap().forEach { (category, categoryResources) ->
			val subCategory = category.substringAfter("worldgen/")
			worldgenObject.addType(generateTagEnum(subCategory, categoryResources, packageName, datapackObjectName, isWorldgen = true))
		}
		tagsInterface.addType(worldgenObject.build())
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
): TypeSpec {
	val enumName = category.split("_").joinToString("") { it.pascalCase() }
	val hasNestedTags = resources.any { "/" in it.id.substringAfter(":") }

	// Get the appropriate tag argument interface
	val tagArgumentInterface = getTagArgumentInterface(category, isWorldgen)

	return if (hasNestedTags) {
		// Create sealed interface for nested tags
		val categoryInterface = TypeSpec.interfaceBuilder(enumName)
			.addModifiers(KModifier.SEALED)
			.addSuperinterface(ClassName(packageName, datapackObjectName, "Tags"))

		if (tagArgumentInterface != null) {
			val parts = tagArgumentInterface.split(".")
			categoryInterface.addSuperinterface(ClassName(parts.dropLast(1).joinToString("."), parts.last()))
		}

		categoryInterface.addProperty(createNamespaceProperty(""))

		// Build nested enums
		buildNestedTagEnums(categoryInterface, resources, tagArgumentInterface)

		categoryInterface.build()
	} else {
		// Create simple enum for flat tags
		val enumBuilder = TypeSpec.enumBuilder(enumName)
			.addAnnotation(createSerializableAnnotation())
			.addSuperinterface(ClassName(packageName, datapackObjectName, "Tags"))

		if (tagArgumentInterface != null) {
			val parts = tagArgumentInterface.split(".")
			enumBuilder.addSuperinterface(ClassName(parts.dropLast(1).joinToString("."), parts.last()))
		}

		enumBuilder.addProperty(createNamespaceProperty(""))

		resources.forEach { resource ->
			val tagName = resource.id.substringAfter(":").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()
			enumBuilder.addEnumConstant(tagName)
		}

		enumBuilder.addFunction(createAsIdFunction(tagPrefix = "#"))

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
	parentBuilder: TypeSpec.Builder,
	resources: List<Resource>,
	tagArgumentInterface: String?,
) {
	val resourcePaths = resources.map { it.id.substringAfter(":") }
	val maxDepth = resourcePaths.maxOfOrNull { it.count { c -> c == '/' } } ?: 0
	val typeBuilders = MutableList(maxDepth + 1) { mutableMapOf<String, TypeSpec.Builder>() }

	// Build enums from deepest to shallowest
	for (resource in resources) {
		val path = resource.id.substringAfter(":")
		val depth = path.count { it == '/' }
		val enumValue = path.substringAfterLast("/").replace(Regex("[^a-zA-Z0-9_]"), "_").snakeCase().uppercase()

		if (depth == 0) {
			// Top-level tag - create data object
			val objectName = path.pascalCase()
			parentBuilder.addType(
				TypeSpec.objectBuilder(objectName)
					.addModifiers(KModifier.DATA)
					.addSuperinterfaces(buildSupertypes(tagArgumentInterface))
					.addFunction(createAsIdFunction(tagPrefix = "#"))
					.build()
			)
		} else {
			// Nested tag - add to enum
			val parent = path.substringBeforeLast("/")
			val enumName = parent.substringAfterLast("/").split("_").joinToString("") { it.pascalCase() }

			val enumBuilder = typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.enumBuilder(enumName)
					.addAnnotation(createSerializableAnnotation())
					.addSuperinterfaces(buildSupertypes(tagArgumentInterface))
					.addProperty(createNamespaceProperty(""))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "#\$NAMESPACE:$parent/\${name.lowercase()}")
							.build()
					)
			}

			enumBuilder.addEnumConstant(enumValue)
		}
	}

	// Nest enums from deepest to shallowest
	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast("/")
			val objectName = parent.substringAfterLast("/").split("_").joinToString("") { it.pascalCase() }

			typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.objectBuilder(objectName)
					.addModifiers(KModifier.DATA)
					.addSuperinterfaces(buildSupertypes(tagArgumentInterface))
					.addFunction(
						FunSpec.builder("asId")
							.addModifiers(KModifier.OVERRIDE)
							.returns(String::class)
							.addStatement("return %P", "#\$NAMESPACE:${parent.lowercase()}")
							.build()
					)
			}.addType(typeBuilder.build())
		}
	}

	// Add top-level enums/objects to parent
	typeBuilders.firstOrNull()?.forEach { (_, builder) ->
		parentBuilder.addType(builder.build())
	}
}

/**
 * Builds a list of supertype class names for tag types.
 */
private fun buildSupertypes(tagArgumentInterface: String?): List<ClassName> {
	val supertypes = mutableListOf<ClassName>()

	if (tagArgumentInterface != null) {
		val parts = tagArgumentInterface.split(".")
		supertypes.add(ClassName(parts.dropLast(1).joinToString("."), parts.last()))
	}

	return supertypes
}
