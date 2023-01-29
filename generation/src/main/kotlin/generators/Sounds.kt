package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import url

suspend fun downloadSounds() {
	val url = url("data/misc/sounds.txt")
	val sounds = getFromCacheOrDownloadTxt("sounds.txt", url).lines()

	generateSoundsObject(sounds, url)
}

fun generateSoundsObject(sounds: List<String>, sourceUrl: String) {
	generatePathEnumTree(sounds, "Sounds", sourceUrl, "Sound")
}
