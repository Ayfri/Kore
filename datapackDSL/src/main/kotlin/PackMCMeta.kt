import arguments.chatcomponents.ChatComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a filtered block.
 *
 * @param namespace The namespace of the filtered block.
 * @param path The path of the filtered block.
 */
@Serializable
data class FilteredBlock(
	var namespace: String? = null,
	var path: String? = null,
)

/**
 * Represents a filter used for blocking content.
 *
 * @property blocks A list of [FilteredBlock] objects that define the blocks in the filter.
 */
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

/**
 * Represents a pack that contains game data and resources.
 *
 * @property format The format of the pack.
 * @property description The description of the pack.
 */
@Serializable
data class Pack(
	@SerialName("pack_format")
	var format: Int,
	@Serializable
	var description: ChatComponents,
)

/**
 * Represents the metadata of a Minecraft pack.
 *
 * @property pack The details of the pack.
 * @property filter The filter applied to the pack, if any.
 */
@Serializable
data class PackMCMeta(
	val pack: Pack,
	val filter: Filter? = null,
)
