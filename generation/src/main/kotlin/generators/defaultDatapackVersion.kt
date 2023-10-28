package generators

import MAIN_GITHUB_URL
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import generateFile
import getFromCacheOrDownloadJson
import minecraftVersion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class VersionData(val dataPack: Int)

@OptIn(ExperimentalSerializationApi::class)
val jsonParser = Json {
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

suspend fun downloadDefaultDatapackVersion() {
	val url = "$MAIN_GITHUB_URL/master/versions_data.json"
	val versions = getFromCacheOrDownloadJson("version_data.json", url).jsonObject
	val currentVersion = jsonParser.decodeFromJsonElement<VersionData>(versions[minecraftVersion]!!.jsonObject)
	writeDefaultDatapackVersion(currentVersion.dataPack, url)
}

fun writeDefaultDatapackVersion(version: Int, sourceUrl: String) = generateFile("defaultDatapackVersion", sourceUrl) {
	addProperty(
		PropertySpec
			.builder("DEFAULT_DATAPACK_FORMAT", Int::class)
			.receiver(ClassName("io.github.ayfri.kore", "DataPack"))
			.getter(FunSpec.getterBuilder().addStatement("return $version").build())
			.build()
	)
}
