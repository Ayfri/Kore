package features.advancements

import arguments.chatcomponents.ChatComponents
import arguments.types.resources.ItemArgument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag
import serializers.LowercaseSerializer
import serializers.NbtAsJsonTextComponentSerializer

@Serializable
data class AdvancementDisplay(
	var icon: AdvancementIcon,
	@Serializable
	var title: ChatComponents,
	@Serializable
	var description: ChatComponents,
	var frame: AdvancementFrameType = AdvancementFrameType.TASK,
	var background: String? = null,
	var showToast: Boolean = true,
	var announceToChat: Boolean = true,
	var hidden: Boolean = false,
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
	@Serializable(with = NbtAsJsonTextComponentSerializer::class) var nbtData: NbtTag? = null
)
