package features.itemmodifiers

import DataPack
import Generator
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import java.io.File

@Serializable
data class ItemModifier(
	@Transient var fileName: String = "item_modifier",
	var modifiers: List<ItemModifierEntry> = emptyList(),
) : Generator {
	override fun generate(dataPack: DataPack, directory: File) {
		val json = dataPack.jsonEncoder.encodeToString(
			when (modifiers.size) {
				1 -> modifiers[0]
				else -> modifiers
			}
		)

		File(directory, "$fileName.json").writeText(json)
	}
}
