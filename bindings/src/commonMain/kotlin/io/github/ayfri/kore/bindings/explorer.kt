package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.generated.DEFAULT_PACK_FORMAT
import io.github.ayfri.kore.pack.*
import kotlinx.io.files.Path
import kotlinx.serialization.json.*

// Debug configuration for verbose logging
internal var debugEnabled = false

private val jsonDecoder = Json { ignoreUnknownKeys = true }

/**
 * Explores an already in-memory datapack (e.g. an uploaded/downloaded zip decoded via [readZipDatapack])
 * and builds its [Datapack] model - functions, resources, and pack metadata.
 */
fun explore(datapack: InMemoryDatapack, displayName: String, displayPath: Path): Datapack {
	val tree = datapack.files.keys.toList()

	// Find the actual data directory root (may be nested like datapack_name/datapack_name/data/...)
	// Look for the first occurrence of /data/<namespace>/ pattern (e.g., /data/minecraft/ or /data/custom_ns/)
	val dataRoot = tree.firstOrNull {
		it.matches(Regex("^.*/data/[^/]+/.+")) || it.matches(Regex("^data/[^/]+/.+"))
	}?.let { file ->
		val dataIndex = file.indexOf("/data/")
		if (dataIndex >= 0) file.take(dataIndex) else ""
	} ?: ""

	// Build regex patterns that handle both empty and non-empty data roots
	val dataPrefix = if (dataRoot.isEmpty()) "" else "${Regex.escape(dataRoot)}/"
	val dataFiles = tree.filter { it.matches(Regex("^${dataPrefix}data/.+")) }
		// Filter out nested datapacks (e.g., data/minecraft/datapacks/trade_rebalance/...)
		// This is a special case in vanilla Minecraft datapacks
		.filter { !it.contains("/datapacks/") }

	if (debugEnabled) {
		println("[DEBUG] Data files found: ${dataFiles.size}")
	}

	// Find all function files
	val functionsFiles = dataFiles.filter { it.matches(Regex("^${dataPrefix}data/.+?/function/.+\\.mcfunction$")) }
	val functions = functionsFiles.mapNotNull { exploreFunction(datapack, dataRoot, it) }

	// Auto-discover resource types by looking for directories like data/<namespace>/<type>/
	val discoveredTypes = discoverResourceTypes(dataFiles, dataRoot)
	// Filter to only valid resource types that have corresponding Kore Arguments
	val resourceTypes = discoveredTypes.filter { isValidResourceType(it) }

	if (debugEnabled && discoveredTypes.isNotEmpty()) {
		println("[DEBUG] Discovered resource types: ${
			discoveredTypes.joinToString(" | ") { type ->
				"$type ${exploreResources(dataFiles, dataRoot, type).size}"
			}
		}")
	}

	// Explore all discovered and valid resource types
	val resources = resourceTypes.associateWith { resourceType ->
		exploreResources(dataFiles, dataRoot, resourceType)
	}.filterValues { it.isNotEmpty() }

	// Parse pack.mcmeta - find the one at the root, not in subdirectories
	val packInfo = try {
		val rootPackMcMetaPattern = if (dataRoot.isEmpty()) {
			Regex("^pack\\.mcmeta$")
		} else {
			Regex("^${Regex.escape(dataRoot)}/pack\\.mcmeta$")
		}

		val packMcMetaPath = tree.firstOrNull { it.matches(rootPackMcMetaPattern) }

		if (packMcMetaPath != null) {
			val parsedPack = parsePackMcMeta(datapack, packMcMetaPath)

			if (parsedPack != null) {
				checkPackCompatibility(parsedPack)
			}

			parsedPack
		} else {
			warn("pack.mcmeta not found at root")
			null
		}
	} catch (e: Exception) {
		warn("Could not parse pack.mcmeta: ${e.message}")
		null
	}

	return Datapack(displayName, displayPath, functions = functions, resources = resources, pack = packInfo)
}

fun discoverResourceTypes(dataFiles: List<String>, dataRootString: String): List<String> {
	val resourceTypes = mutableSetOf<String>()

	for (file in dataFiles) {
		if (!file.endsWith(".json")) continue

		// Remove data root prefix if present
		val relativePath = if (dataRootString.isNotEmpty()) {
			val prefix = "$dataRootString/"
			if (file.startsWith(prefix)) file.removePrefix(prefix) else file
		} else {
			file
		}

		// Expected structure: data/<namespace>/<type>/...  or data/<namespace>/<type>/<subtype>/...
		if (!relativePath.startsWith("data/")) continue

		val parts = relativePath.split("/")
		if (parts.size < 4) continue // Need at least: data, namespace, type, file

		val typeOrCategory = parts[2]

		// Skip special directories
		if (typeOrCategory in listOf("pack.mcmeta", "function", "structures")) continue

		when (typeOrCategory) {
			"worldgen" -> {
				// Structure: data/<namespace>/worldgen/<subtype>/<name>.json
				if (parts.size >= 5) {
					resourceTypes.add("worldgen/${parts[3]}")
				}
			}
			"tags" -> {
				// Structure: data/<namespace>/tags/<subtype>/<name>.json
				// or data/<namespace>/tags/worldgen/<subtype>/<name>.json
				if (parts.size >= 5) {
					val subtype = parts[3]
					if (subtype == "worldgen" && parts.size >= 6) {
						resourceTypes.add("tags/worldgen/${parts[4]}")
					} else {
						resourceTypes.add("tags/$subtype")
					}
				}
			}
			else -> {
				// Regular resource type
				resourceTypes.add(typeOrCategory)
			}
		}
	}

	return resourceTypes.sorted()
}

fun exploreFunction(datapack: InMemoryDatapack, dataRootString: String, functionFile: String): Function? {
	val content = datapack.files[functionFile] ?: return null
	val dataPrefix = if (dataRootString.isEmpty()) "" else "${Regex.escape(dataRootString)}/"

	val path = Regex("^${dataPrefix}data/.+?/function/(.+)\\.mcfunction$").find(functionFile)?.groupValues?.get(1)
		?: run {
			warn("Function file $functionFile doesn't match the expected pattern - skipping")
			return null
		}

	// For namespace extraction, match only up to the first directory after data/
	// This ensures we only capture the actual namespace, not nested paths
	val namespace = Regex("^${dataPrefix}data/([^/\\\\]+)/function/.+\\.mcfunction$").find(functionFile)?.let {
		it.groupValues[1]
	} ?: run {
		warn("Could not extract namespace from function $functionFile - skipping")
		return null
	}

	// Validate that namespace doesn't contain invalid characters (should be impossible now with [^/\\]+ pattern)
	if ('/' in namespace || '\\' in namespace) {
		warn("Invalid namespace '$namespace' extracted from $functionFile - skipping")
		return null
	}

	// a macro is detected as a line starting with $ and containing $(.+?).
	// Extract all variable names from $(variable_name) patterns
	val macroArguments = content.lines()
		.filter { it.startsWith("$") }
		.flatMap { line ->
			Regex("\\$\\(([^)]+)\\)").findAll(line).map { it.groupValues[1] }
		}
		.distinct()
		.toList()

	return Function("$namespace:$path", macroArguments)
}

fun exploreResources(dataFiles: List<String>, dataRootString: String, resourceType: String): List<Resource> {
	return dataFiles.mapNotNull { file ->
		// Remove data root prefix if present
		val relativePath = if (dataRootString.isNotEmpty()) {
			val prefix = "$dataRootString/"
			if (file.startsWith(prefix)) file.removePrefix(prefix) else file
		} else {
			file
		}

		// Expected structure: data/<namespace>/<type>/...
		if (!relativePath.startsWith("data/") || !relativePath.endsWith(".json")) {
			return@mapNotNull null
		}

		val parts = relativePath.split("/")
		if (parts.size < 4) return@mapNotNull null // Need at least: data, namespace, type, file

		val namespace = parts[1]

		// Build the expected path for this resource type
		val typeParts = resourceType.split("/")
		val expectedPathPrefix = when (typeParts.size) {
			1 -> "data/$namespace/${typeParts[0]}/"
			2 -> "data/$namespace/${typeParts[0]}/${typeParts[1]}/"
			3 -> "data/$namespace/${typeParts[0]}/${typeParts[1]}/${typeParts[2]}/"
			else -> return@mapNotNull null
		}

		if (!relativePath.startsWith(expectedPathPrefix)) {
			return@mapNotNull null
		}

		// Extract the resource path (everything after the type prefix, without .json)
		val resourcePath = relativePath
			.removePrefix(expectedPathPrefix)
			.removeSuffix(".json")

		if (resourcePath.isEmpty()) return@mapNotNull null

		Resource("$namespace:$resourcePath", resourceType)
	}
}

fun parsePackMcMeta(datapack: InMemoryDatapack, packMcMetaFile: String): PackMCMeta? = try {
	val content = datapack.files[packMcMetaFile] ?: return null
	val json = jsonDecoder.parseToJsonElement(content).jsonObject
	val packObj = json["pack"]?.jsonObject ?: return null

	val description = packObj["description"]?.let {
		jsonDecoder.decodeFromJsonElement(ChatComponents.serializer(), it)
	} ?: textComponent("Imported datapack")

	val minFormat = packObj["min_format"]?.let { jsonDecoder.decodeFromJsonElement<PackFormat>(it) } ?: return null
	val maxFormat = packObj["max_format"]?.let { jsonDecoder.decodeFromJsonElement<PackFormat>(it) } ?: return null
	val packFormat = packObj["pack_format"]?.let { jsonDecoder.decodeFromJsonElement<PackFormat>(it) }
	val supportedFormats = packObj["supported_formats"]?.let { jsonDecoder.decodeFromJsonElement<SupportedFormats>(it) }

	val overlays = json["overlays"]?.jsonObject?.let { overlaysObj ->
		val entries = overlaysObj["entries"]?.jsonArray?.mapNotNull { entry ->
			val entryObj = entry.jsonObject
			val directory = entryObj["directory"]?.jsonPrimitive?.content ?: return@mapNotNull null
			val entryMin =
				entryObj["min_format"]?.let { jsonDecoder.decodeFromJsonElement<PackFormat>(it) } ?: return@mapNotNull null
			val entryMax =
				entryObj["max_format"]?.let { jsonDecoder.decodeFromJsonElement<PackFormat>(it) } ?: return@mapNotNull null
			val entryFormats = entryObj["formats"]?.let { jsonDecoder.decodeFromJsonElement<SupportedFormats>(it) }
			PackOverlayEntry(directory, entryMin, entryMax, entryFormats)
		} ?: emptyList()
		entries.takeIf { it.isNotEmpty() }?.let { PackOverlays(it) }
	}

	val pack = PackSection(description, minFormat, maxFormat, packFormat, supportedFormats)
	PackMCMeta(pack, overlays)
} catch (e: Exception) {
	warn("Error parsing pack.mcmeta")
	null
}

/**
 * Checks if the imported datapack is compatible with the current project's pack format.
 * Uses the same logic as DataPack.isCompatibleWith().
 */
fun checkPackCompatibility(importedPack: PackMCMeta): Boolean {
	val currentPack = PackSection(
		description = textComponent("Current pack"),
		minFormat = DataPack.DEFAULT_PACK_FORMAT,
		maxFormat = DataPack.DEFAULT_PACK_FORMAT,
	)

	if (importedPack.pack.isCompatibleWith(currentPack)) return true

	val packFormatPrint =
		"Format range: current: ${currentPack.minFormat.asFormatString()}..${currentPack.maxFormat.asFormatString()} imported: ${importedPack.pack.minFormat.asFormatString()}..${importedPack.pack.maxFormat.asFormatString()}."
	warn("The pack format range of the imported datapack is different from the current one. This may cause issues.")
	warn(packFormatPrint)
	return false
}
