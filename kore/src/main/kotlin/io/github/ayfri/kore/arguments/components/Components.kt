package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.unescape
import net.benwoodworth.knbt.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = Components.Companion.ComponentsSerializer::class)
data class Components(
	val components: MutableMap<String, Component> = mutableMapOf(),
) {
	fun addComponent(name: String, nbt: NbtTag) = components.set(name, object : CustomComponent() {
		override val nbt = nbt
	})

	fun addComponent(name: String, block: NbtCompoundBuilder.() -> Unit) = addComponent(name, nbt(block))

	operator fun get(name: String) = components[name]

	operator fun set(name: String, component: Component) {
		components[name] = component
	}

	operator fun set(name: String, nbt: NbtTag) = addComponent(name, nbt)

	fun asNbt(): NbtCompound {
		val classicComponents = components.filterValues { it !is CustomComponent }
		val customComponents = components.filterValues { it is CustomComponent } as Map<String, CustomComponent>

		val classicComponentsSerialized = nbt {
			classicComponents.forEach { (key, value) -> put(key, nbtSerializer.encodeToNbtTag(value)) }
		}

		val customComponentsSerialized = nbt {
			customComponents.forEach { (key, value) -> put(key, value.nbt) }
		}

		return nbt {
			classicComponentsSerialized.forEach { (key, value) -> put(key, value) }
			customComponentsSerialized.forEach { (key, value) -> put(key, value) }
		}
	}

	fun asJson(): JsonObject {
		val classicComponents = components.filterValues { it !is CustomComponent }
		val customComponents = components.filterValues { it is CustomComponent } as Map<String, CustomComponent>

		val classicComponentsSerialized = buildJsonObject {
			classicComponents.map { (key, value) -> key to jsonSerializer.encodeToJsonElement(value) }
				.forEach { (key, value) ->
					put(key, value)
				}
		}

		val customComponentsSerialized = jsonSerializer.encodeToJsonElement(
			MapSerializer(String.serializer(), CustomComponent.Companion.CustomComponentSerializer),
			customComponents
		).jsonObject

		return buildJsonObject {
			classicComponentsSerialized.jsonObject.entries.forEach { (key, value) -> put(key, value) }
			customComponentsSerialized.entries.forEach { (key, value) -> put(key, value) }
		}
	}

	override fun toString() = asNbt().entries
		.joinToString(separator = ",", prefix = "[", postfix = "]") { (key, value) ->
			// The quotes are added by the serializer, we just need to unescape the string.
			if (key in CHAT_COMPONENTS_COMPONENTS_TYPES) {
				val unescaped = value.toString().unescape()
					// we also need a fix for JSON Components as they are serialized as JSON but single quoted.
					.replace(Regex("\"\'\"(.+?)\"\'\"", RegexOption.DOT_MATCHES_ALL), "'\"$1\"'")
					.replace(Regex("\"\'\\{(.+?)\\}\'\"", RegexOption.DOT_MATCHES_ALL), "'{$1}'")
				"$key=$unescaped"
			} else "$key=$value"
		}

	companion object {
		@OptIn(ExperimentalSerializationApi::class)
		val jsonSerializer = Json {
			prettyPrint = false
			encodeDefaults = false
			classDiscriminatorMode = ClassDiscriminatorMode.NONE
			namingStrategy = JsonNamingStrategy.SnakeCase
		}

		val nbtSerializer = StringifiedNbt

		/**
		 * List of all the Components that are chat components and thus must be quoted.
		 * Modify this list if you want to add a new chat component Component.
		 *
		 * See [ChatComponentsEscapedSerializer][io.github.ayfri.kore.arguments.chatcomponents.ChatComponents.Companion.ChatComponentsEscapedSerializer] for understanding how it's serialized.
		 */
		val CHAT_COMPONENTS_COMPONENTS_TYPES = mutableListOf(
			"custom_name",
			"lore",
			"written_book_content",
		)

		object ComponentsSerializer : KSerializer<Components> {
			override val descriptor = buildClassSerialDescriptor("Components") {
				element<Map<String, Component>>("components")
			}

			override fun deserialize(decoder: Decoder) = error("Components deserialization is not supported.")

			override fun serialize(encoder: Encoder, value: Components) = when (encoder) {
				is NbtEncoder -> encoder.encodeNbtTag(value.asNbt())
				is JsonEncoder -> encoder.encodeJsonElement(value.asJson())
				else -> error("This serializer can only be used with Nbt or Json")
			}
		}
	}
}
