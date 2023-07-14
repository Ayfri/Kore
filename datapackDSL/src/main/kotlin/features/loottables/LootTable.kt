package features.loottables

import DataPack
import Generator
import arguments.Argument
import arguments.selector.Advancements
import features.advancements.types.AdvancementsJSONSerializer
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class LootTable(
	@Transient
	override var fileName: String = "loot_table",
	var functions: ItemModifierAsList? = null,
	var pools: List<LootPool>? = null,
	var randomSequence: Argument.RandomSequence? = null,
) : Generator {
	@Transient
	private lateinit var json: Json

	override fun generateJson(dataPack: DataPack) = getJsonEncoder(dataPack).encodeToString(this)

	@OptIn(ExperimentalSerializationApi::class)
	private fun getJsonEncoder(dataPack: DataPack) = when {
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

fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

fun LootTable.pool(rolls: NumberProvider = constant(1f), block: LootPool.() -> Unit = {}) {
	pools = (pools ?: emptyList()) + LootPool(rolls).apply(block)
}
