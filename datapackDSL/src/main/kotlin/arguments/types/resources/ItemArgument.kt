package arguments.types.resources

import arguments.Argument
import arguments.types.ItemOrTagArgument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import utils.nbt

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemArgument : ResourceLocationArgument, ItemOrTagArgument {
	var nbtData: NbtCompound?

	override fun asString() = "${asId()}${nbtData?.toString() ?: ""}"

	operator fun invoke(block: NbtCompoundBuilder.() -> Unit = {}) = apply { nbtData = nbt(block) }

	companion object {
		operator fun invoke(
			name: String,
			namespace: String,
			nbtData: NbtCompound? = null
		) = object : ItemArgument {
			override val name = name
			override val namespace = namespace
			override var nbtData = nbtData
		}
	}
}

fun item(
	item: String,
	namespace: String = "minecraft",
	nbtData: (NbtCompoundBuilder.() -> Unit)? = null
) = ItemArgument(item, namespace, nbtData?.let { nbt(it) })
