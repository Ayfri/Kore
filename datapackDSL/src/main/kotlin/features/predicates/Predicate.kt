package features.predicates

import DataPack
import Generator
import arguments.Argument
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.predicateConditionsSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.io.File


data class Predicate(
	var fileName: String = "predicate",
	var predicateConditions: List<PredicateCondition> = mutableListOf(),
) : Generator {
	@Transient
	private lateinit var json: Json

	override fun generate(dataPack: DataPack, directory: File) {
		val json = getJsonEncoder(dataPack).encodeToString(predicateConditionsSerializer, predicateConditions)
		File(directory, "$fileName.json").writeText(json)
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

fun DataPack.predicate(fileName: String, predicate: Predicate.() -> Unit): Argument.Predicate {
	predicates += Predicate(fileName).apply(predicate)
	return Argument.Predicate(fileName, name)
}
