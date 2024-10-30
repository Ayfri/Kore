package io.github.ayfri.kore.features.tags

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions

@Serializable(with = Tag.Companion.TagSerializer::class)
data class Tag<out T : TaggedResourceLocationArgument>(
	@Transient
	override var fileName: String = "tag",
	@Transient
	var type: String = "",
	var replace: Boolean = false,
	var values: List<TagEntry> = emptyList(),
) : Generator("tags") {

	@Transient
	@PublishedApi
	internal var tagClass: KClass<out TaggedResourceLocationArgument> = TaggedResourceLocationArgument::class

	@PublishedApi
	internal val invokeFunction get() = tagClass.companionObject!!.memberFunctions.first { it.name == "invoke" }

	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(TagSerializer, this)

	override fun getFinalPath(dataPack: DataPack): Path {
		val dataFolder = Path(dataPack.path.toString(), dataPack.name, "data")
		return Path(dataFolder.toString(), namespace ?: dataPack.name, resourceFolder, type, "$fileName.json")
	}

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

	fun add(
		name: String,
		namespace: String = "minecraft",
		tag: Boolean = false,
		required: Boolean? = null,
	) {
		values += TagEntry("${if (tag) "#" else ""}$namespace:$name", required)
	}

	fun add(value: String, required: Boolean? = null) {
		values += TagEntry(value, required)
	}

	fun add(value: ResourceLocationArgument, required: Boolean? = null) {
		values += TagEntry(value.asString(), required)
	}

	companion object {
		data object TagSerializer : KSerializer<Tag<*>> {
			override val descriptor = buildClassSerialDescriptor("Tag") {
				element<Boolean>("replace")
				element<List<TagEntry>>("values")
			}

			override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
				var replace = false
				var values = emptyList<TagEntry>()

				while (true) {
					when (val index = decodeElementIndex(descriptor)) {
						0 -> replace = decodeBooleanElement(descriptor, 0)
						1 -> values = decodeSerializableElement(descriptor, 1, ListSerializer(TagEntry.serializer()))
						CompositeDecoder.DECODE_DONE -> break
						else -> error("Unexpected index: $index")
					}
				}

				Tag<TaggedResourceLocationArgument>(replace = replace, values = values)
			}

			override fun serialize(encoder: Encoder, value: Tag<*>) = encoder.encodeStructure(descriptor) {
				encodeBooleanElement(descriptor, 0, value.replace)
				encodeSerializableElement(descriptor, 1, ListSerializer(TagEntry.serializer()), value.values)
			}
		}
	}
}

inline fun <reified T : TaggedResourceLocationArgument> DataPack.tag(
	fileName: String = "tag",
	type: String = "",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<T>.() -> Unit = {},
): T {
	val tag = Tag<T>(fileName = fileName, type = type, replace = replace).apply {
		this.namespace = namespace
		tagClass = T::class
		block()
	}
	tags += tag
	return tag.invokeFunction.call(tag.tagClass.companionObjectInstance, fileName, namespace) as T
}

@JvmName("tagUntyped")
inline fun DataPack.tag(
	fileName: String = "tag",
	type: String = "",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<TaggedResourceLocationArgument>.() -> Unit = {},
) = tag<TaggedResourceLocationArgument>(fileName, type, namespace, replace, block)

inline fun <reified T : TaggedResourceLocationArgument> DataPack.addToTag(
	fileName: String = "tag",
	type: String = "",
	namespace: String = name,
	block: Tag<T>.() -> Unit = {},
): T {
	val tag = tags.find {
		it.fileName == fileName && it.type == type && it.namespace == namespace && it.tagClass == T::class
	} as Tag<T>? ?: Tag<T>(fileName = fileName, type = type).also {
		it.namespace = namespace
		it.tagClass = T::class
		tags += it
	}

	tag.apply(block)
	return tag.invokeFunction.call(tag.tagClass.companionObjectInstance, fileName, namespace) as T
}

@JvmName("addToTagUntyped")
inline fun DataPack.addToTag(
	fileName: String = "tag",
	type: String = "",
	namespace: String = name,
	block: Tag<TaggedResourceLocationArgument>.() -> Unit = {},
) = addToTag<TaggedResourceLocationArgument>(fileName, type, namespace, block)
