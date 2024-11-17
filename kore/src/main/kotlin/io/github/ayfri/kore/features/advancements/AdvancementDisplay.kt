package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class AdvancementDisplay(
	var icon: AdvancementIcon,
	@Serializable
	var title: ChatComponents,
	@Serializable
	var description: ChatComponents,
	var frame: AdvancementFrameType = AdvancementFrameType.TASK,
	var background: String? = null,
	var showToast: Boolean? = null,
	var announceToChat: Boolean? = null,
	var hidden: Boolean? = null,
)

@Serializable(with = AdvancementFrameType.Companion.AdvancementFrameTypeSerializer::class)
enum class AdvancementFrameType {
	CHALLENGE,
	GOAL,
	TASK;

	companion object {
		data object AdvancementFrameTypeSerializer : LowercaseSerializer<AdvancementFrameType>(entries)
	}
}

@Serializable
data class AdvancementIcon(
	var id: ItemArgument,
	var components: Components? = null,
	var count: Int? = null,
)
