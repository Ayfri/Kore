package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Display properties (icon, title, description, frame, etc.) for an advancement.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements#display
 */
@Serializable
data class AdvancementDisplay(
	/** The icon for the advancement. */
	var icon: AdvancementIcon,
	/** The title of the advancement. */
	var title: ChatComponents,
	/** The description of the advancement. */
	var description: ChatComponents,
	/** The frame type for the advancement. */
	var frame: AdvancementFrameType = AdvancementFrameType.TASK,
	/** The background texture for the advancement. */
	var background: ModelArgument? = null,
	var showToast: Boolean? = null,
	var announceToChat: Boolean? = null,
	var hidden: Boolean? = null,
)

/**
 * Frame type for advancement display.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements#display
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#Display
 */
@Serializable(with = AdvancementFrameType.Companion.AdvancementFrameTypeSerializer::class)
enum class AdvancementFrameType {
	CHALLENGE,
	GOAL,
	TASK;

	companion object {
		data object AdvancementFrameTypeSerializer : LowercaseSerializer<AdvancementFrameType>(entries)
	}
}

/**
 * Icon definition for an advancement.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements#display
 */
@Serializable
data class AdvancementIcon(
	var id: ItemArgument,
	var components: Components? = null,
	var count: Int? = null,
)
