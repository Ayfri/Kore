package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import url

suspend fun downloadLootTables() {
	val url = url("data/misc/loot_tables.txt")
	val lootTables = getFromCacheOrDownloadTxt("loot_tables.txt", url).lines()

	generateLootTablesObject(lootTables, url)
}

fun generateLootTablesObject(lootTables: List<String>, sourceUrl: String) {
	generatePathEnumTree(lootTables, "LootTables", sourceUrl, "LootTable")
}
