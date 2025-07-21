import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun generatePathEnumTree(
	paths: List<String>,
	name: String,
	sourceUrl: String,
	parentArgumentType: String? = null,
	separator: String = "/",
	tagsParents: Map<String, String>? = null,
) {
	val typeBuilders = MutableList(paths.maxOf { path -> path.countOccurrences(separator) }) {
		mutableMapOf<String, TypeSpec.Builder>()
	}

	val hasParent = parentArgumentType != null

	val topLevel = TypeSpec.interfaceBuilder(name).apply {
		parentArgumentType?.let {
			// Make it work with `worldgen.` prefix
			val prefix = if ("." in it) it.substringBeforeLast(".") + "." else ""
			val argumentTypeName = it.substringAfterLast(".")
			addSuperinterface(argumentClassName("${prefix}types.$argumentTypeName"))
		}

		addModifiers(KModifier.SEALED)
		if (hasParent) {
			addProperty(
				PropertySpec.builder("namespace", String::class)
					.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
					.overrides()
					.build()
			)
		}
	}

	val topLevelInterfaceClassName = ClassName(GENERATED_PACKAGE, name)

	for (path in paths) {
		val parent = path.substringBeforeLast(separator)
		val depth = parent.countOccurrences(separator)

		val enumValue = path.substringAfterLast(separator).snakeCase().uppercase()
		val enumName = parent.substringAfterLast(separator).pascalCase()
		val tagParent = tagsParents?.keys?.firstOrNull { parent.startsWith(it) }

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

				if (tagParent != null) {
					addProperty(
						PropertySpec.builder("namespace", String::class)
							.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
							.overrides()
							.build()
					)

					val argumentType = tagsParents[tagParent]!!
					// Make it work with `worldgen.` prefix
					val prefix = if ("." in argumentType) argumentType.substringBeforeLast(".") + "." else ""
					val argumentTypeName = argumentType.substringAfterLast(".")
					addSuperinterface(argumentClassName("${prefix}tagged.$argumentTypeName"))
				}

				if (hasParent || tagParent != null) {
					val hash = if (tagParent != null) "#" else ""
					var tagPath = if (tagParent != null) parent.substringAfter(tagParent).substringAfterLast(separator) + separator else ""
					if (tagPath == separator) tagPath = ""

					if (hasParent) tagPath = "$parent$separator"

					addFunction(
						FunSpec.builder("asId")
							.addStatement($$"return \"$$hash$namespace:$$tagPath${name.lowercase()}\"")
							.returns(String::class)
							.overrides()
							.build()
					)
				}

				addAnnotation(
					AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
						.addMember("with = $enumName.Companion.${enumName.asSerializer()}::class")
						.build()
				)

				addType(generateCompanion(enumName, "value.asId()"))
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

	typeBuilders.firstOrNull()?.forEach { topLevel.addType(it.value.build()) }

	generateFile(name, sourceUrl, topLevel)
}

inline fun <T> T.letIf(
	condition: Boolean,
	block: (T) -> T,
) = if (condition) block(this) else this

fun generateCompanion(name: String, encoderValue: String? = "value.asId()") =
	TypeSpec.companionObjectBuilder().apply {
		addType(
			TypeSpec.objectBuilder(name.asSerializer())
				.superclass(ClassName("$CODE_PACKAGE.serializers", "LowercaseSerializer").parameterizedBy(ClassName("", name)))
				.addSuperclassConstructorParameter("entries")
				.letIf(encoderValue != null) {
					it.addFunction(
						FunSpec.builder("serialize")
							.addParameter("encoder", ClassName("kotlinx.serialization.encoding", "Encoder"))
							.addParameter("value", ClassName("", name))
							.addStatement("encoder.encodeString($encoderValue)")
							.overrides()
							.build()
					)
				}.build()
		)
	}.build()
