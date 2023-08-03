package pack

import DataPack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a pack.filter used for blocking content.
 *
 * @property blocks A list of [FilteredBlock] objects that define the blocks in the pack.filter.
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

fun DataPack.filter(block: Filter.() -> Unit) = Filter().apply(block).let { filter = it }
