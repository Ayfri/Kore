package io.github.ayfri.kore.features.jukeboxsongs

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.resources.JukeboxSongArgument
import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

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

fun JukeboxSong.soundEvent(sound: SoundEventArgument, range: Float? = null) {
	soundEvent = SoundEvent(sound, range)
}

fun DataPack.jukeboxSong(fileName: String, init: JukeboxSong.() -> Unit): JukeboxSongArgument {
	val jukeboxSong = JukeboxSong(fileName).apply(init)
	jukeboxSongs += jukeboxSong
	return JukeboxSongArgument(fileName, jukeboxSong.namespace ?: name)
}

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

fun DataPack.jukeboxSong(
	fileName: String,
	sound: SoundEventArgument,
	lengthInSeconds: Float,
	comparatorOutput: Int,
	description: String? = null,
	init: JukeboxSong.() -> Unit = {},
) = jukeboxSong(fileName, sound, lengthInSeconds, comparatorOutput, description?.let(::textComponent), init)
