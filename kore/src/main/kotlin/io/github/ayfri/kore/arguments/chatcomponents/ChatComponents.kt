package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.serializerFor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*

@Serializable(with = ChatComponents.Companion.ChatComponentsSerializer::class)
data class ChatComponents(
	val list: MutableList<ChatComponent> = mutableListOf(),
	var onlySimpleComponents: Boolean = false,
) : Iterable<ChatComponent>, Argument {
	constructor(vararg components: ChatComponent) : this(components.toMutableList())

	val containsOnlySimpleComponents get() = list.all { it is SimpleComponent }

	init {
		if (onlySimpleComponents) requireSimpleComponents()
	}

	operator fun plus(textComponent: ChatComponent): ChatComponents {
		if (onlySimpleComponents) throw ONLY_SIMPLE_COMPONENTS_EXCEPTION
		list += textComponent
		return this
	}

	operator fun plus(chatComponents: ChatComponents): ChatComponents {
		if (onlySimpleComponents) {
			throw ONLY_SIMPLE_COMPONENTS_EXCEPTION
		}
		list += chatComponents.list
		return this
	}

	override fun iterator() = list.iterator()

	fun requireSimpleComponents() {
		if (!containsOnlySimpleComponents) throw ONLY_SIMPLE_COMPONENTS_EXCEPTION
	}

	fun toJsonString(json: Json = Json) = json.encodeToString(NbtAsJsonSerializer, toNbtTag())
	fun toJsonListString(json: Json = Json) = json.encodeToString(NbtAsJsonSerializer, toNbtList())

	fun toNbtList(): NbtTag = if (list.all { it.containsOnlyText() }) {
		nbtListOf(list.map { it.text })
	} else {
		nbtListOf(list.map { it.toNbtTag() })
	}

	fun toNbtTag(): NbtTag = when (list.size) {
		0 -> NbtString("")
		1 -> list[0].toNbt()
		else -> toNbtList()
	}

	/** Collapses these components into a single [ChatComponent]: the first is the root, the rest become its `extra` siblings. */
	fun toComponent(): ChatComponent = when (list.size) {
		0 -> text()
		1 -> list[0]
		else -> list[0].apply { extra = ChatComponents(list.drop(1).toMutableList()) }
	}

	override fun asString() = StringifiedNbt.encodeToString(toNbtTag())

	fun asJsonArg() = literal(toJsonString())

	fun asSnbtArg() = literal(asString())

	companion object {
		val ONLY_SIMPLE_COMPONENTS_EXCEPTION =
			IllegalArgumentException("This ChatComponent should only contain simple components.")

		@OptIn(ExperimentalSerializationApi::class)
		val jsonSerializer = Json {
			ignoreUnknownKeys = true
			encodeDefaults = false
			namingStrategy = JsonNamingStrategy.SnakeCase
		}

		/* Encodes a list of components as a serialized collection, each via [ChatComponentSerializer]. */
		private fun Encoder.encodeComponents(descriptor: SerialDescriptor, components: List<ChatComponent>) =
			encodeCollection(descriptor, components.size) {
				components.forEachIndexed { i, component ->
					encodeSerializableElement(descriptor, i, ChatComponentSerializer, component)
				}
			}

		/* Per-type serializers, keyed by the `type` discriminator Minecraft uses. */
		private val componentSerializers = mapOf(
			"keybind" to KeybindComponent.serializer(),
			"nbt" to NbtComponent.serializer(),
			"object" to ObjectTextComponent.serializer(),
			"score" to ScoreComponent.serializer(),
			"selector" to EntityComponent.serializer(),
			"text" to PlainTextComponent.serializer(),
			"translatable" to TranslatedTextComponent.serializer(),
		)

		private fun serializerForType(type: String) =
			componentSerializers[type] ?: error("Unknown chat component type: '$type'.")

		/* Minecraft allows omitting `type`; the present key then determines the component type, defaulting to text. */
		private fun inferType(keys: Set<String>) = when {
			"translate" in keys -> "translatable"
			"score" in keys -> "score"
			"selector" in keys -> "selector"
			"keybind" in keys -> "keybind"
			"nbt" in keys -> "nbt"
			"object" in keys -> "object"
			else -> "text"
		}

		/* Decodes a single component: a string is plain text, an array is a root with its siblings as `extra`, an object dispatches on `type`. */
		private fun decodeJson(element: JsonElement): ChatComponent = when (element) {
			is JsonArray -> {
				require(element.isNotEmpty()) { "Cannot decode an empty array as a chat component." }
				decodeJson(element[0]).also { root ->
					if (element.size > 1) root.extra =
						ChatComponents(element.drop(1).mapTo(mutableListOf(), ::decodeJson))
				}
			}

			is JsonObject -> {
				val type = element["type"]?.jsonPrimitive?.content ?: inferType(element.keys)
				jsonSerializer.decodeFromJsonElement(serializerForType(type), JsonObject(element - "type"))
			}

			else -> text((element as JsonPrimitive).content)
		}

		private fun decodeNbt(nbt: NbtFormat, tag: NbtTag): ChatComponent = when (tag) {
			is NbtList<*> -> {
				require(tag.isNotEmpty()) { "Cannot decode an empty list as a chat component." }
				decodeNbt(nbt, tag[0]).also { root ->
					if (tag.size > 1) root.extra =
						ChatComponents(tag.drop(1).mapTo(mutableListOf()) { decodeNbt(nbt, it) })
				}
			}

			is NbtCompound -> {
				val type = (tag["type"] as? NbtString)?.value ?: inferType(tag.keys)
				nbt.decodeFromNbtTag(serializerForType(type), NbtCompound(tag.filterKeys { it != "type" }))
			}

			is NbtString -> text(tag.value)
			else -> text(tag.toString())
		}

		private data object ChatComponentSerializer : KSerializer<ChatComponent> {
			override val descriptor = NbtTag.serializer().descriptor

			override fun deserialize(decoder: Decoder) = when (decoder) {
				is JsonDecoder -> decodeJson(decoder.decodeJsonElement())
				is NbtDecoder -> decodeNbt(decoder.nbt, decoder.decodeNbtTag())
				else -> throw IllegalArgumentException("Unsupported decoder: $decoder")
			}

			override fun serialize(encoder: Encoder, value: ChatComponent) = when (value) {
				is PlainTextComponent ->
					if (value.containsOnlyText()) encoder.encodeString(value.text)
					else encoder.encodeSerializableValue(PlainTextComponent.serializer(), value)

				else -> encoder.encodeSerializableValue(encoder.serializersModule.serializerFor(value), value)
			}
		}

		data object ChatComponentsSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(NbtTag.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = when (decoder) {
				is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
					is JsonArray -> ChatComponents(element.mapTo(mutableListOf(), ::decodeJson))
					else -> ChatComponents(decodeJson(element))
				}

				is NbtDecoder -> when (val tag = decoder.decodeNbtTag()) {
					is NbtList<*> -> ChatComponents(tag.mapTo(mutableListOf()) { decodeNbt(decoder.nbt, it) })
					else -> ChatComponents(decodeNbt(decoder.nbt, tag))
				}

				else -> throw IllegalArgumentException("Unsupported decoder: $decoder")
			}

			/* Encode each component, if there's only one, encode it as a single component, if the component only contains a text, encode it as a string. */
			override fun serialize(encoder: Encoder, value: ChatComponents) {
				if (value.list.size == 1) encoder.encodeSerializableValue(ChatComponentSerializer, value.list[0])
				else encoder.encodeComponents(descriptor, value.list)
			}
		}

		/* Serializes ChatComponents to a string with escaped quotes for use in SNBT. */
		data object ChatComponentsEscapedSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(JsonElement.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = ChatComponents()

			override fun serialize(encoder: Encoder, value: ChatComponents) = when (encoder) {
				is NbtEncoder -> {
					val snbtSerialized = value.asString()
					encoder.encodeNbtTag(NbtString("'$snbtSerialized'"))
				}

				is JsonEncoder -> {
					val snbtSerialized = value.asString()
					encoder.encodeJsonElement(JsonPrimitive(snbtSerialized))
				}

				else -> throw IllegalArgumentException("Unsupported encoder: $encoder")
			}
		}
	}
}
