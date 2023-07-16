import arguments.chatcomponents.ChatComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilteredBlock(
	var namespace: String? = null,
	var path: String? = null,
)

@Serializable
class Filter {
	@SerialName("block")
	internal val blocks = mutableListOf<FilteredBlock>()

	fun block(namespace: String? = null, path: String? = null) {
		blocks += FilteredBlock(namespace, path)
	}

	fun block(block: FilteredBlock) {
		blocks += block
	}

	fun block(block: FilteredBlock.() -> Unit) {
		blocks += FilteredBlock().apply(block)
	}

	fun blocks(vararg blocks: FilteredBlock) {
		this.blocks += blocks
	}

	fun blocks(blocks: Collection<FilteredBlock>) {
		this.blocks += blocks
	}
}

@Serializable
data class Pack(
	@SerialName("pack_format")
	var format: Int,
	@Serializable
	var description: ChatComponents,
)

@Serializable
data class PackMCMeta(
	val pack: Pack,
	val filter: Filter? = null,
)
