package features.tags

import DataPack
import Generator
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import java.io.File

@Serializable
data class Tag(
	@Transient
	val name: String = "tag",
	val replace: Boolean = false,
	val values: MutableList<@Serializable(TagEntry.Companion.TagEntrySerializer::class) TagEntry> = mutableListOf(),
) : Generator {
	operator fun plusAssign(value: TagEntry) {
		values += value
	}

	operator fun plusAssign(value: String) {
		values += TagEntry(value)
	}

	operator fun plusAssign(value: Pair<String, Boolean>) {
		values += TagEntry(value.first, value.second)
	}

	fun addTag(name: String, required: Boolean? = null) {
		values += TagEntry("#$name", required)
	}

	fun add(name: String, namespace: String = "minecraft", group: Boolean = false, required: Boolean? = null) {
		values += TagEntry("${if (group) "#" else ""}$namespace:$name", required)
	}

	override fun generate(dataPack: DataPack, directory: File) {
		val file = File(directory, "$name.json")
		file.parentFile.mkdirs()
		file.writeText(dataPack.jsonEncoder.encodeToString(this))
	}
}
