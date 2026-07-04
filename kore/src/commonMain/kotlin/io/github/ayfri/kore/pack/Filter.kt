package io.github.ayfri.kore.pack

import io.github.ayfri.kore.DataPack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a pack.filter used for blocking content.
 * Any file that matches one of the patterns inside [blocks] is treated as if it was not present in the pack at all.
 *
 * JSON format reference: https://minecraft.wiki/w/Pack.mcmeta
 *
 * @property blocks A list of [FilteredBlock] objects that define the blocks in the pack.filter.
 */
@Serializable
class Filter {
	@SerialName("block")
	internal val blocks = mutableListOf<FilteredBlock>()

	/** Adds a block pattern to the filter, can be a Regex or a string. */
	fun block(namespace: String? = null, path: String? = null) {
		blocks += FilteredBlock(namespace, path)
	}

	/** Adds a block pattern to the filter, can be a Regex or a string. */
	fun block(block: FilteredBlock) {
		blocks += block
	}

	/** Adds a block pattern to the filter, can be a Regex or a string. */
	fun block(block: FilteredBlock.() -> Unit) {
		blocks += FilteredBlock().apply(block)
	}

	/** Adds multiple block patterns to the filter, can be a Regex or a string. */
	fun blocks(vararg blocks: FilteredBlock) {
		this.blocks += blocks
	}

	/** Adds multiple block patterns to the filter, can be a Regex or a string. */
	fun blocks(blocks: Collection<FilteredBlock>) {
		this.blocks += blocks
	}
}

/**
* Sets the filter of the datapack, used for filtering out files from packs applied below this one.
* Any file that matches one of the patterns inside [Filter.blocks] is treated as if it was not present in the pack at all.
*/
fun DataPack.filter(block: Filter.() -> Unit) = Filter().apply(block).let { filter = it }
