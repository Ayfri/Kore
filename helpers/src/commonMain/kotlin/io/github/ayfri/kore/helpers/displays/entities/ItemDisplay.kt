package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ItemDisplay(
	var item: ItemStack? = null,
	@JsonSerialName("item_display")
	var displayMode: ItemDisplayModelMode? = null,
) : DisplayEntity() {
	@Transient
	override val entityType = EntityTypes.ITEM_DISPLAY
}
