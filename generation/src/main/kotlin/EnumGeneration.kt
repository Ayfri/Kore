import com.squareup.kotlinpoet.*

fun generateEnum(
	values: Collection<String>,
	name: String,
	sourceUrl: String,
	parentArgumentType: String? = null,
	asString: String = "\${name.lowercase()}",
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
			// Make it work with `worldgen.` prefix
			val prefix = if ("." in it) it.substringBeforeLast(".") + "." else ""
			val argumentTypeName = it.substringAfterLast(".")
			addSuperinterface(argumentClassName("${prefix}types.$argumentTypeName"))
		}

		values.filter(String::isNotBlank).removeMinecraftPrefix().forEach { value ->
			addEnumConstant(value.snakeCase().uppercase())
		}

		if (parentArgumentType != null) {
			addProperty(
				PropertySpec.builder("namespace", String::class)
					.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
					.overrides()
					.build()
			)

			addFunction(
				FunSpec.builder("asId")
					.addStatement($$"return \"$namespace:$$asString\"")
					.returns(String::class)
					.overrides()
					.build()
			)
		}

		additionalEnumCode()

		val encoderValue = if (parentArgumentType != null) "value.asId()" else null
		addType(generateCompanion(name, encoderValue))
	}

	generateFile(name, sourceUrl, enumType, additionalCode = additionalCode)
}
