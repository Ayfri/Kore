package generators

import GENERATED_PACKAGE
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import generateCompanion
import generateFile
import getFromCacheOrDownloadTxt
import overrides
import pascalCase
import removeMinecraftPrefix
import snakeCase
import url

suspend fun downloadComponentTypes() {
	val url = url("custom-generated/registries/data_component_type.txt")
	val componentTypes = getFromCacheOrDownloadTxt("data_component_type.txt", url).lines()
		.filter(String::isNotBlank)
		.removeMinecraftPrefix()

	val (itemComponents, entityComponents) = componentTypes.partition { "/" !in it }

	generateComponentTypesInterface(url)
	generateComponentEnumTypes(itemComponents, "ItemComponentType", url)
	generateComponentEnumTypes(entityComponents, "EntityComponentType", url)
}

private fun generateComponentTypesInterface(sourceUrl: String) {
	val interfaceType = TypeSpec.interfaceBuilder("ComponentType").apply {
		addAnnotation(
			AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
				.addMember("with = ComponentType.Companion.ComponentTypeSerializer::class")
				.build()
		)

		addModifiers(KModifier.SEALED)

		addProperty(
			PropertySpec.builder("namespace", String::class)
				.getter(FunSpec.getterBuilder().addStatement("return \"minecraft\"").build())
				.build()
		)

		addFunction(
			FunSpec.builder("asId")
				.addModifiers(KModifier.ABSTRACT)
				.returns(String::class)
				.build()
		)

		addType(
			TypeSpec.companionObjectBuilder().apply {
				addProperty(
					PropertySpec.builder("values", List::class.asClassName().parameterizedBy(ClassName(GENERATED_PACKAGE, "ComponentType")))
						.getter(
							FunSpec.getterBuilder()
								.addStatement("return ItemComponentType.entries + EntityComponentType.entries")
								.build()
						)
						.build()
				)
				addType(
					TypeSpec.objectBuilder("ComponentTypeSerializer")
						.superclass(ClassName("io.github.ayfri.kore.serializers", "ToStringSerializer").parameterizedBy(ClassName(GENERATED_PACKAGE, "ComponentType")))
						.addFunction(
							FunSpec.builder("serialize")
								.addParameter("encoder", ClassName("kotlinx.serialization.encoding", "Encoder"))
								.addParameter("value", ClassName(GENERATED_PACKAGE, "ComponentType"))
								.addStatement("encoder.encodeString(value.asId())")
								.overrides()
								.build()
						)
						.build()
				)
			}.build()
		)
	}

	generateFile("ComponentType", sourceUrl, interfaceType)
}

private fun generateComponentEnumTypes(values: List<String>, name: String, sourceUrl: String) {
	val hasSlash = values.any { "/" in it }

	val enumType = TypeSpec.enumBuilder(name).apply {
		addAnnotation(
			AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
				.addMember("with = $name.Companion.${name}Serializer::class")
				.build()
		)

		addSuperinterface(ClassName(GENERATED_PACKAGE, "ComponentType"))

		values.forEach { component ->
			val enumName = if (hasSlash) {
				component.replace("/", "_").snakeCase().uppercase()
			} else {
				component.snakeCase().uppercase()
			}
			addEnumConstant(enumName)
		}

		if (hasSlash) {
			// Pour EntityComponentType : ajouter originalName property
			addProperty(
				PropertySpec.builder("originalName", String::class)
					.getter(
						FunSpec.getterBuilder()
							.beginControlFlow("return when (this)")
							.apply {
								values.forEach { component ->
									val enumName = component.replace("/", "_").snakeCase().uppercase()
									addStatement("$enumName -> %S", component)
								}
							}
							.endControlFlow()
							.build()
					)
					.build()
			)

			addFunction(
				FunSpec.builder("asId")
					.addStatement("return \"\$namespace:\$originalName\"")
					.returns(String::class)
					.overrides()
					.build()
			)

			addType(generateCompanion(name, "value.asId()"))
		} else {
			// Pour ItemComponentType : utiliser name.lowercase()
			addFunction(
				FunSpec.builder("asId")
					.addStatement($$"return \"$namespace:${name.lowercase()}\"")
					.returns(String::class)
					.overrides()
					.build()
			)

			addType(generateCompanion(name, "\"minecraft:\${value.name.lowercase()}\""))
		}
	}

	generateFile(name, sourceUrl, enumType)
}
