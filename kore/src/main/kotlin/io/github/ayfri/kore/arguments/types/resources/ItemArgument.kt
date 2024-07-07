package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.components.ComponentsRemovables
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemArgument : ResourceLocationArgument, ItemOrTagArgument {
	var components: ComponentsRemovables?

	override fun asString() = "${asId()}${components?.toString() ?: ""}"

	operator fun invoke(block: ComponentsRemovables.() -> Unit = {}) = apply { components = ComponentsRemovables().apply(block) }

	companion object {
		operator fun invoke(
			name: String,
			namespace: String,
			components: ComponentsRemovables? = null,
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
	components: ComponentsRemovables? = null,
) = ItemArgument(item, namespace, components)

fun item(
	item: String,
	namespace: String = "minecraft",
	components: (ComponentsRemovables.() -> Unit),
) = ItemArgument(item, namespace, ComponentsRemovables().apply(components))
