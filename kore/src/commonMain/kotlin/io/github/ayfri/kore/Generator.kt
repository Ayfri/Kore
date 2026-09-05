package io.github.ayfri.kore

import io.github.ayfri.kore.generation.fabric.conditions.FABRIC_LOAD_CONDITIONS_KEY
import io.github.ayfri.kore.generation.fabric.conditions.ResourceCondition
import io.github.ayfri.kore.utils.resolve
import io.github.ayfri.kore.utils.warn
import kotlinx.io.files.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonObject

/**
* Base class for all generators.
*
* A generator is a class that can generate a JSON file.
*
* @param resourceFolder the folder where the generated file is stored
*/
@Serializable
abstract class Generator(@Transient val resourceFolder: String = "") {
	@Transient
	var namespace: String? = null

	/**
	 * Fabric API resource conditions injected as a [FABRIC_LOAD_CONDITIONS_KEY] array at the JSON root during
	 * generation. Set via [io.github.ayfri.kore.generation.fabric.conditions.fabricLoadConditions].
	 */
	@Transient
	var fabricLoadConditions: List<ResourceCondition> = emptyList()

	abstract var fileName: String

	/** Generates the JSON content for the generator. */
	abstract fun generateJson(dataPack: DataPack): String

	/**
	 * Generates the JSON content, wrapping it with the Fabric [FABRIC_LOAD_CONDITIONS_KEY] array when [loadConditions]
	 * is set. Conditions are only applied when the generated JSON root is an object; array-form resources (multi-entry
	 * predicates/item modifiers) emit a warning and are left untouched.
	 */
	@OptIn(ExperimentalSerializationApi::class)
	fun generateJsonWithLoadConditions(dataPack: DataPack): String {
		val json = generateJson(dataPack)
		if (fabricLoadConditions.isEmpty()) return json

		val element = dataPack.jsonEncoder.parseToJsonElement(json)
		if (element !is JsonObject) {
			warn(
				"Cannot attach Fabric load conditions to '${namespace ?: dataPack.name}:$fileName' ($resourceFolder): " +
					"its JSON root is not an object, so conditions are ignored."
			)
			return json
		}

		val conditions =
			dataPack.jsonEncoder.encodeToJsonElement(ListSerializer(ResourceCondition.serializer()), fabricLoadConditions)
		val merged = JsonObject(mapOf(FABRIC_LOAD_CONDITIONS_KEY to conditions) + element)
		return dataPack.jsonEncoder.encodeToString(JsonObject.serializer(), merged)
	}

	/** Returns the final path of the generated file, relative to the datapack output directory. */
	fun getFinalPath(dataPack: DataPack): Path {
		val dataFolder = dataPack.cleanPath.resolve(dataPack.folderName ?: dataPack.name, "data")
		val namespace = namespace ?: dataPack.name
		return getPathFromDataDir(dataFolder, namespace)
	}

	/** Returns the path of the generated file, relative to the datapack output directory, is open because some generators have a different path. */
	open fun getPathFromDataDir(dir: Path, namespace: String): Path = dir.resolve(namespace, resourceFolder, "$fileName.json")
}
