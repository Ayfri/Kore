package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.plusAssign
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.buildNbtList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable(with = ChatComponents.Companion.ChatComponentsSerializer::class)
data class ChatComponents(val list: MutableList<ChatComponent> = mutableListOf(), var onlySimpleComponents: Boolean = false) : Argument {
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

	fun requireSimpleComponents() {
		if (!containsOnlySimpleComponents) throw ONLY_SIMPLE_COMPONENTS_EXCEPTION
	}

	fun toJsonString(json: Json = Json) = json.encodeToString(NbtAsJsonSerializer, toNbtTag())

	fun toNbtTag() = when (list.size) {
		0 -> NbtString("")
		1 -> list[0].also {
			if (it.containsOnlyText()) return NbtString(it.text)
		}.toNbtTag()

		else -> buildNbtList {
			list.forEach {
				this += it.toNbtTag()
			}
		}
	}

	override fun asString() = StringifiedNbt.encodeToString(toNbtTag())

	fun asJsonArg() = literal(toJsonString())

	companion object {
		val ONLY_SIMPLE_COMPONENTS_EXCEPTION = IllegalArgumentException("This ChatComponents should only contain simple components.")

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
						is PlainTextComponent -> if (component.containsOnlyText()) encoder.encodeString(component.text)
						else encoder.encodeSerializableValue(PlainTextComponent.serializer(), component)
					}
				} else {
					encoder.encodeCollection(descriptor, value.list.size) {
						value.list.forEachIndexed { i, component ->
							when (component) {
								is EntityComponent -> encodeSerializableElement(descriptor, i, EntityComponent.serializer(), component)
								is ScoreComponent -> encodeSerializableElement(descriptor, i, ScoreComponent.serializer(), component)
								is NbtComponent -> encodeSerializableElement(descriptor, i, NbtComponent.serializer(), component)
								is TranslatedTextComponent -> encodeSerializableElement(
									descriptor,
									i,
									TranslatedTextComponent.serializer(),
									component
								)

								is PlainTextComponent -> encodeSerializableElement(
									descriptor,
									i,
									PlainTextComponent.serializer(),
									component
								)
							}
						}
					}
				}
			}
		}
	}
}
