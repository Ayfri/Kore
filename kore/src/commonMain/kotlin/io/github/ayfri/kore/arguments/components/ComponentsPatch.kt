package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.utils.nbt
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
		this["!$name"] = EmptyComponent
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
	 *    remove(ItemComponentTypes.DAMAGE)
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
	 *    remove("damage")
	 * }
	 * ```
	 * Will be serialized as: `[!damage]`
	 */
	fun remove(vararg components: String) = components.forEach(::setToRemove)

	/** Convert the components to a [Components] object. */
	fun toComponents() = Components(components.mapKeys { it.key.removePrefix("!") }.toMutableMap())

	override fun toString() = asNbt().entries
		.joinToString(separator = ",", prefix = "[", postfix = "]") { (key, value) ->
			when {
				components[key]?.isChatComponent() == true -> "$key=${value.unescapeChatComponent()}"
				value == nbt {} && key.startsWith("!") -> key
				else -> "$key=$value"
			}
		}
}
