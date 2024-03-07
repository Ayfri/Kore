package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemArgument : ResourceLocationArgument, ItemOrTagArgument {
	var components: Components?

	override fun asString() = "${asId()}${components?.toString() ?: ""}"

	operator fun invoke(block: Components.() -> Unit = {}) = apply { components = Components().apply(block) }

	companion object {
		operator fun invoke(
			name: String,
			namespace: String,
			components: Components? = null,
		) = object : ItemArgument {
			override val name = name
			override val namespace = namespace
			override var components = components
		}
	}
}

fun item(
	item: String,
	namespace: String = "minecraft",
	components: Components? = null,
) = ItemArgument(item, namespace, components)

fun item(
	item: String,
	namespace: String = "minecraft",
	components: (Components.() -> Unit),
) = ItemArgument(item, namespace, Components().apply(components))
