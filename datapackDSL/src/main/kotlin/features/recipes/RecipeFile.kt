package features.recipes

import DataPack
import Generator
import features.recipes.types.Recipe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

data class RecipeFile(
	override var fileName: String = "recipe",
	var recipe: Recipe,
) : Generator {
	private lateinit var json: Json

	override fun generateJson(dataPack: DataPack): String {
		val jsonEncoder = getJsonEncoder(dataPack)
		var json = jsonEncoder.encodeToJsonElement(recipe)
		json = buildJsonObject {
			json.jsonObject.forEach { (key, value) ->
				if (key == "__type__") return@forEach
				if (key == "type") put(key, JsonPrimitive("minecraft:${value.jsonPrimitive.content}"))
				else put(key, value)
			}
		}
		return jsonEncoder.encodeToString(json)
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
