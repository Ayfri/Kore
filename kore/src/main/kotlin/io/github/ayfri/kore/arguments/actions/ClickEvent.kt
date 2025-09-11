package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ClickEvent.Companion.ClickEventSerializer::class)
sealed class ClickEvent : Action() {
	companion object {
		data object ClickEventSerializer : NamespacedPolymorphicSerializer<ClickEvent>(
			kClass = ClickEvent::class,
			outputName = "action",
			useMinecraftPrefix = false
		)
	}
}

@Serializable(with = ClickEventContainer.Companion.ClickEventContainerSerializer::class)
data class ClickEventContainer(
	@Serializable(with = ClickEvent.Companion.ClickEventSerializer::class)
	override var action: Action? = null
) : ActionWrapper() {
	companion object {
		data object ClickEventContainerSerializer : InlineAutoSerializer<ClickEventContainer>(ClickEventContainer::class)
	}
}
