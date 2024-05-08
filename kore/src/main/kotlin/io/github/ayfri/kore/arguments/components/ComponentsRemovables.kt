package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable(with = ComponentsSerializer::class)
open class ComponentsRemovables : ComponentsScope() {
	/**
	 * Set the last added component to be removed, meaning it will have a `!` before its name and an empty NBT Compound as value.
	 */
	operator fun ComponentsScope.not() = setToRemove(lastAddedComponentName ?: error("No component to set as removed."))

	/**
	 * Set a component to be removed, meaning it will have a `!` before its name and an empty NBT Compound as value.
	 * @receiver The name of the component to remove.
	 */
	operator fun String.not() = setToRemove(this)

	/**
	 * Set a component to be removed, meaning it will have a `!` before its name and an empty NBT Compound as value.
	 * @receiver The name of the component to remove.
	 */
	operator fun ComponentTypes.not() = setToRemove(this)

	/**
	 * Set a component to be removed, meaning it will have a `!` before its name and an empty NBT Compound as value.
	 * @param name The name of the component to remove.
	 */
	fun setToRemove(name: String) {
		components.remove(name)
		this["!$name"] = EmptyComponent()
	}

	/**
	 * Set a component to be removed, meaning it will have a `!` before its name and an empty NBT Compound as value.
	 * @param component The name of the component to remove.
	 */
	fun setToRemove(component: ComponentTypes) = setToRemove(component.name.lowercase())
}
