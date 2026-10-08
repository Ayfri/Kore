import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import generators.Generator

/**
 * Generates a sealed hierarchy of nested `object`/`enum class` declarations from `/`-separated resource paths
 * (e.g. `worldgen/biome/plains`): one nesting level per path segment, one `enum class` per leaf directory. Used
 * instead of [generateEnum] whenever entries contain [Generator.separator].
 */
fun generatePathEnumTree(paths: List<String>, generator: Generator) {
	val name = generator.name
	val sourceUrl = generator.url
	val parentArgumentType = generator.getParentArgumentType()
	val subInterfacesParents = generator.subInterfacesParents.orEmpty()
	val separator = generator.separator
	val tagsParents = generator.tagsParents

	val typeBuilders = MutableList(paths.maxOf { path -> path.countOccurrences(separator) }) {
		mutableMapOf<String, TypeSpec.Builder>()
	}

	val hasParent = parentArgumentType != null

	val topLevel = TypeSpec.interfaceBuilder(name).apply {
		parentArgumentType?.let {
			val (prefix, argumentTypeName) = splitArgumentTypePrefix(it)
			addSuperinterface(argumentClassName("${prefix}types.$argumentTypeName"))
		}

		addModifiers(KModifier.SEALED)
		if (hasParent) addMinecraftNamespaceProperty()
	}

	val topLevelInterfaceClassName = ClassName(GENERATED_PACKAGE, name)
	val topLevelFolders = paths.filter { separator in it }.mapTo(mutableSetOf()) { it.substringBefore(separator) }
	val topLevelLeavesWithFolder = mutableSetOf<String>()

	for (path in paths) {
		val parent = path.substringBeforeLast(separator)
		val depth = parent.countOccurrences(separator)

		val enumValue = path.substringAfterLast(separator).snakeCase().uppercase()
		val enumName = parent.substringAfterLast(separator).pascalCase()
		val tagParent = tagsParents?.keys?.firstOrNull { parent.startsWith(it) }

		if (separator !in path && path in topLevelFolders) {
			topLevelLeavesWithFolder += path
			continue
		}

		if (separator !in path) {
			topLevel.addType(
				TypeSpec
					.objectBuilder(enumName)
					.apply {
						addModifiers(KModifier.DATA)
						addSuperinterface(topLevelInterfaceClassName)
						addProperty(
							PropertySpec
								.builder("name", String::class)
								.overrides()
								.initializer("\"${enumValue.lowercase()}\"")
								.build()
						)
					}
					.build()
			)
			continue
		}

		typeBuilders[depth].getOrPut(parent) {
			TypeSpec.enumBuilder(enumName).apply {
				addSuperinterface(topLevelInterfaceClassName)
				if (enumName in subInterfacesParents) {
					val additionalInterface = subInterfacesParents[enumName]!!
					addSuperinterface(
						ClassName(
							GENERATED_PACKAGE + "." + additionalInterface.substringBeforeLast("."),
							additionalInterface.substringAfterLast(".")
						)
					)
				}

				if (tagParent != null) {
					addMinecraftNamespaceProperty()

					val (prefix, argumentTypeName) = splitArgumentTypePrefix(tagsParents[tagParent]!!)
					addSuperinterface(argumentClassName("${prefix}tagged.$argumentTypeName"))
				}

				if (hasParent || tagParent != null) {
					val hash = if (tagParent != null) "#" else ""
					var tagPath = if (tagParent != null) parent.substringAfter(tagParent).substringAfterLast(separator) + separator else ""
					if (tagPath == separator) tagPath = ""

					if (hasParent) tagPath = "$parent$separator"

					// For Tags specifically, don't include the path, only the name
					if (name == "Tags") {
						tagPath = tagPath.substringAfter(separator)
						if ("worldgen" in parent) {
							tagPath = tagPath.substringAfter(separator)
						}
					}

					addFunction(
						FunSpec.builder("asId")
							.addStatement($$"return \"$$hash$namespace:$$tagPath${name.lowercase()}\"")
							.returns(String::class)
							.overrides()
							.build()
					)
				}
			}
		}.addEnumConstant(enumValue)
	}

	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast(separator)
			val objectName = parent.substringAfterLast(separator).pascalCase()

			typeBuilders[depth - 1].getOrPut(parent) {
				TypeSpec.objectBuilder(objectName)
			}.addType(typeBuilder.build())
		}
	}

	/** A top-level file sharing its name with a folder (`overworld` and `overworld/surface`) becomes the folder type itself (an object) or its companion (an enum). */
	fun TypeSpec.Builder.addTopLevelLeaf(path: String) = apply {
		val leaf = if (enumConstants.isEmpty()) this else TypeSpec.companionObjectBuilder()
		leaf.addSuperinterface(topLevelInterfaceClassName)
		leaf.addProperty(PropertySpec.builder("name", String::class).overrides().initializer("%S", path).build())
		if (leaf !== this) addType(leaf.build())
	}

	typeBuilders.firstOrNull()?.forEach { (path, typeBuilder) ->
		if (path in topLevelLeavesWithFolder) typeBuilder.addTopLevelLeaf(path)
		topLevel.addType(typeBuilder.build())
	}

	val file = generateFile(name, sourceUrl, topLevel)
	logGenerated("enum tree", name, "${paths.size} paths", file)
}

/** Builds the `companion object` shared by every generated enum: a nested `<name>Serializer` object, writing [transform] when set. */
fun generateCompanion(name: String, transform: String? = null) =
	TypeSpec.companionObjectBuilder().addType(
		TypeSpec.objectBuilder(name.asSerializer())
			.superclass(ClassName("$CODE_PACKAGE.serializers", "LowercaseSerializer").parameterizedBy(ClassName("", name)))
			.addSuperclassConstructorParameter(if (transform == null) "entries" else "entries, { $transform }")
			.build()
	).build()
