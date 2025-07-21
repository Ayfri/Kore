package generators

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import generateEnum
import getFromCacheOrDownloadTxt
import overrides
import removeMinecraftPrefix
import snakeCase
import url

suspend fun downloadComponentTypes() {
	val url = url("custom-generated/registries/data_component_type.txt")
	val componentTypes = getFromCacheOrDownloadTxt("data_component_type.txt", url).lines()
		.filter(String::isNotBlank)
		.removeMinecraftPrefix()

	val (itemComponents, entityComponents) = componentTypes.partition { "/" !in it }

	generateEnum(
		values = itemComponents,
		name = "ItemComponentType",
		sourceUrl = url,
		parentArgumentType = "DataComponentType"
	)

    generateEnum(
		values = entityComponents.map { it.replace("/", "_") },
		name = "EntityComponentType",
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
