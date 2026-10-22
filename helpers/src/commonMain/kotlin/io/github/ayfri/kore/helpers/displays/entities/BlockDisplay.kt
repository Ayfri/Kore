package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.generated.EntityTypes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BlockDisplay(
	var blockState: BlockState? = null,
) : DisplayEntity() {
	@Transient
	override val entityType = EntityTypes.BLOCK_DISPLAY
}
