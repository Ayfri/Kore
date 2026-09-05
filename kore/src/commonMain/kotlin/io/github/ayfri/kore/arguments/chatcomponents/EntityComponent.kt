package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

/**
 * A `minecraft:selector` component that renders the names of all entities matching [selector], joined by [separator].
 *
 * Docs: [Text component format - Entity names](https://minecraft.wiki/w/Text_component_format#Entity_names)
 */
@Serializable
data class EntityComponent(
	/** Entity selector string (e.g. `"@a"`, `"@e[type=zombie]"`). */
	var selector: String,
	/** Component inserted between names when multiple entities match. Defaults to `", "` in-game when `null`. */
	var separator: String? = null,
) : ChatComponent() {
	override val type = ChatComponentType.SELECTOR

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["selector"] = selector
		separator?.let { this["separator"] = it }
	}
}

/** Creates an [EntityComponent] from a raw selector string. */
fun entityComponent(selector: String, separator: String? = null, block: EntityComponent.() -> Unit = {}) =
	ChatComponents(EntityComponent(selector, separator).apply(block))

/** Creates an [EntityComponent] from an [EntityArgument]. */
fun entityComponent(selector: EntityArgument, separator: String? = null, block: EntityComponent.() -> Unit = {}) =
	entityComponent(selector.asString(), separator, block)
