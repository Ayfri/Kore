package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class EntityComponent(
	var selector: String,
	var separator: String? = null,
) : ChatComponent() {
	override val type = ChatComponentType.SELECTOR

	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["selector"] = selector
		separator?.let { this["separator"] = it }
	}
}

fun entityComponent(selector: String, separator: String? = null, block: EntityComponent.() -> Unit = {}) =
	ChatComponents(EntityComponent(selector, separator).apply(block))

fun entityComponent(selector: EntityArgument, separator: String? = null, block: EntityComponent.() -> Unit = {}) =
	entityComponent(selector.asString(), separator, block)
