package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.generated.DEFAULT_PACK_FORMAT
import io.github.ayfri.kore.pack.*
import kotlinx.serialization.json.*
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.name

// Debug configuration for verbose logging
internal var debugEnabled = false

private val jsonDecoder = Json { ignoreUnknownKeys = true }

fun openAsFiles(path: Path): List<Path> {
	if (!path.isDirectory()) {
		if (!path.isZipFormat()) error("File $path isn't using a supported format.")

		val zipPath = path.toAbsolutePath()
		ZipFile(zipPath.toFile()).use {
			val entries = it.entries()
			val files = mutableListOf<Path>()

			while (entries.hasMoreElements()) {
				val entry = entries.nextElement()
				if (!entry.isDirectory) {
					// Store relative path inside the ZIP so getEntry() works later
					val extractedPath = Path(entry.name)
					files.add(extractedPath)
				}
			}

			return files
		}
	}

	val files = mutableListOf<Path>()

	path.toFile().walkTopDown().forEach {
		if (it.isFile) {
			files.add(it.toPath())
		}
	}

	return files
}

fun openFile(rootPath: Path, path: Path, isZip: Boolean): String {
	if (isZip) {
		ZipFile(rootPath.toFile()).use {
			// Normalize entry name for ZIP lookup: forward slashes, no leading slash
			val entryName = path.pathInvariant
				.replace('\\', '/')
				.trimStart('/')

			val entry = it.getEntry(entryName)
			if (entry != null) {
				return it.getInputStream(entry).bufferedReader().readText()
			}

			// Try to locate entry by suffix, ignoring leading root folder and singular/plural variations
			val afterData = entryName.substringAfter("data/", entryName)
			val candidates = buildList {
				add(entryName)
				add(afterData)
			}
			val found = it.entries().asSequence().firstOrNull { e ->
				val name = e.name.replace('\\', '/')
				candidates.any { cand -> name.endsWith(cand) }
			}
			if (found != null) {
				return it.getInputStream(found).bufferedReader().readText()
			}
			throw FileNotFoundException("File $entryName was not found inside the zip.")

		}
	}

	return path.toFile().readText()
}

fun Path.matchesWithDatapackPath(path: Regex) = pathInvariant.matches(path)

fun explore(inputPath: String): Datapack {
	val input = Path(inputPath).toAbsolutePath()
	var path = input
	var isZip = input.isZipFormat()
	val displayName = input.name

	// Fallbacks when a .zip path is provided but the file does not exist
	if (isZip && !Files.exists(path)) {
		val baseName = displayName.removeSuffix(".zip")
		// 1) Try <dir>/<base>/<base>.zip
		val nestedZip = input.parent?.resolve(baseName)?.resolve("$baseName.zip")
		if (nestedZip != null && Files.exists(nestedZip)) {
			path = nestedZip
			isZip = true
		} else {
			// 2) Try sibling directory with same base name (possibly nested)
			val candidateDir = input.parent?.resolve(baseName)
			if (candidateDir != null && Files.isDirectory(candidateDir)) {
				var chosen = candidateDir
				val nested = candidateDir.resolve(baseName)
				if (Files.isDirectory(nested)) chosen = nested
				path = chosen
				isZip = false
			}
		}
	}

	val tree = openAsFiles(path)

	// Find the actual data directory root (may be nested like datapack_name/datapack_name/data/...)
	// Look for the first occurrence of /data/<namespace>/ pattern (e.g., /data/minecraft/ or /data/custom_ns/)
	// For ZIP files, paths are relative and may start without a prefix
	val dataRoot = tree.firstOrNull {
		val normalized = it.pathInvariant.replace('\\', '/')
		normalized.matches(Regex("^.*/data/[^/]+/.+")) || normalized.matches(Regex("^data/[^/]+/.+"))
	}?.let { file ->
		val normalized = file.pathInvariant.replace('\\', '/')
		val dataIndex = normalized.indexOf("/data/")
		if (dataIndex >= 0) {
            normalized.take(dataIndex)
		} else {
			// Path starts with "data/" directly (no prefix)
			""
		}
	} ?: (if (isZip) "" else path.pathInvariant)

	// Build regex patterns that handle both empty and non-empty data roots
	val dataPrefix = if (dataRoot.isEmpty()) "" else "${Regex.escape(dataRoot)}/"
	val dataFiles = tree.filter { it.matchesWithDatapackPath(Regex("^${dataPrefix}data/.+")) }
		// Filter out nested datapacks (e.g., data/minecraft/datapacks/trade_rebalance/...)
		// This is a special case in vanilla Minecraft datapacks
		.filter { file ->
			val normalized = file.pathInvariant.replace('\\', '/')
			!normalized.contains("/datapacks/")
		}

	if (debugEnabled) {
		println("[DEBUG] Data files found: ${dataFiles.size}")
	}

	// Find all function files
	val functionsFiles =
		dataFiles.filter { it.matchesWithDatapackPath(Regex("^${dataPrefix}data/.+?/function/.+\\.mcfunction$")) }

	val functions = functionsFiles.mapNotNull {
		val rootForOpen = if (isZip) path else Path(dataRoot)
		exploreFunction(rootForOpen, dataRoot, it, isZip)
	}

	// Auto-discover resource types by looking for directories like data/<namespace>/<type>/
	// Use all data files (including minecraft namespace) for resource discovery
	val discoveredTypes = discoverResourceTypes(dataFiles, dataRoot)
	// Filter to only valid resource types that have corresponding Kore Arguments
	val resourceTypes = discoveredTypes.filter { isValidResourceType(it) }

	// Debug: Print discovered types
	if (debugEnabled && discoveredTypes.isNotEmpty()) {
		println("[DEBUG] Discovered resource types: ${
			discoveredTypes.joinToString(" | ") { type ->
				val explored = exploreResources(dataFiles, dataRoot, type)
				"$type ${explored.size}"
			}
		}")
	}

	// Explore all discovered and valid resource types
	// Use all data files (including minecraft namespace) for resource exploration
	val resources = resourceTypes.associateWith { resourceType ->
		exploreResources(dataFiles, dataRoot, resourceType)
	}.filterValues { it.isNotEmpty() }

	// Parse pack.mcmeta - find the one at the root, not in subdirectories
	val packInfo = try {
		// Build the expected path for the root pack.mcmeta
		val rootPackMcMetaPattern = if (dataRoot.isEmpty()) {
			Regex("^pack\\.mcmeta$")
		} else {
			Regex("^${Regex.escape(dataRoot)}/pack\\.mcmeta$")
		}

		val packMcMetaPath = tree.firstOrNull {
			it.pathInvariant.replace('\\', '/').matches(rootPackMcMetaPattern)
		}

		if (packMcMetaPath != null) {
			val rootForOpen = if (isZip) path else if (dataRoot.isEmpty()) path else Path(dataRoot)
			val parsedPack = parsePackMcMeta(rootForOpen, packMcMetaPath, isZip)

			// Check pack format compatibility
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

	return Datapack(displayName, path, functions = functions, resources = resources, pack = packInfo)
}

fun discoverResourceTypes(dataFiles: List<Path>, dataRootString: String): List<String> {
	val resourceTypes = mutableSetOf<String>()

	for (file in dataFiles) {
		if (!file.toString().endsWith(".json")) continue

		val pathStr = file.pathInvariant.replace('\\', '/')

		// Remove data root prefix if present
		val relativePath = if (dataRootString.isNotEmpty()) {
			val prefix = dataRootString.replace('\\', '/') + "/"
			if (pathStr.startsWith(prefix)) pathStr.removePrefix(prefix) else pathStr
		} else {
			pathStr
		}

		// Expected structure: data/<namespace>/<type>/...  or data/<namespace>/<type>/<subtype>/...
		if (!relativePath.startsWith("data/")) continue

		val parts = relativePath.split("/")
		if (parts.size < 4) continue // Need at least: data, namespace, type, file

		parts[1]
		val typeOrCategory = parts[2]

		// Skip special directories
		if (typeOrCategory in listOf("pack.mcmeta", "function", "structures")) continue

		when (typeOrCategory) {
			"worldgen" -> {
				// Structure: data/<namespace>/worldgen/<subtype>/<name>.json
				if (parts.size >= 5) {
					val subtype = parts[3]
					resourceTypes.add("worldgen/$subtype")
				}
			}
			"tags" -> {
				// Structure: data/<namespace>/tags/<subtype>/<name>.json
				// or data/<namespace>/tags/worldgen/<subtype>/<name>.json
				if (parts.size >= 5) {
					val subtype = parts[3]
					if (subtype == "worldgen" && parts.size >= 6) {
						// tags/worldgen/<subtype>
						val worldgenSubtype = parts[4]
						resourceTypes.add("tags/worldgen/$worldgenSubtype")
					} else {
						// tags/<subtype>
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

fun exploreFunction(rootPath: Path, dataRootString: String, functionFile: Path, isZip: Boolean): Function? {
	val content = openFile(rootPath, functionFile, isZip)
	val dataPrefix = if (dataRootString.isEmpty()) "" else "${Regex.escape(dataRootString)}/"
	val path =
		Regex("^${dataPrefix}data/.+?/function/(.+)\\.mcfunction\$").find(functionFile.pathInvariant)?.groupValues?.get(
			1
		) ?: run {
			warn("Function file $functionFile doesn't match the expected pattern - skipping")
			return null
		}
	// For namespace extraction, match only up to the first directory after data/
	// This ensures we only capture the actual namespace, not nested paths
	val namespace =
		Regex("^${dataPrefix}data/([^/\\\\]+)/function/.+\\.mcfunction\$").find(functionFile.pathInvariant)?.let {
			it.groupValues[1]
		} ?: run {
			warn("Could not extract namespace from function $functionFile - skipping")
			return null
		}

	// Validate that namespace doesn't contain invalid characters (should be impossible now with [^/\\\\]+ pattern)
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

fun exploreResources(dataFiles: List<Path>, dataRootString: String, resourceType: String): List<Resource> {
	return dataFiles.mapNotNull { file ->
		val pathStr = file.pathInvariant.replace('\\', '/')

		// Remove data root prefix if present
		val relativePath = if (dataRootString.isNotEmpty()) {
			val prefix = dataRootString.replace('\\', '/') + "/"
			if (pathStr.startsWith(prefix)) pathStr.removePrefix(prefix) else pathStr
		} else {
			pathStr
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

fun parsePackMcMeta(rootPath: Path, packMcMetaFile: Path, isZip: Boolean): PackMCMeta? = try {
	val content = openFile(rootPath, packMcMetaFile, isZip)
	val json = jsonDecoder.parseToJsonElement(content).jsonObject
	val packObj = json["pack"]?.jsonObject ?: return null

	val description = packObj["description"]?.let { desc ->
		// Parse description as plain text for simplicity
		when (desc) {
			is JsonPrimitive -> textComponent(desc.content)
			else -> textComponent(desc.toString())
		}
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
	e.printStackTrace()
	null
}

/**
 * Checks if the imported datapack is compatible with the current project's pack format.
 * Uses the same logic as DataPack.isCompatibleWith().
 */
fun checkPackCompatibility(importedPack: PackMCMeta): Boolean {
	val currentFormat = DataPack.DEFAULT_DATAPACK_FORMAT

	if (importedPack.pack.format != currentFormat) {
		val packFormatPrint = "Format: current: $currentFormat imported: ${importedPack.pack.format}."

		if (importedPack.pack.supportedFormats != null) {
			// Check if the current format is in the supported formats range
			if (importedPack.pack.supportedFormats!!.isInRange(currentFormat)) {
				return true
			}

			println("[WARNING]: The pack format of the imported datapack is different from the current one and not in supported range. This may cause issues.")
			println("[WARNING]: $packFormatPrint")
			println("[WARNING]: Supported Formats: ${importedPack.pack.supportedFormats}.")
			return false
		}

		println("[WARNING]: The pack format of the imported datapack is different from the current one. This may cause issues.")
		println("[WARNING]: $packFormatPrint")
		return false
	}

	return true
}
