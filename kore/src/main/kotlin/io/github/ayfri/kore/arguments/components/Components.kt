package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.ToStringSerializer
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
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

	override fun toString() = asJson().entries
		.joinToString(separator = ",", prefix = "[", postfix = "]") { (key, value) ->
			// adding quotes to the value if the value is an array as it's required by Minecraft.
			if (value is JsonArray) {
				val arrayAsString = when (value.size) {
					0 -> "[]"
					1 -> "['${value.first()}']"
					else -> value.joinToString(",", prefix = "['[", postfix = "]']")
				}

				"$key=$arrayAsString"
			} else {
				"$key=$value"
			}
		}

	companion object {
		val jsonSerializer = Json {
			prettyPrint = false
			encodeDefaults = false
			classDiscriminatorMode = ClassDiscriminatorMode.NONE
		}

		object ComponentsSerializer : ToStringSerializer<Components>()
	}
}
