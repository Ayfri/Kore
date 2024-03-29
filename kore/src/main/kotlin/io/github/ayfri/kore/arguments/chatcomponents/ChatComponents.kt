package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.plusAssign
import kotlin.reflect.full.createType
import net.benwoodworth.knbt.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNamingStrategy

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

	companion object {
		val ONLY_SIMPLE_COMPONENTS_EXCEPTION = IllegalArgumentException("This ChatComponents should only contain simple components.")

		@OptIn(ExperimentalSerializationApi::class)
		val jsonSerializer = Json {
			ignoreUnknownKeys = true
			encodeDefaults = false
			namingStrategy = JsonNamingStrategy.SnakeCase
		}

		object ChatComponentsSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(NbtTag.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = ChatComponents()

			/* Encode each component, if there's only one, encode it as a single component, if the component only contains a text, encode it as a string. */
			override fun serialize(encoder: Encoder, value: ChatComponents) {
				if (value.list.size == 1) {
					when (val component = value.list[0]) {
						is PlainTextComponent -> if (component.containsOnlyText()) encoder.encodeSerializableValue(
							String.serializer(),
							component.text
						) else encoder.encodeSerializableValue(PlainTextComponent.serializer(), component)

						else -> encoder.encodeSerializableValue(serializer(component::class.createType()), component)
					}
				} else {
					encoder.encodeCollection(descriptor, value.list.size) {
						value.list.forEachIndexed { i, component ->
							when (component) {
								is PlainTextComponent -> if (component.containsOnlyText()) encodeStringElement(
									descriptor,
									i,
									component.text
								) else encodeSerializableElement(descriptor, i, PlainTextComponent.serializer(), component)

								else -> encodeSerializableElement(descriptor, i, serializer(component::class.createType()), component)
							}
						}
					}
				}
			}
		}

		object ChatComponentsEscapedSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(JsonElement.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = ChatComponents()

			override fun serialize(encoder: Encoder, value: ChatComponents) = when (encoder) {
				is NbtEncoder -> {
					val normalSerialized = jsonSerializer.encodeToString(ChatComponentsSerializer, value)
					encoder.encodeNbtTag(NbtString("'$normalSerialized'"))
				}

				is JsonEncoder -> encoder.encodeSerializableValue(ChatComponentsSerializer, value)
				else -> throw IllegalArgumentException("Unsupported encoder: $encoder")
			}
		}

		object ChatComponentsAsListEscapedSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(JsonElement.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = ChatComponents()

			override fun serialize(encoder: Encoder, value: ChatComponents) = when (encoder) {
				is NbtEncoder -> {
					val normalSerialized = jsonSerializer.encodeToString(ChatComponentsSerializer, value)
					encoder.encodeNbtTag(NbtString("['$normalSerialized']"))
				}

				is JsonEncoder -> encoder.encodeSerializableValue(ChatComponentsSerializer, value)
				else -> throw IllegalArgumentException("Unsupported encoder: $encoder")
			}
		}
	}
}
