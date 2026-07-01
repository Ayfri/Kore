import com.squareup.kotlinpoet.*

/**
 * Generates a flat `enum class` from a list of resource names, one constant per entry. When [parentArgumentType]
 * is set, the enum also implements that `*Argument` interface. Used for entries without a `/`; see
 * [generatePathEnumTree] for the tree-shaped variant.
 */
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
		addAnnotation(serializableWith("$name.Companion.${name.asSerializer()}"))

		parentArgumentType?.let {
			val (prefix, argumentTypeName) = splitArgumentTypePrefix(it)
			addSuperinterface(argumentClassName("${prefix}types.$argumentTypeName"))
		}

		values.filter(String::isNotBlank).removeMinecraftPrefix().forEach { value ->
			addEnumConstant(value.snakeCase().uppercase())
		}

		if (parentArgumentType != null) {
			addMinecraftNamespaceProperty()

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
