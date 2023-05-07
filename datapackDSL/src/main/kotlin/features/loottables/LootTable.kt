package features.loottables

import DataPack
import Generator
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import java.io.File

@Serializable
data class LootTable(
	@Transient var fileName: String = "loot_table",
	var functions: List<String>? = null,
	var pools: List<LootPool>? = null,
) : Generator {
	@Transient
	private lateinit var json: Json

	override fun generate(dataPack: DataPack, directory: File) {
		val jsonEncoder = getJsonEncoder(dataPack)
		val json = jsonEncoder.encodeToJsonElement(this)
		val finalJson = buildJsonObject {
			json.jsonObject.forEach { (key, value) ->
				println("$key: $value")
				if (key == "pools") {
					put(key, buildJsonArray {
						value.jsonArray.forEach { pool ->
							val finalPool = buildJsonObject {
								pool.jsonObject.forEach poolProperties@{ (key, value) ->
									println("$key: $value")
									if (key == "conditions") {
										val finalConditions = value.jsonArray.filterNot { it.jsonPrimitive.isString }

										if (finalConditions.isNotEmpty()) put(key, JsonArray(finalConditions))
										return@poolProperties
									}

									put(key, value)
								}
							}
							add(finalPool)
						}
					})
					return@forEach
				}

				put(key, value)
			}
		}
		File(directory, "$fileName.json").writeText(jsonEncoder.encodeToString(JsonElement.serializer(), finalJson))
	}

	@OptIn(ExperimentalSerializationApi::class)
	fun getJsonEncoder(dataPack: DataPack) = when {
		::json.isInitialized -> json
		else -> {
			json = Json {
				prettyPrint = dataPack.jsonEncoder.configuration.prettyPrint
				if (prettyPrint) prettyPrintIndent = dataPack.jsonEncoder.configuration.prettyPrintIndent
				serializersModule = SerializersModule {
					contextual(Advancements::class, AdvancementsJSONSerializer)
				}
			}
			json
		}
	}
}
