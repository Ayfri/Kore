package generators

import MAIN_GITHUB_URL
import cacheDir
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import generateFile
import getFromCacheOrDownloadJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import minecraftVersion
import java.io.File

@Serializable
data class PackVersion(
	val major: Int,
	val minor: Int? = null,
)

@Serializable
data class VersionData(
	val dataPack: PackVersion? = null,
	val dataPackVersion: Int? = null,
)

@OptIn(ExperimentalSerializationApi::class)
val jsonParser = Json {
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

suspend fun downloadDefaultDatapackVersion() {
	val url = "$MAIN_GITHUB_URL/master/versions_data.json"
	val versions = getFromCacheOrDownloadJson("version_data.json", url).jsonObject
	if (!versions.containsKey(minecraftVersion)) {
		File(cacheDir, "version_data.json").delete()
		downloadDefaultDatapackVersion()
		return
	}
	val currentVersion = jsonParser.decodeFromJsonElement<VersionData>(versions[minecraftVersion]!!.jsonObject)
	val packVersion = currentVersion.dataPack ?: PackVersion(
		major = currentVersion.dataPackVersion ?: error("No data_pack version found for $minecraftVersion")
	)
	writeDefaultDatapackVersion(packVersion, url)
}

fun writeDefaultDatapackVersion(packVersion: PackVersion, sourceUrl: String) = generateFile("defaultDatapackVersion", sourceUrl) {
	val dataPackClassName = ClassName("io.github.ayfri.kore", "DataPack")
	val packFormatClassName = ClassName("io.github.ayfri.kore.pack", "PackFormat")
	val packFormatFn = MemberName("io.github.ayfri.kore.pack", "packFormat")

	// Deprecated variable for backwards compatibility
	addProperty(
		PropertySpec
			.builder("DEFAULT_DATAPACK_FORMAT", Int::class)
			.receiver(dataPackClassName.nestedClass("Companion"))
			.addAnnotation(
				ClassName("kotlin", "Deprecated")
					.let {
						com.squareup.kotlinpoet.AnnotationSpec.builder(it)
							.addMember("message = %S", "Use DEFAULT_PACK_FORMAT instead")
							.addMember("replaceWith = %L", "ReplaceWith(\"DEFAULT_PACK_FORMAT.major\")")
							.build()
					}
			)
			.getter(FunSpec.getterBuilder().addStatement("return ${packVersion.major}").build())
			.build()
	)

	// New variable with full PackFormat support
	addProperty(
		PropertySpec
			.builder("DEFAULT_PACK_FORMAT", packFormatClassName)
			.receiver(dataPackClassName.nestedClass("Companion"))
			.getter(
				FunSpec.getterBuilder()
					.addStatement(
						"return %M(${packVersion.major}${packVersion.minor?.let { ", $it" } ?: ""})",
						packFormatFn
					)
					.build()
			)
			.build()
	)
}
