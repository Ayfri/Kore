package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadEffects() {
	val url = url("data/registries/mob_effect.txt")
	val effects = getFromCacheOrDownloadTxt("effects.txt", url).lines()

	generateEffectsEnum(effects, url)
}

fun generateEffectsEnum(effects: List<String>, sourceUrl: String) {
	generateEnum(effects, "Effects", sourceUrl, "MobEffect")
}
