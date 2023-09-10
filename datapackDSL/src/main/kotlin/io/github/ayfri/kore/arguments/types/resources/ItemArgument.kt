package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemArgument : ResourceLocationArgument, ItemOrTagArgument {
	var nbtData: NbtCompound?

	override fun asString() = "${asId()}${nbtData?.toString() ?: ""}"

	operator fun invoke(block: NbtCompoundBuilder.() -> Unit = {}) = apply { nbtData = nbt(block) }

	companion object {
		operator fun invoke(
			name: String,
			namespace: String,
			nbtData: NbtCompound? = null,
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
	nbtData: (NbtCompoundBuilder.() -> Unit)? = null,
) = ItemArgument(item, namespace, nbtData?.let { nbt(it) })
