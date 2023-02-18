package generators

import generatePathEnumTree
import getFromCacheOrDownloadJson
import kotlinx.serialization.json.jsonObject
import removeSuffix
import url

suspend fun downloadSounds() {
	val url = url("custom-generated/misc/sounds.json")
	val sounds = getFromCacheOrDownloadJson("sounds.json", url).jsonObject.keys.toList()

	generateSoundsObject(sounds, url)
}

fun generateSoundsObject(sounds: List<String>, sourceUrl: String) {
	generatePathEnumTree(sounds.removeSuffix(".ogg"), "Sounds", sourceUrl, "Sound")
}
