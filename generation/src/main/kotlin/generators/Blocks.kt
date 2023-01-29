package generators

import camelCase
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import generateEnum
import getFromCacheOrDownloadJson
import header
import isIntOrBoolean
import kotlinx.serialization.json.*
import libDir
import pascalCase
import snakeCase
import url
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.contains
import kotlin.collections.set

suspend fun downloadBlocks() {
	val urlString = url("generated/blocks.json")
	val blocks = getFromCacheOrDownloadJson("blocks.json", urlString).jsonObject
	generateBlocks(urlString, blocks)
}

fun generateBlocks(urlString: String, blocks: JsonObject) {
	val blocksList = blocks.keys.map { it.substringAfter("minecraft:").uppercase() }

	generateBlocksEnum(blocksList, urlString)
	generateSealedBlockStateClass()
}

fun generateBlocksEnum(blocks: List<String>, sourceUrl: String) {
	generateEnum(blocks, "Blocks", sourceUrl, "Block") {
		addProperty(
			PropertySpec.builder("nbtData", ClassName("net.benwoodworth.knbt", "NbtCompound").copy(nullable = true))
				.addModifiers(KModifier.OVERRIDE)
				.mutable()
				.initializer("null")
				.build()
		)

		val stringTypeName = String::class.asTypeName()
		addProperty(
			PropertySpec.builder("states", MUTABLE_MAP.parameterizedBy(stringTypeName, stringTypeName))
				.addModifiers(KModifier.OVERRIDE)
				.mutable()
				.initializer("mutableMapOf()")
				.build()
		)
	}
}

fun generateBlockStates(urlString: String, blocks: JsonObject) {
	val propertiesTypes = mutableMapOf<String, List<String>>()
	blocks.forEach blocks@{ (key, value) ->
		val blockName = key.substringAfter("minecraft:").uppercase()
		val block = value.jsonObject
		val properties = block["properties"]?.jsonObject ?: return@blocks
		val numberProperties = mutableListOf<String>()
		val booleanProperties = mutableListOf<String>()
		val defaultValues = block["states"]?.jsonArray?.find {
			it.jsonObject["default"]?.jsonPrimitive?.boolean == true
		}?.jsonObject?.get("properties")?.jsonObject?.entries?.associate { (key, value) ->
			key to when {
				value.jsonPrimitive.booleanOrNull != null -> value.jsonPrimitive.boolean.toString()
				value.jsonPrimitive.intOrNull != null -> value.jsonPrimitive.int.toString()
				else -> "${key.pascalCase()}.${value.jsonPrimitive.content.pascalCase()}"
			}
		} ?: return@blocks

		properties.forEach { (propertyName, values) ->
			val propertyValues = values.jsonArray.map { it.jsonPrimitive.content }

			if (propertyValues.any { it.toIntOrNull() != null }) {
				numberProperties += propertyName
				return@forEach
			}
			if (propertyValues.all { it == "true" || it == "false" }) {
				booleanProperties += propertyName
				return@forEach
			}

			if (propertyName in propertiesTypes) return@forEach
			propertiesTypes[propertyName] = propertyValues
			generateBlockStateType(propertyValues, propertyName)
		}

		val states = properties.keys.filter {
			it !in numberProperties && it !in booleanProperties
		}.map { it to it.pascalCase() } + numberProperties.map { it to "Int" } + booleanProperties.map { it to "Boolean" }

		generateBlockStatesClass(blockName, states.map { it.first.camelCase() to it.second }, defaultValues)
	}
}

fun generateSealedBlockStateClass() {
	val file = File(libDir, "src/main/kotlin/generated/blockStates/BlockState.kt")
	file.parentFile.mkdirs()
	println("Generating ${file.canonicalPath}")

	file.writeText(
		"""
		$header
		
		package generated.blockStates
		
		sealed interface BlockState
	""".trimIndent()
	)
}

fun generateBlockStateType(values: Collection<String>, name: String) = Unit

fun generateBlockStatesClass(name: String, states: List<Pair<String, String>>, defaultValues: Map<String, String> = emptyMap()) {
	val className = name.pascalCase()
	val containsCustomProperties = states.any { !it.second.isIntOrBoolean() }

	val file = File(libDir, "src/main/kotlin/generated/blockStates/$className.kt")
	file.parentFile.mkdirs()
	println("Generating $file")

	file.writeText(
		"""
		|$header
		|
		|package generated.blockStates
		|
		|import arguments.Argument${if (containsCustomProperties) "\n|import generated.blockStates.types.*" else ""}
		|import generated.Blocks
		|
		|data class $className(
		|	${
			states.joinToString(",\n\t", postfix = ",") { (propertyName, propertyType) ->
				when (propertyName) {
					in defaultValues -> "val $propertyName: $propertyType = ${defaultValues[propertyName]}"
					else -> "val $propertyName: $propertyType"
				}
			}
		}
		|) : BlockState, Argument.Block("${name.lowercase()}", states = mutableMapOf(${
			states.joinToString(", ") {
				"\"${it.first.snakeCase()}\" to ${it.first}.toString()${if (it.second.isIntOrBoolean()) ".lowercase()" else ""}"
			}
		})) {
		|	companion object {
		|		val type = Blocks.$name
		|	}
		|}
		""".trimMargin()
	)
}
