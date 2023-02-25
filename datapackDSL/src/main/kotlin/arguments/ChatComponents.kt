package arguments

import arguments.chatcomponents.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.buildNbtList
import serializers.NbtAsJsonTextComponentSerializer

@Serializable(with = ChatComponents.Companion.ChatComponentsSerializer::class)
data class ChatComponents(val list: MutableList<ChatComponent> = mutableListOf()) : Argument {
	constructor(vararg components: ChatComponent) : this(components.toMutableList())

	operator fun plus(textComponent: ChatComponent): ChatComponents {
		list += textComponent
		return this
	}

	operator fun plus(chatComponents: ChatComponents): ChatComponents {
		list += chatComponents.list
		return this
	}

	fun toJsonString() = Json.encodeToString(NbtAsJsonTextComponentSerializer, toNbtTag())

	fun toNbtTag() = when (list.size) {
		0 -> NbtString("")
		1 -> list[0].toNbtTag()
		else -> buildNbtList {
			list.forEach {
				this += it.toNbtTag()
			}
		}
	}

	override fun asString() = StringifiedNbt.encodeToString(toNbtTag())

	fun asJsonArg() = literal(toJsonString())

	companion object {
		object ChatComponentsSerializer : KSerializer<ChatComponents> {
			override val descriptor = ListSerializer(JsonElement.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = ChatComponents()

			/* Encode each component, if there's only one, encode it as a single component, if the component only contains a text, encode it as a string. */
			override fun serialize(encoder: Encoder, value: ChatComponents) {
				if (value.list.size == 1) {
					when (val component = value.list[0]) {
						is EntityComponent -> encoder.encodeSerializableValue(EntityComponent.serializer(), component)
						is ScoreComponent -> encoder.encodeSerializableValue(ScoreComponent.serializer(), component)
						is NbtComponent -> encoder.encodeSerializableValue(NbtComponent.serializer(), component)
						is TranslatedTextComponent -> encoder.encodeSerializableValue(TranslatedTextComponent.serializer(), component)
						is TextComponent -> if (component.containsOnlyText()) encoder.encodeString(component.text)
						else encoder.encodeSerializableValue(TextComponent.serializer(), component)
					}
				} else {
					encoder.encodeCollection(descriptor, value.list.size) {
						value.list.forEach {
							when (it) {
								is EntityComponent -> encoder.encodeSerializableValue(EntityComponent.serializer(), it)
								is ScoreComponent -> encoder.encodeSerializableValue(ScoreComponent.serializer(), it)
								is NbtComponent -> encoder.encodeSerializableValue(NbtComponent.serializer(), it)
								is TranslatedTextComponent -> encoder.encodeSerializableValue(TranslatedTextComponent.serializer(), it)
								is TextComponent -> encoder.encodeSerializableValue(TextComponent.serializer(), it)
							}
						}
					}
					encoder.encodeSerializableValue(ListSerializer(ChatComponent.serializer()), value.list)
				}
			}
		}
	}
}
