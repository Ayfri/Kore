package arguments.types

import serializers.ToStringSerializer
import kotlinx.serialization.encoding.Encoder

interface TaggedResourceLocationArgument : ResourceLocationArgument {
	override fun asId() = "#$namespace:$name"

	object TaggedResourceLocationWithoutPrefixSerializer : ToStringSerializer<TaggedResourceLocationArgument>() {
		override fun serialize(encoder: Encoder, value: TaggedResourceLocationArgument) =
			encoder.encodeString(value.asId().removePrefix("#"))
	}
}
