package arguments.colors

import kotlinx.serialization.Serializable

@Serializable(with = NamedColorSerializer::class)
class BossBarColor(name: String) : NamedColor(name)
