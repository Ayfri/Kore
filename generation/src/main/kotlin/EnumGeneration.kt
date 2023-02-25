import com.squareup.kotlinpoet.*

fun generateEnum(
	values: Collection<String>,
	name: String,
	sourceUrl: String,
	parentArgumentType: String,
	asString: String = "lowercase()",
	additionalCode: FileSpec.Builder.(enumName: String) -> Unit = {},
	additionalEnumCode: TypeSpec.Builder.() -> Unit = {}
) {
	val enumType = TypeSpec.enumBuilder(name).apply {
		addAnnotation(
			AnnotationSpec.builder(
				ClassName("kotlinx.serialization", "Serializable")
			)
				.addMember("with = $name.Companion.${name.asSerializer()}::class")
				.build()
		)
		addSuperinterface(ClassName("arguments", "Argument", parentArgumentType))

		values.removeMinecraftPrefix().forEach { value ->
			addEnumConstant(value.snakeCase().uppercase())
		}

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

		additionalEnumCode()

		addType(generateCompanion(name, "minecraft:\${value.name.$asString}"))
	}

	generateFile(name, sourceUrl, enumType, additionalCode)
}
