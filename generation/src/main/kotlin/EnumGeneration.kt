import com.squareup.kotlinpoet.*

fun generateEnum(
	values: Collection<String>,
	name: String,
	sourceUrl: String,
	parentArgumentType: String? = null,
	asString: String = "lowercase()",
	additionalCode: FileSpec.Builder.(enumName: String) -> Unit = {},
	additionalEnumCode: TypeSpec.Builder.() -> Unit = {},
) {
	val enumType = TypeSpec.enumBuilder(name).apply {
		addAnnotation(
			AnnotationSpec.builder(
				ClassName("kotlinx.serialization", "Serializable")
			)
				.addMember("with = $name.Companion.${name.asSerializer()}::class")
				.build()
		)

		parentArgumentType?.let {
			addSuperinterface(argumentClassName(it))
		}

		values.removeMinecraftPrefix().forEach { value ->
			addEnumConstant(value.snakeCase().uppercase())
		}

		if (parentArgumentType != null) {
			addProperty(
				PropertySpec.builder("namespace", String::class)
					.addModifiers(KModifier.OVERRIDE)
					.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
					.build()
			)

			addFunction(
				FunSpec.builder("asId")
					.addStatement("return \"\$namespace:\${name.$asString}\"")
					.addModifiers(KModifier.OVERRIDE)
					.build()
			)
		}


		additionalEnumCode()

		val encoderValue = if (parentArgumentType != null) "\"minecraft:\${value.name.$asString}\"" else null
		addType(generateCompanion(name, encoderValue))
	}

	generateFile(name, sourceUrl, enumType, additionalCode)
}
