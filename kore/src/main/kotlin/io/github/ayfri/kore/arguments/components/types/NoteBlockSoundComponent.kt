package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = NoteBlockSoundComponent.Companion.NoteBlockSoundComponentSerializer::class)
data class NoteBlockSoundComponent(var blockSound: SoundArgument) : Component() {
	companion object {
		object NoteBlockSoundComponentSerializer : KSerializer<NoteBlockSoundComponent> {
			override val descriptor = SoundArgument.serializer().descriptor

			override fun deserialize(decoder: Decoder) = error("NoteBlockSoundComponent is not deserializable.")

			override fun serialize(encoder: Encoder, value: NoteBlockSoundComponent) {
				encoder.encodeString(value.blockSound.asString().replace('/', '.'))
			}
		}
	}
}

fun ComponentsScope.noteBlockSound(blockSound: SoundArgument) = apply {
	this[ComponentTypes.NOTE_BLOCK_SOUND] = NoteBlockSoundComponent(blockSound)
}
