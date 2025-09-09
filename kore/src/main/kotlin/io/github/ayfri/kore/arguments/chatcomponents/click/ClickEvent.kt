package io.github.ayfri.kore.arguments.chatcomponents.click

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ClickEvent.Companion.ClickEventSerializer::class)
sealed class ClickEvent {
	companion object {
		data object ClickEventSerializer : NamespacedPolymorphicSerializer<ClickEvent>(
			kClass = ClickEvent::class,
			outputName = "action",
			useMinecraftPrefix = false
		)
	}
}

@Serializable(with = ClickEventContainer.Companion.ClickEventContainerSerializer::class)
data class ClickEventContainer(var event: ClickEvent? = null) {
	companion object {
		data object ClickEventContainerSerializer : InlineAutoSerializer<ClickEventContainer>(ClickEventContainer::class)
	}
}
