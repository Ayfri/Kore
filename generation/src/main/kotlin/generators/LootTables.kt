package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadLootTables() {
	val url = url("custom-generated/lists/loot_tables.txt")
	val lootTables = getFromCacheOrDownloadTxt("loot_tables.txt", url).lines()

	generateLootTablesObject(lootTables, url)
}

fun generateLootTablesObject(lootTables: List<String>, sourceUrl: String) {
	generatePathEnumTree(lootTables.removeJSONSuffix(), "LootTables", sourceUrl, "LootTable")
}
