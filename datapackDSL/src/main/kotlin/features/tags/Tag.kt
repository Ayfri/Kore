package features.tags

import DataPack
import Generator
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class Tag(
	@Transient
	override var fileName: String = "tag",
	@Transient
	var type: String = "tag",
	@Transient
	var replace: Boolean = false,
	var values: List<TagEntry> = emptyList(),
) : Generator("tags") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)

	operator fun plusAssign(value: TagEntry) {
		values += value
	}

	operator fun plusAssign(value: String) {
		values += TagEntry(value)
	}

	operator fun plusAssign(value: Pair<String, Boolean>) {
		values += TagEntry(value.first, value.second)
	}

	operator fun plusAssign(value: ResourceLocationArgument) {
		values += TagEntry(value.asString())
	}

	fun add(name: String, namespace: String = "minecraft", tag: Boolean = false, required: Boolean? = null) {
		values += TagEntry("${if (tag) "#" else ""}$namespace:$name", required)
	}

	fun add(value: String, required: Boolean? = null) {
		values += TagEntry(value, required)
	}

	fun add(value: ResourceLocationArgument, required: Boolean? = null) {
		values += TagEntry(value.asString(), required)
	}
}

fun DataPack.tag(
	type: String,
	fileName: String = "tag",
	replace: Boolean = false,
	block: Tag.() -> Unit = {},
) {
	tags += Tag(fileName = fileName, type = type, replace = replace).apply(block)
}

fun DataPack.addToTag(
	type: String,
	fileName: String = "tag",
	namespace: String = "minecraft",
	block: Tag.() -> Unit = {},
) {
	val tag = tags.find {
		it.fileName == fileName && it.type == type && it.namespace == namespace
	} ?: Tag(fileName = fileName, type = type).also {
		it.namespace = namespace
		tags += it
	}

	tag.apply(block)
}
