package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.types.Component
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.Serializable

@Serializable(with = ComponentsSerializer::class)
open class ComponentsPatch(components: MutableMap<String, Component> = mutableMapOf()) : ComponentsScope(components) {
	/**
	 * Set the last added component to be removed.
	 *
	 * Example:
	 * ```kotlin
	 * components {
	 *    !damage(1)
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	open operator fun ComponentsScope.not() = setToRemove(lastAddedComponentName ?: error("No component to set as removed."))

	/**
	 * Set a component to be removed, meaning it will have a `!` before its name and an empty NBT Compound as value.
	 * @receiver The name of the component to remove.
	 */
	operator fun String.not() = setToRemove(this)

	/**
	 * Set a component to be removed.
	 *
	 * Example:
	 * ```kotlin
	 * components {
	 *   !ItemComponentTypes.DAMAGE
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	operator fun ItemComponentTypes.not() = setToRemove(this)

	/**
	 * Set the component to be removed.
	 *
	 * Example:
	 * ```kotlin
	 * components {
	 *    setToRemove("damage")
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	open fun setToRemove(name: String) {
		components.remove(name)
		this["!$name"] = EmptyComponent()
	}

	/**
	 * Set the component to be removed.
	 *
	 * Example:
	 * ```kotlin
	 * components {
	 *    setToRemove(ItemComponentTypes.DAMAGE)
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	fun setToRemove(component: ItemComponentTypes) = setToRemove(component.name.lowercase())

	/**
	 * Set the components to be removed.
	 *
	 * Example:
	 * ```kotlin
	 * components {
	 *    !damage(1)
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	fun remove(vararg components: ItemComponentTypes) = components.forEach(::setToRemove)

	/**
	 * Set the components to be removed.
	 *
	 * Example:
	 * ```kotlin
	 * components {
	 *    !damage(1)
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	fun remove(vararg components: String) = components.forEach(::setToRemove)

	fun toComponents() = Components(components.mapKeys { it.key.removePrefix("!") }.toMutableMap())

	override fun toString() = asNbt().entries
		.joinToString(separator = ",", prefix = "[", postfix = "]") { (key, value) ->
			// The quotes are added by the serializer, we just need to unescape the string.
			if (key in CHAT_COMPONENTS_COMPONENTS_TYPES) {
				val unescaped = value.toString().unescape()
					// we also need a fix for JSON Components as they are serialized as JSON but single quoted.
					.replace(Regex("\"\'\"(.+?)\"\'\"", RegexOption.DOT_MATCHES_ALL), "'\"$1\"'")
					.replace(Regex("\"\'\\{(.+?)\\}\'\"", RegexOption.DOT_MATCHES_ALL), "'{$1}'")
				"$key=$unescaped"
			} else if (value == nbt {} && key.startsWith("!")) {
				key
			} else "$key=$value"
		}
}
