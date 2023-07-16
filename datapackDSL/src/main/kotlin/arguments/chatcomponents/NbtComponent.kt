package arguments.chatcomponents

import arguments.types.EntityArgument
import arguments.types.resources.BlockArgument
import arguments.types.resources.StorageArgument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import utils.set

@Serializable
data class NbtComponent(
	var nbt: String,
	var interpret: Boolean? = null,
	var block: String? = null,
	var entity: String? = null,
	var storage: String? = null,
	var separator: ChatComponent? = null,
) : ChatComponent() {
	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["nbt"] = nbt
		interpret?.let { this["interpret"] = it }
		block?.let { this["block"] = it }
		entity?.let { this["entity"] = it }
		storage?.let { this["storage"] = it }
		separator?.let { this["separator"] = it.toNbtTag() }
	}
}

fun nbtComponent(path: String, block: NbtComponent.() -> Unit = {}) = ChatComponents(NbtComponent(path).apply(block))

fun nbtComponent(path: String, block: BlockArgument, block2: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, block = block.asString()).apply(block2))

fun nbtComponent(path: String, entity: EntityArgument, block: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, entity = entity.asString()).apply(block))

fun nbtComponent(path: String, storage: StorageArgument, block: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, storage = storage.asString()).apply(block))
