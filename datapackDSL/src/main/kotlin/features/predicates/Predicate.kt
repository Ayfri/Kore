package features.predicates

import DataPack
import Generator
import arguments.Argument
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.predicateConditionsSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.io.File


data class Predicate(
	var fileName: String = "predicate",
	var predicateConditions: List<PredicateCondition> = mutableListOf(),
) : Generator {
	override fun generate(directory: File) {
		val json = json.encodeToString(predicateConditionsSerializer, predicateConditions)
		File(directory, "$fileName.json").writeText(json)
	}

	companion object {
		private val json = Json {
			prettyPrint = true
			serializersModule = SerializersModule {
				contextual(Advancements::class, AdvancementsJSONSerializer)
			}
		}
	}
}

fun DataPack.predicate(fileName: String, predicate: Predicate.() -> Unit): Argument.Predicate {
	predicates += Predicate(fileName).apply(predicate)
	return Argument.Predicate(fileName, name)
}
