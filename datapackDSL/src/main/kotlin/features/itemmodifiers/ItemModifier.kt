package features.itemmodifiers

import DataPack
import Generator
import arguments.Argument
import features.itemmodifiers.functions.SetCount
import features.predicates.providers.constant
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
		val json = when (modifiers.size) {
			1 -> dataPack.jsonEncoder.encodeToString(modifiers[0])
			else -> dataPack.jsonEncoder.encodeToString(modifiers)
		}

		File(directory, "$fileName.json").writeText(json)
	}
}

fun DataPack.itemModifier(fileName: String = "item_modifier", configure: ItemModifierEntry.() -> Unit = {}): Argument.ItemModifier {
	itemModifiers += ItemModifier(fileName).apply {
		modifiers += ItemModifierEntry(SetCount(constant(1f))).apply(configure)
	}

	return Argument.ItemModifier(fileName, name)
}
