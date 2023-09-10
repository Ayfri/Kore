package io.github.ayfri.kore.arguments.types

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.encoding.Encoder

interface TaggedResourceLocationArgument : ResourceLocationArgument {
	override fun asId() = "#$namespace:$name"

	object TaggedResourceLocationWithoutPrefixSerializer : ToStringSerializer<TaggedResourceLocationArgument>() {
		override fun serialize(encoder: Encoder, value: TaggedResourceLocationArgument) =
			encoder.encodeString(value.asString().removePrefix("#"))
	}

	companion object {
		operator fun invoke(tagName: String, namespace: String) = object : TaggedResourceLocationArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
