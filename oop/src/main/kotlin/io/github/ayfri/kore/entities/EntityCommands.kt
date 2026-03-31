package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.Xp
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument

context(fn: Function)
fun Entity.addTag(tag: String) = fn.tag(asSelector()) { add(tag) }

context(fn: Function)
fun Entity.clearItems(item: ItemArgument? = null, maxCount: Int? = null) = fn.clear(asSelector(), item, maxCount)

context(fn: Function)
fun Entity.damage(amount: Float, damageType: DamageTypeArgument? = null) = fn.damage(asSelector(), amount, damageType)

context(fn: Function)
fun Entity.dismount() = fn.rideDismount(asSelector())

context(fn: Function)
fun Entity.giveXp(amount: Xp) = fn.experience(asSelector()) { add(amount) }

context(fn: Function)
fun Entity.kill() = fn.kill(asSelector())

context(fn: Function)
fun Entity.mount(vehicle: Entity) = fn.rideMount(asSelector(), vehicle.asSelector())

context(fn: Function)
fun Entity.playSound(
	sound: SoundArgument,
	source: PlaySoundMixer? = null,
	pos: Vec3? = null,
	volume: Double? = null,
	pitch: Double? = null
) =
	fn.playSound(sound, source, asSelector(), pos, volume, pitch)

context(fn: Function)
fun Entity.removeTag(tag: String) = fn.tag(asSelector()) { remove(tag) }

context(fn: Function)
fun Entity.sendMessage(message: ChatComponents) = fn.tellraw(asSelector(), message)

context(fn: Function)
fun Entity.sendMessage(message: String) = fn.tellraw(asSelector(), message)

context(fn: Function)
fun Entity.setGamemode(gamemode: Gamemode) = fn.gamemode(gamemode, asSelector())

context(fn: Function)
fun Entity.setXp(amount: Xp) = fn.experience(asSelector()) { set(amount) }

context(fn: Function)
fun Entity.showActionBar(message: ChatComponents) = fn.title(asSelector(), TitleLocation.ACTIONBAR, message)

context(fn: Function)
fun Entity.showActionBar(message: String) = showActionBar(textComponent(message))

context(fn: Function)
fun Entity.showTitle(title: ChatComponents, subtitle: ChatComponents? = null) {
	fn.title(asSelector(), TitleLocation.TITLE, title)
	if (subtitle != null) fn.title(asSelector(), TitleLocation.SUBTITLE, subtitle)
}

context(fn: Function)
fun Entity.showTitle(title: String, subtitle: String? = null) {
	showTitle(textComponent(title), subtitle?.let { textComponent(it) })
}
