package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Sealed interface for all click event actions attached to a text component.
 * Concrete types: [OpenUrl], [RunCommand], [SuggestCommand], [CopyToClipboard], [ChangePage], [ShowDialog], [Custom].
 *
 * Docs: [Text component format - Click event](https://minecraft.wiki/w/Text_component_format#Click_event)
 */
@Serializable(with = ClickEvent.Companion.ClickEventSerializer::class)
sealed interface ClickEvent : ActionType {
	companion object {
		data object ClickEventSerializer : NamespacedPolymorphicSerializer<ClickEvent>(
			kClass = ClickEvent::class,
			outputName = "action",
			useMinecraftPrefix = false
		)
	}
}

/**
 * Mutable wrapper that accumulates a [ClickEvent] from the click event builder DSL.
 * Access the built action via [action] after calling one of the builder extensions.
 */
@Serializable(with = ClickEventContainer.Companion.ClickEventContainerSerializer::class)
data class ClickEventContainer(override var action: ClickEvent? = null) : ActionWrapper<ClickEvent>() {
	companion object {
		data object ClickEventContainerSerializer : InlineAutoSerializer<ClickEventContainer>(ClickEventContainer::class)
	}
}
