package features.tags

import DataPack
import Generator

data class Tags(
	override var fileName: String = "tags",
	val namespace: String,
	val tags: MutableList<Pair<String, Tag>> = mutableListOf()
) : Generator {
	fun add(type: String, name: String, replace: Boolean = false, block: Tag.() -> Unit): Pair<String, Tag> {
		val tag = type to Tag(name, replace).apply(block)
		tags += tag
		return tag
	}

	fun addTo(type: String, name: String, block: Tag.() -> Unit) {
		val tag = tags.find { it.first == type && it.second.fileName == name }
		when {
			tag != null -> tag.second.apply(block)
			else -> add(type, name, block = block)
		}
	}

	fun tag(type: String, name: String, replace: Boolean = false, block: Tag.() -> Unit) = add(type, name, replace, block)

	// TODO : Refactor tags to use Generator syntax
	override fun generateJson(dataPack: DataPack) = tags.forEach { (type, tag) ->
		tag.generateJson(dataPack)
	}.let { "" }
}

fun DataPack.tags(namespace: String = name, block: Tags.() -> Unit = {}): Tags {
	val newTags = Tags(namespace = namespace).apply(block)
	tags += newTags
	return newTags
}

fun DataPack.addToTag(namespace: String, type: String, name: String, block: Tag.() -> Unit = {}) {
	(tags.find { it.namespace == namespace } ?: tags(namespace)).addTo(type, name, block)
}
