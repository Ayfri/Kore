package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import kotlinx.serialization.Serializable

@Serializable(with = BlockArgument.Companion.DataArgumentSerializer::class)
interface BlockArgument : ResourceLocationArgument, BlockOrTagArgument {
	var states: MutableMap<String, String>
	var nbtData: NbtCompound?

	override fun asString() = "${asId()}${states.map { "[$it]" }.joinToString("")}${nbtData?.toString() ?: ""}"

	operator fun invoke(states: Map<String, String> = mutableMapOf(), nbtData: (NbtCompoundBuilder.() -> Unit)? = null) = apply {
		this.states = states.toMutableMap()
		nbtData?.let { this.nbtData = nbt(it) }
	}

	companion object {
		operator fun invoke(
			name: String,
			namespace: String,
			states: Map<String, String> = mutableMapOf(),
			nbtData: NbtCompound? = null,
		) = object : BlockArgument {
			override val name = name
			override val namespace = namespace
			override var states = states.toMutableMap()
			override var nbtData = nbtData
		}

		data object DataArgumentSerializer : ToStringSerializer<BlockArgument>({ asString() })
	}
}

fun block(
	block: String,
	namespace: String = "minecraft",
	states: Map<String, String> = mutableMapOf(),
	nbtData: NbtCompoundBuilder.() -> Unit = {},
) = BlockArgument(block, namespace, states.toMutableMap(), nbt(nbtData))
