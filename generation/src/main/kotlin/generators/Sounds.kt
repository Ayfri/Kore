package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import removeSuffix
import url

suspend fun downloadSounds() {
	val url = url("custom-generated/lists/sounds.txt")
	val sounds = getFromCacheOrDownloadTxt("sounds.txt", url).lines()

	generateSoundsObject(sounds, url)
}

fun generateSoundsObject(sounds: List<String>, sourceUrl: String) {
	generatePathEnumTree(sounds.removeSuffix(".ogg"), "Sounds", sourceUrl, "Sound")
}
