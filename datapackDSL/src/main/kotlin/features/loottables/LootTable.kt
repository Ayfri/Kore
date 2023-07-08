package features.loottables

import DataPack
import Generator
import arguments.Argument
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
		File(directory, "$fileName.json").writeText(getJsonEncoder(dataPack).encodeToString(this))
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

fun DataPack.lootTable(fileName: String, block: LootTable.() -> Unit = {}): Argument.LootTable {
	val lootTable = LootTable(fileName).apply(block)
	lootTables += lootTable
	return Argument.LootTable(fileName, name)
}

fun LootTable.function(function: String) {
	functions = (functions ?: emptyList()) + function
}

fun LootTable.pool(rolls: NumberProvider = constant(1f), block: LootPool.() -> Unit = {}) {
	pools = (pools ?: emptyList()) + LootPool(rolls).apply(block)
}
