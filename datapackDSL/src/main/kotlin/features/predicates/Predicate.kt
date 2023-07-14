package features.predicates

import DataPack
import Generator
import arguments.Argument
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import features.predicates.conditions.PredicateConditions
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule


data class Predicate(
	override var fileName: String = "predicate",
	var predicateConditions: PredicateConditions = mutableListOf(),
) : Generator {
	@Transient
	private lateinit var json: Json

	override fun generateJson(dataPack: DataPack) = getJsonEncoder(dataPack).encodeToString(predicateConditions)

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
