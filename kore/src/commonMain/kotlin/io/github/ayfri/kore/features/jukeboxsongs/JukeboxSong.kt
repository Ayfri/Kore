package io.github.ayfri.kore.features.jukeboxsongs

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.generated.arguments.types.JukeboxSongArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven jukebox song metadata.
 *
 * Describes a music track that can be played by jukeboxes: associated sound event, in-game
 * description, length for the HUD progress, and comparator output level. Allows adding
 * custom music discs backed by your own sounds.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Jukebox_song_definition
 */
@Serializable
data class JukeboxSong(
	@Transient
	override var fileName: String = "jukebox_song",
	var soundEvent: SoundEvent = SoundEvent(),
	var description: ChatComponents = ChatComponents(),
	var lengthInSeconds: Float = 0f,
	var comparatorOutput: Int = 0,
) : Generator("jukebox_song") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Set the sound event and range for this jukebox song.
 */
fun JukeboxSong.soundEvent(sound: SoundEventArgument, range: Float? = null) {
	soundEvent = SoundEvent(sound, range)
}

/**
 * Creates a jukebox song using a builder block.
 *
 * Lets you configure the sound event, description, duration, and comparator output via the DSL.
 * Produces `data/<namespace>/jukebox_song/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Jukebox_song_definition
 */
fun DataPack.jukeboxSong(fileName: String, init: JukeboxSong.() -> Unit): JukeboxSongArgument {
	val jukeboxSong = JukeboxSong(fileName).apply(init)
	jukeboxSongs += jukeboxSong
	return JukeboxSongArgument(fileName, jukeboxSong.namespace ?: name)
}

/**
 * Creates a jukebox song with inline parameters.
 *
 * Lets you configure the sound event, description, duration, and comparator output via the DSL.
 * Produces `data/<namespace>/jukebox_song/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Jukebox_song_definition
 */
fun DataPack.jukeboxSong(
	fileName: String,
	sound: SoundEventArgument,
	lengthInSeconds: Float,
	comparatorOutput: Int,
	description: ChatComponents? = null,
	init: JukeboxSong.() -> Unit = {},
) =
	jukeboxSong(fileName) {
		soundEvent = SoundEvent(sound)
		this.lengthInSeconds = lengthInSeconds
		this.comparatorOutput = comparatorOutput
		if (description != null) this.description = description
		init()
	}

/**
 * Creates a jukebox song with inline parameters and a plain string description.
 *
 * Lets you configure the sound event, description, duration, and comparator output via the DSL.
 * Produces `data/<namespace>/jukebox_song/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Jukebox_song_definition
 */
fun DataPack.jukeboxSong(
	fileName: String,
	sound: SoundEventArgument,
	lengthInSeconds: Float,
	comparatorOutput: Int,
	description: String? = null,
	init: JukeboxSong.() -> Unit = {},
) = jukeboxSong(fileName, sound, lengthInSeconds, comparatorOutput, description?.let(::textComponent), init)
