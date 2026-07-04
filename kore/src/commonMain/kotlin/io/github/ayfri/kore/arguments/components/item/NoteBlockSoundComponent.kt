package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents the `minecraft:note_block_sound` item component, which specifies the sound a note block plays when this player head is above it.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#note_block_sound
 */
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

/** Specifies the sound a note block plays when this player head is above it. */
fun ComponentsScope.noteBlockSound(blockSound: SoundArgument) = apply {
	this[ItemComponentTypes.NOTE_BLOCK_SOUND] = NoteBlockSoundComponent(blockSound)
}
