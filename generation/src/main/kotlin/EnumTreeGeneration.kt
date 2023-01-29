import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun generatePathEnumTree(paths: List<String>, name: String, sourceUrl: String, parentArgumentType: String? = null) {
	val typeBuilders = MutableList(paths.maxOf { path -> path.count { it == '/' } }) {
		mutableMapOf<String, TypeSpec.Builder>()
	}

	val hasParent = parentArgumentType != null

	val topLevel = TypeSpec.interfaceBuilder(name).apply {
		parentArgumentType?.let { addSuperinterface(ClassName("arguments", "Argument", parentArgumentType)) }
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

	for (path in paths) {
		val parent = path.substringBeforeLast('/')
		val depth = parent.count { it == '/' }

		val enumValue = path.substringAfterLast('/').snakeCase().uppercase()
		val enumName = parent.substringAfterLast('/').pascalCase()

		typeBuilders[depth].getOrPut(parent) {
			TypeSpec.enumBuilder(enumName).apply {
				addSuperinterface(ClassName("", name))

				if (hasParent) {
					addFunction(
						FunSpec.builder("asString")
							.addStatement("return \"\$namespace:$parent/\${name.lowercase()}\"")
							.overrides()
							.build()
					)
				}

				addAnnotation(
					AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
						.addMember("with = $enumName.Companion.${enumName.asSerializer()}::class")
						.build()
				)

				addType(generateCompanion(enumName, "$parent/\${value.name.lowercase()}"))
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

fun generateCompanion(name: String, encoderValue: String = "minecraft:\${value.name.lowercase()}"): TypeSpec {
	return TypeSpec.companionObjectBuilder().apply {
		addProperty(
			PropertySpec.Companion.builder("values", ARRAY.parameterizedBy(ClassName("", name)))
				.initializer("values()")
				.build()
		)

		addType(
			TypeSpec.objectBuilder(name.asSerializer())
				.superclass(ClassName("serializers", "LowercaseSerializer").parameterizedBy(ClassName("", name)))
				.addSuperclassConstructorParameter("values")
				.addFunction(
					FunSpec.builder("serialize")
						.addModifiers(KModifier.OVERRIDE)
						.addParameter("encoder", ClassName("kotlinx.serialization.encoding", "Encoder"))
						.addParameter("value", ClassName("", name))
						.addStatement("encoder.encodeString(\"$encoderValue\")")
						.build()
				)
				.build()
		)
	}.build()
}
