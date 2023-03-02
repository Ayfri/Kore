package features.tags

import DataPack
import Generator
import java.io.File

data class Tags(
	val namespace: String,
	val tags: MutableList<Pair<String, Tag>> = mutableListOf()
) : Generator {
	fun add(type: String, name: String, replace: Boolean = false, block: Tag.() -> Unit): Pair<String, Tag> {
		val tag = type to Tag(name, replace).apply(block)
		tags += tag
		return tag
	}

	fun addTo(type: String, name: String, block: Tag.() -> Unit) {
		val tag = tags.find { it.first == type && it.second.name == name }
		when {
			tag != null -> tag.second.apply(block)
			else -> add(type, name, block = block)
		}
	}

	fun tag(type: String, name: String, replace: Boolean = false, block: Tag.() -> Unit) = add(type, name, replace, block)

	override fun generate(directory: File) = tags.forEach { (type, tag) ->
		tag.generate(File(directory, type))
	}
}

fun DataPack.tags(namespace: String = this.name, block: Tags.() -> Unit = {}): Tags {
	val newTags = Tags(namespace).apply(block)
	tags += newTags
	return newTags
}

fun DataPack.addToTag(namespace: String, type: String, name: String, block: Tag.() -> Unit = {}) {
	(tags.find { it.namespace == namespace } ?: tags(namespace)).addTo(type, name, block)
}
