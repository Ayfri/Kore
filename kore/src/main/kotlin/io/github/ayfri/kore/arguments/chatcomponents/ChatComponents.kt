package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.plusAssign
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*
import kotlin.reflect.full.createType

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

	fun toNbtList() = buildNbtList {
		list.forEach {
			this += it.toNbtTag()
		}
	}

	fun toNbtTag() = when (list.size) {
		0 -> NbtString("")
		1 -> list[0].also {
			if (it.containsOnlyText()) return NbtString(it.text)
		}.toNbtTag()

		else -> toNbtList()
	}

	override fun asString() = StringifiedNbt.encodeToString(toNbtTag())

	fun asJsonArg() = literal(toJsonString())

	fun asSnbtArg() = literal(asString())

	companion object {
		val ONLY_SIMPLE_COMPONENTS_EXCEPTION = IllegalArgumentException("This ChatComponent should only contain simple components.")

		@OptIn(ExperimentalSerializationApi::class)
		val jsonSerializer = Json {
			ignoreUnknownKeys = true
			encodeDefaults = false
			namingStrategy = JsonNamingStrategy.SnakeCase
		}

		private data object ChatComponentSerializer : KSerializer<ChatComponent> {
			override val descriptor = NbtTag.serializer().descriptor

			override fun deserialize(decoder: Decoder) = when (decoder) {
				is JsonDecoder -> {
					val decodeJsonElement = decoder.decodeJsonElement()
					try {
						jsonSerializer.decodeFromJsonElement(ChatComponent.serializer(), decodeJsonElement)
					} catch (error: Exception) {
						text(text = decodeJsonElement.jsonPrimitive.content)
					}
				}

				is NbtDecoder -> {
					val tag = decoder.decodeNbtTag()
					try {
						decoder.nbt.decodeFromNbtTag(ChatComponent.serializer(), tag)
					} catch (error: Exception) {
						text(text = tag.toString())
					}
				}

				else -> throw IllegalArgumentException("Unsupported decoder: $decoder")
			}

			override fun serialize(encoder: Encoder, value: ChatComponent) = when (value) {
				is PlainTextComponent ->
					if (value.containsOnlyText()) encoder.encodeString(value.text)
					else encoder.encodeSerializableValue(PlainTextComponent.serializer(), value)

				else -> encoder.encodeSerializableValue(serializer(value::class.createType()), value)
			}
		}

		data object ChatComponentsSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(NbtTag.serializer()).descriptor

			override fun deserialize(decoder: Decoder): ChatComponents {
				return when (decoder) {
					is JsonDecoder -> {
						val list = mutableListOf<ChatComponent>()
						val decodeJsonElement = decoder.decodeJsonElement()
						try {
							decodeJsonElement.jsonArray.forEach {
								list += jsonSerializer.decodeFromJsonElement(ChatComponent.serializer(), it)
							}
						} catch (error: Exception) {
							list += text(text = decodeJsonElement.jsonPrimitive.content)
						}
						ChatComponents(list)
					}

					is NbtDecoder -> {
						throw UnsupportedOperationException("NBT decoding is not supported for ChatComponents.")
					}

					else -> throw IllegalArgumentException("Unsupported decoder: $decoder")
				}
			}

			/* Encode each component, if there's only one, encode it as a single component, if the component only contains a text, encode it as a string. */
			override fun serialize(encoder: Encoder, value: ChatComponents) {
				if (value.list.size == 1) {
					encoder.encodeSerializableValue(ChatComponentSerializer, value.list[0])
				} else {
					encoder.encodeCollection(descriptor, value.list.size) {
						value.list.forEachIndexed { i, component ->
							encodeSerializableElement(descriptor, i, ChatComponentSerializer, component)
						}
					}
				}
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

		/** Serializes a list of Chat Components as a list instead of inlining the first element if possible. **/
		data object ChatComponentsAsListSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(ChatComponent.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = ChatComponents()

			override fun serialize(encoder: Encoder, value: ChatComponents) = when (encoder) {
				is NbtEncoder -> encoder.encodeCollection(descriptor, value.list.size) {
					value.list.forEachIndexed { i, component ->
						encodeSerializableElement(descriptor, i, ChatComponentSerializer, component)
					}
				}

				is JsonEncoder -> encoder.encodeCollection(descriptor, value.list.size) {
					value.list.forEachIndexed { i, component ->
						encodeSerializableElement(descriptor, i, ChatComponentSerializer, component)
					}
				}

				else -> throw IllegalArgumentException("Unsupported encoder: $encoder")
			}
		}
	}
}
