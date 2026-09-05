package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemArgument : ResourceLocationArgument, ItemOrTagArgument {
	var components: ComponentsPatch?

	override fun asString() = "${asId()}${components?.toString() ?: ""}"

	operator fun invoke(block: ComponentsPatch.() -> Unit = {}) = item(name, namespace, block)

	companion object {
		operator fun invoke(
			name: String,
			namespace: String,
			components: ComponentsPatch? = null,
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
	components: ComponentsPatch? = null,
) = ItemArgument(item, namespace, components)

fun item(
	item: String,
	namespace: String = "minecraft",
	components: (ComponentsPatch.() -> Unit),
) = ItemArgument(item, namespace, ComponentsPatch().apply(components))
