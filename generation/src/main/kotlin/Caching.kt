import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File

fun cacheFile(name: String, content: String) {
	val file = File(cacheDir, name)
	file.parentFile.mkdirs()
	file.writeText(content)
}

fun clearCache() {
	println("Clearing cache")
	if (cacheDir.exists()) cacheDir.deleteRecursively()
	cacheDir.mkdirs()
}

suspend fun getFromCacheOrDownloadTxt(name: String, url: String) = getFromCacheTxt(name) ?: download(name, url).body<String>().also { cacheFile(name, it) }
suspend fun getFromCacheOrDownloadJson(name: String, url: String) =
	getFromCacheJson(name) ?: download(name, url).body<JsonElement>().also { cacheFile(name, it.toString()) }

private fun getFromCacheTxt(name: String): String? {
	val file = File(cacheDir, name)
	if (file.exists()) {
		println("Getting $name from cache")
		return file.readText()
	}

	return null
}

private fun getFromCacheJson(name: String): JsonElement? {
	val file = File(cacheDir, name)
	if (file.exists()) {
		println("Getting $name from cache")
		return Json.decodeFromString(JsonElement.serializer(), file.readText())
	}

	return null
}

private suspend fun download(name: String, url: String): HttpResponse {
	println("Downloading '$name' at : $url")
	val get = client.get(url)
	if (get.status.value == 404) error("404: $url")
	return get
}
