package generators

import GENERATED_FOLDER
import GENERATED_PACKAGE
import com.squareup.kotlinpoet.*
import generateFile
import getFromCacheOrDownloadJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import libDir
import minecraftVersion
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

/** Downloads the vanilla registry list, plus registries the report doesn't expose, minus ones modeled by hand. */
suspend fun downloadRegistriesList(): Map<String, Registry> {
	val registriesListUrl = url("minecraft-generated/reports/datapack.json")
	val registriesList = getFromCacheOrDownloadJson("registries.json", registriesListUrl)
	val json = jsonDecoder.decodeFromJsonElement<Map<String, Map<String, Registry>>>(registriesList)

	val additionalTypes = mapOf(
		"minecraft:atlas" to Registry(elements = false, stable = true, tags = false),
		"minecraft:attribute_modifier" to Registry(elements = false, stable = true, tags = false),
		"minecraft:consume_cooldown_group" to Registry(elements = false, stable = true, tags = false),
		"minecraft:jigsaw" to Registry(elements = false, stable = true, tags = false),
		"minecraft:moon_phase" to Registry(elements = false, stable = true, tags = false),
		"minecraft:painting_asset" to Registry(elements = false, stable = true, tags = false),
		"minecraft:stopwatch" to Registry(elements = false, stable = true, tags = false),
		"minecraft:trim_color_palette" to Registry(elements = false, stable = true, tags = false),
		"minecraft:waypoint_style" to Registry(elements = false, stable = true, tags = false),
		"minecraft:worldgen/configured_structure" to Registry(elements = false, stable = true, tags = true),
	)

	val ignoreList = listOf("minecraft:block", "minecraft:item", "minecraft:tag")

	val completeMap = json.getOrDefault("registries", emptyMap()) + additionalTypes
	return completeMap.filterKeys { key ->
		key !in ignoreList
	}
}

/** Builds `<Name>Argument`, and when tagged also `<Name>OrTagArgument`/`<Name>TagArgument`, keyed by their path. */
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
									.initializer("%L","name")
									.overrides()
									.build()
							)
							.addProperty(
								PropertySpec.builder("namespace", String::class)
									.initializer("%L","namespace")
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

/** Info about a generated `*TagArgument` interface needed by the factory registry. */
data class TagArgumentInfo(
	val simpleName: String,
	val packageName: String,
)

/** Generates a typed `*Argument` interface (and tag variants) for every vanilla registry, no manual entry needed. */
suspend fun launchArgumentTypeGenerators(): List<TagArgumentInfo> {
	val registriesList = downloadRegistriesList()
	val tagArguments = mutableListOf<TagArgumentInfo>()

	registriesList.map { entry ->
		val argumentType = argumentTypeGen(entry.key, entry.value.tags)
		val filesToCreate = processArgumentType(argumentType)

		filesToCreate.map { (name, typeSpec) ->
			val prefix = name.substringBeforeLast(".").prependIfNotEmpty(".")

			val subPackage = when {
				"OrTagArgument" in name -> "arguments${prefix}"
				"TagArgument" in name -> {
					val tagSubPackage = "arguments${prefix}.tagged"
					tagArguments.add(TagArgumentInfo(name.substringAfterLast("."), tagSubPackage))
					tagSubPackage
				}

				else -> "arguments${prefix}.types"
			}

			generateFile(name.substringAfterLast("."), topLevel = typeSpec, subPackage = subPackage)
		}
	}

	return tagArguments
}

/** Emits `kore/src/main/generated/TagArgumentFactories.kt` mapping every `*TagArgument` KClass to its factory lambda. */
fun generateTagArgumentFactories(tagArguments: List<TagArgumentInfo>) {
	val generatedNames = tagArguments.map { it.simpleName }.toSet()
	val allNames = (generatedNames + listOf(
		"BlockTagArgument",
		"FunctionTagArgument",
		"ItemTagArgument",
		"TaggedResourceLocationArgument"
	))
		.sortedBy { it.lowercase() }

	val imports = buildString {
		appendLine("package $GENERATED_PACKAGE")
		appendLine()
		appendLine("import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument")
		appendLine("import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument")
		appendLine("import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument")
		appendLine("import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument")
		appendLine("import kotlin.reflect.KClass")
		for (info in tagArguments.sortedBy { it.simpleName.lowercase() }) {
			appendLine("import $GENERATED_PACKAGE.${info.packageName}.${info.simpleName}")
		}
		appendLine()
	}

	val factoryEntries = allNames.joinToString(",\n\t") { name ->
		"$name::class to { name, namespace -> $name(name, namespace) }"
	}

	val content = buildString {
		appendLine("// Automatically generated - do not modify!")
		appendLine("// Minecraft version : $minecraftVersion")
		appendLine(imports)
		appendLine("val tagArgumentFactories: Map<KClass<out TaggedResourceLocationArgument>, (String, String) -> TaggedResourceLocationArgument> = mapOf(")
		appendLine("\t$factoryEntries")
		appendLine(")")
	}

	val file = java.io.File(libDir, "$GENERATED_FOLDER/TagArgumentFactories.kt")
	file.parentFile.mkdirs()
	file.writeText(content)
	println("Generated $file")
}
