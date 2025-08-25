package generators

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import generateEnum
import getFromCacheOrDownloadTxt
import removeMinecraftPrefix
import snakeCase
import url

suspend fun downloadItemComponentTypes() {
	val url = url("custom-generated/registries/data_component_type.txt")
	val components = getFromCacheOrDownloadTxt("data_component_type.txt", url).lines()
		.filter(String::isNotBlank)
		.removeMinecraftPrefix()

	val (itemComponents, entityComponents) = components.partition { "/" !in it }

	generateEnum(
		values = itemComponents,
		name = "ItemComponentTypes",
		sourceUrl = url,
		parentArgumentType = "DataComponentType"
	)

    generateEnum(
		values = entityComponents.map { it.replace("/", "_") },
		name = "EntityItemComponentTypes",
		sourceUrl = url,
		parentArgumentType = "DataComponentType",
		asString = "\$originalName",
		additionalEnumCode = {
			addProperty(
				PropertySpec.builder("originalName", String::class)
					.getter(
						FunSpec.getterBuilder()
							.beginControlFlow("return when (this)")
							.apply {
								entityComponents.forEach { component ->
									val enumName = component.replace("/", "_").snakeCase().uppercase()
									addStatement("$enumName -> %S", component)
								}
							}
							.endControlFlow()
							.build()
					)
					.build()
			)
		}
	)
}
