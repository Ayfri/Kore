package arguments.colors

import kotlinx.serialization.Serializable

@Serializable(with = NamedColor.Companion.NamedColorSerializer::class)
class BossBarColor internal constructor(name: String) : NamedColor(name) {
	companion object {
		val BLUE = BossBarColor("blue")
		val GREEN = BossBarColor("green")
		val PINK = BossBarColor("pink")
		val PURPLE = BossBarColor("purple")
		val RED = BossBarColor("red")
		val WHITE = BossBarColor("white")
		val YELLOW = BossBarColor("yellow")
	}
}
