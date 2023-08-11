package features.advancements

import arguments.chatcomponents.ChatComponents
import arguments.types.resources.ItemArgument
import net.benwoodworth.knbt.NbtTag
import serializers.LowercaseSerializer
import serializers.NbtAsJsonTextComponentSerializer
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
	var item: ItemArgument,
	@Serializable(with = NbtAsJsonTextComponentSerializer::class) var nbtData: NbtTag? = null,
)
