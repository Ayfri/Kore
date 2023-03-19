package generators

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadDamageTypes() {
	val url = url("custom-generated/lists/damage_types.txt")
	val damageTypes = getFromCacheOrDownloadTxt("damage_types.txt", url).lines()

	generateDamageTypesEnum(damageTypes, url)
}

fun generateDamageTypesEnum(damageTypes: List<String>, sourceUrl: String) {
	generateEnum(damageTypes.removeJSONSuffix(), "DamageTypes", sourceUrl, "DamageType")
}
