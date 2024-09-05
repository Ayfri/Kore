package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable(with = NbtComponentSource.Companion.NbtComponentSourceSerializer::class)
enum class NbtComponentSource {
	BLOCK,
	ENTITY,
	STORAGE;

	companion object {
		data object NbtComponentSourceSerializer : LowercaseSerializer<NbtComponentSource>(entries)
	}
}

@Serializable
data class NbtComponent(
	var nbt: String,
	var interpret: Boolean? = null,
	var block: String? = null,
	var entity: String? = null,
	var storage: String? = null,
	var separator: ChatComponent? = null,
	var source: NbtComponentSource? = null,
) : ChatComponent() {
	override val type = ChatComponentType.NBT

	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["nbt"] = nbt
		interpret?.let { this["interpret"] = it }
		block?.let { this["block"] = it }
		entity?.let { this["entity"] = it }
		storage?.let { this["storage"] = it }
		separator?.let { this["separator"] = it.toNbtTag() }
		source?.let { this["source"] = it.name.lowercase() }
	}
}

fun nbtComponent(path: String, block: NbtComponent.() -> Unit = {}) = ChatComponents(NbtComponent(path).apply(block))

fun nbtComponent(path: String, block: Vec3, block2: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, block = block.asString(), source = NbtComponentSource.BLOCK).apply(block2))

fun nbtComponent(path: String, entity: EntityArgument, block: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, entity = entity.asString(), source = NbtComponentSource.ENTITY).apply(block))

fun nbtComponent(path: String, storage: StorageArgument, block: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, storage = storage.asString(), source = NbtComponentSource.STORAGE).apply(block))
