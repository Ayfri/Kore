import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun generatePathEnumTree(
	paths: List<String>,
	name: String,
	sourceUrl: String,
	parentArgumentType: String? = null,
	tagsParents: Map<String, String>? = null,
) {
	val typeBuilders = MutableList(paths.maxOf { path -> path.count { it == '/' } }) {
		mutableMapOf<String, TypeSpec.Builder>()
	}

	val hasParent = parentArgumentType != null

	val topLevel = TypeSpec.interfaceBuilder(name).apply {
		parentArgumentType?.let {
			addSuperinterface(argumentClassName(it))
		}

		addModifiers(KModifier.SEALED)
		if (hasParent) {
			addProperty(
				PropertySpec.builder("namespace", String::class)
					.addModifiers(KModifier.OVERRIDE)
					.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
					.build()
			)
		}
	}

	val topLevelInterfaceClassName = ClassName("generated", name)

	for (path in paths) {
		val parent = path.substringBeforeLast('/')
		val depth = parent.count { it == '/' }

		val enumValue = path.substringAfterLast('/').snakeCase().uppercase()
		val enumName = parent.substringAfterLast('/').pascalCase()
		val tagParent = tagsParents?.keys?.firstOrNull { parent.startsWith(it) }

		if ("/" !in path) {
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
							.addModifiers(KModifier.OVERRIDE)
							.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
							.build()
					)

					val argumentType = tagsParents[tagParent]!!
					addSuperinterface(argumentClassName("tagged.$argumentType"))
				}

				if (hasParent || tagParent != null) {
					val hash = if (tagParent != null) "#" else ""
					var tagPath = if (tagParent != null) parent.substringAfter(tagParent).substringAfterLast("/") + "/" else ""
					if (tagPath == "/") tagPath = ""

					if (hasParent) tagPath = parent.substringAfterLast("/") + "/"

					addFunction(
						FunSpec.builder("asString")
							.addStatement("return \"$hash\$namespace:$tagPath\${name.lowercase()}\"")
							.overrides()
							.build()
					)
				}

				addAnnotation(
					AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
						.addMember("with = $enumName.Companion.${enumName.asSerializer()}::class")
						.build()
				)

				addType(generateCompanion(enumName, "\"$parent/\${value.name.lowercase()}\""))
			}
		}.addEnumConstant(enumValue)
	}

	for (depth in typeBuilders.lastIndex downTo 1) {
		for ((path, typeBuilder) in typeBuilders[depth]) {
			val parent = path.substringBeforeLast('/')
			val objectName = parent.substringAfterLast('/').pascalCase()

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

fun generateCompanion(name: String, encoderValue: String? = "\"minecraft:\${value.name.lowercase()}\"") =
	TypeSpec.companionObjectBuilder().apply {
		addType(
			TypeSpec.objectBuilder(name.asSerializer())
				.superclass(ClassName("serializers", "LowercaseSerializer").parameterizedBy(ClassName("", name)))
				.addSuperclassConstructorParameter("entries")
				.letIf(encoderValue != null) {
					it.addFunction(
						FunSpec.builder("serialize")
							.addModifiers(KModifier.OVERRIDE)
							.addParameter("encoder", ClassName("kotlinx.serialization.encoding", "Encoder"))
							.addParameter("value", ClassName("", name))
							.addStatement("encoder.encodeString($encoderValue)")
							.build()
					)
				}.build()
		)
	}.build()
