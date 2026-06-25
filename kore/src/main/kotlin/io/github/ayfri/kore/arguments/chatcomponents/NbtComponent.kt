package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

@Serializable(with = NbtComponentSource.Companion.NbtComponentSourceSerializer::class)
enum class NbtComponentSource {
	BLOCK,
	ENTITY,
	STORAGE;

	companion object {
		data object NbtComponentSourceSerializer : LowercaseSerializer<NbtComponentSource>(entries)
	}
}

/**
 * A `minecraft:nbt` component that reads an NBT path from a block, entity, or storage source and renders the resolved value.
 *
 * Set [interpret] to parse the resolved value as a text component tree. Set [plain] to suppress SNBT key/value coloring.
 * [plain] and [interpret] cannot both be `true` simultaneously.
 *
 * Docs: [Text component format - NBT value](https://minecraft.wiki/w/Text_component_format#NBT_value)
 */
@Serializable
data class NbtComponent(
	/** NBT path expression used to read data from the selected [source]. */
	var nbt: String,
	/** When `true`, the resolved NBT string is parsed and rendered as a text component tree instead of raw SNBT. */
	var interpret: Boolean? = null,
	/** When `true`, suppresses SNBT syntax coloring. Cannot be `true` when [interpret] is also `true`. */
	var plain: Boolean? = null,
	/** Block coordinates string (e.g. `"0 64 0"`) used when [source] is [NbtComponentSource.BLOCK]. */
	var block: String? = null,
	/** Entity selector string used when [source] is [NbtComponentSource.ENTITY]. */
	var entity: String? = null,
	/** Storage namespace used when [source] is [NbtComponentSource.STORAGE]. */
	var storage: String? = null,
	/** Component inserted between multiple resolved values when the path matches more than one element. */
	var separator: ChatComponent? = null,
	/** Which data container to query: block, entity, or storage. */
	var source: NbtComponentSource? = null,
) : ChatComponent() {
	override val type = ChatComponentType.NBT

	init {
		require(plain != true || interpret != true) { "plain and interpret cannot both be true on a NbtComponent." }
	}

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		block?.let { this["block"] = it }
		entity?.let { this["entity"] = it }
		interpret?.let { this["interpret"] = it }
		this["nbt"] = nbt
		plain?.let { this["plain"] = it }
		separator?.let { this["separator"] = it.toNbtTag() }
		source?.let { this["source"] = it.name.lowercase() }
		storage?.let { this["storage"] = it }
	}
}

/** Creates a [NbtComponent] with a raw NBT [path] and no implicit source. */
fun nbtComponent(path: String, block: NbtComponent.() -> Unit = {}) = ChatComponents(NbtComponent(path).apply(block))

/** Creates a [NbtComponent] reading from the block at position [block]. */
fun nbtComponent(path: String, block: Vec3, block2: NbtComponent.() -> Unit = {}) =
	ChatComponents(
		NbtComponent(
			path,
			block = block.toStringTruncated(),
			source = NbtComponentSource.BLOCK
		).apply(block2)
	)

/** Creates a [NbtComponent] reading from the NBT data of [entity]. */
fun nbtComponent(path: String, entity: EntityArgument, block: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, entity = entity.asString(), source = NbtComponentSource.ENTITY).apply(block))

/** Creates a [NbtComponent] reading from the command [storage] namespace. */
fun nbtComponent(path: String, storage: StorageArgument, block: NbtComponent.() -> Unit = {}) =
	ChatComponents(NbtComponent(path, storage = storage.asString(), source = NbtComponentSource.STORAGE).apply(block))
