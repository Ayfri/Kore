package features.advancements

import arguments.Argument
import arguments.ChatComponents
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
	var frameType: AdvancementFrameType = AdvancementFrameType.TASK,
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
		val values = values()

		object AdvancementFrameTypeSerializer : LowercaseSerializer<AdvancementFrameType>(values)
	}
}

@Serializable
data class AdvancementIcon(
	var item: Argument.Item,
	@Serializable(with = NbtAsJsonTextComponentSerializer::class) var nbtData: NbtTag? = null
)
