package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.arguments.types.AtlasArgument
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class ObjectTextComponent(
	var atlas: AtlasArgument? = null,
	var sprite: ModelArgument,
) : ChatComponent(), SimpleComponent {
	override val type = ChatComponentType.OBJECT

	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		atlas?.let { this["atlas"] = it.asId() }
		this["sprite"] = sprite.asId()
	}
}

fun objectComponent(
	sprite: ModelArgument,
	atlas: AtlasArgument? = null,
	block: ObjectTextComponent.() -> Unit = {},
) = ChatComponents(ObjectTextComponent(atlas, sprite).apply(block))
