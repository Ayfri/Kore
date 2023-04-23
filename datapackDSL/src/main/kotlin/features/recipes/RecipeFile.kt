package features.recipes

import DataPack
import Generator
import features.recipes.types.Recipe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import java.io.File

@Serializable
data class RecipeFile(
	@Transient
	var fileName: String = "recipe",
	var recipe: Recipe,
) : Generator {
	@Transient
	private lateinit var json: Json

	override fun generate(dataPack: DataPack, directory: File) {
		var json = getJsonEncoder(dataPack).encodeToJsonElement(Recipe.serializer(), recipe)
		json = buildJsonObject {
			json.jsonObject.forEach { (key, value) ->
				if (key == "__type__") return@forEach
				if (key == "type") put(key, JsonPrimitive("minecraft:${value.jsonPrimitive.content}"))
				else put(key, value)
			}
		}
		File(directory, "$fileName.json").writeText(json.toString())
	}

	@OptIn(ExperimentalSerializationApi::class)
	fun getJsonEncoder(dataPack: DataPack) = when {
		::json.isInitialized -> json
		else -> {
			json = Json {
				prettyPrint = dataPack.jsonEncoder.configuration.prettyPrint
				if (prettyPrint) prettyPrintIndent = dataPack.jsonEncoder.configuration.prettyPrintIndent
				classDiscriminator = "__type__"
				encodeDefaults = true
				explicitNulls = false
			}
			json
		}
	}
}

val DataPack.recipesBuilder get() = Recipes(this)
fun DataPack.recipes(block: Recipes.() -> Unit) = recipesBuilder.apply(block)
