package generators

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import generateFile
import getFromCacheOrDownloadJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import overrides
import pascalCase
import url

data class ArgumentType(
	var name: String,
    var tagged: Boolean = true,
)

fun argumentTypeGen(name: String, tagged: Boolean = true) = ArgumentType(name, tagged)

@Serializable
data class Registry(
	var elements: Boolean,
	var stable: Boolean,
	var tags: Boolean,
)

private val jsonDecoder = Json {
	ignoreUnknownKeys = true
}

suspend fun downloadRegistriesList(): Map<String, Registry> {
	val registriesListUrl = url("minecraft-generated/reports/datapack.json")
	val registriesList = getFromCacheOrDownloadJson("registries.json", registriesListUrl)
	val json = jsonDecoder.decodeFromJsonElement<Map<String, Map<String, Registry>>>(registriesList)

	val additionalTypes = mapOf(
		"minecraft:worldgen/configured_structure" to Registry(elements = false, stable = true, tags = true),
	)

	return json.getOrDefault("registries", emptyMap()) + additionalTypes
}

fun processArgumentType(argumentType: ArgumentType): Map<String, TypeSpec.Builder> {
	val resourceLocationClass = ClassName("io.github.ayfri.kore.arguments.types", "ResourceLocationArgument")
	val taggedResourceLocationClass = ClassName("io.github.ayfri.kore.arguments.types", "TaggedResourceLocationArgument")
	val serializableBaseAnnotation = ClassName("kotlinx.serialization", "Serializable")
	val baseArgumentClass = ClassName("io.github.ayfri.kore.arguments", "Argument")
	val baseArgumentSerializer = ClassName("io.github.ayfri.kore.arguments", "Argument", "ArgumentSerializer")

	val serializableAnnotation = AnnotationSpec.builder(serializableBaseAnnotation).addMember("with = %T::class", baseArgumentSerializer).build()
	val className = argumentType.name.removePrefix("minecraft:").substringAfterLast("/").pascalCase()
	val prefixPath = argumentType.name.removePrefix("minecraft:").substringBeforeLast("/").replace("/", ".").replace("_", "").replace(className, "", ignoreCase = true)

	fun generateCompanionObject(classSuffix: String) =
		TypeSpec.companionObjectBuilder()
			.addFunction(
				FunSpec.builder("invoke")
					.addModifiers(KModifier.OPERATOR)
					.addParameter("name", String::class)
					.addParameter(
						ParameterSpec.builder("namespace", String::class)
							.defaultValue("%S","minecraft")
							.build()
					)
					.returns(ClassName("", "$className$classSuffix"))
					.addStatement(
							"return %L",
						TypeSpec.anonymousClassBuilder()
							.addSuperinterface(ClassName("", "$className$classSuffix"))
							.addProperty(
								PropertySpec.builder("name", String::class)
									.initializer("%S","name")
									.overrides()
									.build()
							)
							.addProperty(
								PropertySpec.builder("namespace", String::class)
									.initializer("%S","namespace")
									.overrides()
									.build()
							)
							.build()
					)
					.build()
			).build()

	val argumentInterface = TypeSpec.interfaceBuilder("${className}Argument")
		.addSuperinterface(resourceLocationClass)
		.addAnnotation(serializableAnnotation)
		.addType(generateCompanionObject("Argument"))

	val result = mutableMapOf("${prefixPath}.${className}Argument" to argumentInterface)

	if (argumentType.tagged) {
		val parentName = "${className}OrTagArgument"
		val parentRef = ClassName("io.github.ayfri.kore.generated.arguments${prefixPath.prependIfNotEmpty(".")}", parentName)
		val parentArgumentInterface = TypeSpec.interfaceBuilder(parentName)
			.addSuperinterface(baseArgumentClass)
			.addAnnotation(serializableAnnotation)

		val taggedArgumentInterface = TypeSpec.interfaceBuilder("${className}TagArgument")
			.addSuperinterface(taggedResourceLocationClass)
			.addSuperinterface(parentRef)
			.addAnnotation(serializableAnnotation)
			.addType(generateCompanionObject("TagArgument"))

		argumentInterface.addSuperinterface(parentRef)

		result += mapOf(
			"${prefixPath}.${parentName}" to parentArgumentInterface,
			"${prefixPath}.${className}TagArgument" to taggedArgumentInterface
		)
	}

	return result
}

private fun String.prependIfNotEmpty(other: String) = if (this.isNotEmpty()) {
    other + this
} else {
    this
}

suspend fun launchArgumentTypeGenerators() {
	val registriesList = downloadRegistriesList()

	val ignoreList = listOf("block", "item", "tag")

	registriesList.filter {
		it.key.removePrefix("minecraft:") !in ignoreList
	}.map { entry ->
		val argumentType = argumentTypeGen(entry.key, entry.value.tags)
		val filesToCreate = processArgumentType(argumentType)

		filesToCreate.map { (name, typeSpec) ->
			val prefix = name.substringBeforeLast(".").prependIfNotEmpty(".")

			generateFile(name.substringAfterLast("."), topLevel = typeSpec, subPackage = when {
				"OrTagArgument" in name -> "arguments${prefix}"
				"TagArgument" in name -> "arguments${prefix}.tagged"
				else -> "arguments${prefix}.types"
			})
		}
	}
}
