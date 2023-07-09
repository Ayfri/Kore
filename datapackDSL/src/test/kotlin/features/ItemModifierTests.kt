package features

import DataPack
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierEntry
import features.itemmodifiers.functions.*
import features.predicates.conditions.WeatherCheck
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
	prettyPrint = true
}

fun DataPack.itemModifierTests() {
	val modifier = ItemModifier(
		fileName = "item_modifier",
		modifiers = listOf(
			ItemModifierEntry(
				function = SetLore(
					entity = Source.BLOCK_ENTITY,
					lore = listOf(
						"test",
						"test2",
					),
					replace = true,
				),
				conditions = listOf(
					WeatherCheck(
						raining = true,
						thundering = true,
					)
				)
			),
			ItemModifierEntry(
				function = CopyNbt(
					source = CopyNbtStorage("test"),
					ops = listOf(
						CopyNbtOperation(
							op = CopyNbtOperationType.REPLACE,
							source = "test",
							target = "test2",
						)
					)
				)
			)
		)
	)

	println(json.encodeToString(modifier))
}
