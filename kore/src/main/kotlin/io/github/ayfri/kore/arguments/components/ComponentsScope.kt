package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.types.Component
import io.github.ayfri.kore.arguments.components.types.CustomComponent
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.encodeToNbtTag

abstract class ComponentsScope(open val components: MutableMap<String, Component> = mutableMapOf()) {
	open val lastAddedComponentName: String?
		get() = components.keys.lastOrNull()

	open val lastAddedComponent: Component?
		get() = components.values.lastOrNull()

	fun addComponent(name: String, nbt: NbtTag) {
		this[name] = CustomComponent(nbt)
	}

	fun addComponent(name: String, block: NbtCompoundBuilder.() -> Unit) = addComponent(name, nbt(block))

	fun copyFrom(scope: ComponentsScope) = components.putAll(scope.components)
	fun copyFrom(item: ItemArgument) = item.components?.let { copyFrom(it) }
	fun copyFrom(item: ItemStack) = item.components?.let { copyFrom(it) }

	open operator fun get(name: String) = components[name]

	open operator fun set(name: String, component: Component) = run { components[name] = component }
	operator fun set(name: String, nbt: NbtTag) = addComponent(name, nbt)

	operator fun set(name: ComponentTypes, component: Component) {
		this[name.name.lowercase()] = component
	}

	operator fun set(name: ComponentTypes, nbt: NbtTag) {
		this[name.name.lowercase()] = nbt
	}

	open fun asNbt(): NbtCompound {
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

	open fun asJson(): JsonObject {
		val classicComponents = components.filterValues { it !is CustomComponent }
		val customComponents = components.filterValues { it is CustomComponent } as Map<String, CustomComponent>

		val classicComponentsSerialized = buildJsonObject {
			classicComponents.map { (key, value) -> key to jsonSerializer.encodeToJsonElement(value) }
				.forEach { (key, value) -> put(key, value) }
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
}
