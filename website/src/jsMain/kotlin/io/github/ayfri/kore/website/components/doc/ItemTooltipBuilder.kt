package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.overflowX
import io.github.ayfri.kore.website.components.common.HighlightedCode
import io.github.ayfri.kore.website.components.mc.DocMockupsStyle
import io.github.ayfri.kore.website.components.mc.McColor
import io.github.ayfri.kore.website.components.mc.McLine
import io.github.ayfri.kore.website.components.mc.McStack
import io.github.ayfri.kore.website.components.mc.McTooltip
import io.github.ayfri.kore.website.components.mc.gui
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private const val KOTLIN_ID = "item-tooltip-kotlin"

private enum class TooltipRarity(val color: String) {
	COMMON(McColor.WHITE),
	UNCOMMON(McColor.YELLOW),
	RARE(McColor.AQUA),
	EPIC(McColor.LIGHT_PURPLE),
}

/** [rarity] and [glints] are what the item has without any component: a nether star is rare and always shines. */
private enum class TooltipItem(val title: String, val rarity: TooltipRarity = TooltipRarity.COMMON, val glints: Boolean = false) {
	DIAMOND_SWORD("Diamond Sword"),
	GOLDEN_APPLE("Golden Apple"),
	NETHER_STAR("Nether Star", TooltipRarity.RARE, glints = true),
	STICK("Stick"),
	TOTEM_OF_UNDYING("Totem of Undying", TooltipRarity.UNCOMMON);

	val id get() = name.lowercase()
}

/** Sets a name, a rarity, a lore line and the glint on an item, drawn as the game's tooltip and written as the Kore item with its components. */
@Composable
fun ItemTooltipBuilder() {
	var glint by remember { mutableStateOf(false) }
	var item by remember { mutableStateOf(TooltipItem.DIAMOND_SWORD) }
	var lore by remember { mutableStateOf("Forged for champions") }
	var name by remember { mutableStateOf("Arena Blade") }
	var rarity by remember { mutableStateOf(TooltipRarity.COMMON) }

	val components = listOfNotNull(
		name.takeIf { it.isNotBlank() }?.let { "customName(${it.kotlin()})" },
		glint.takeIf { it != item.glints }?.let { "enchantmentGlintOverride($it)" },
		lore.takeIf { it.isNotBlank() }?.let { "lore(${it.kotlin()})" },
		rarity.takeIf { it != item.rarity }?.let { "rarity(Rarities.${it.name})" },
	)
	val kotlin = if (components.isEmpty()) "Items.${item.name}"
	else components.joinToString("\n\t", "Items.${item.name} {\n\t", "\n}")

	Div({ classes(DocControlsStyle.widget) }) {
		Div({ classes(DocControlsStyle.panel, DocControlsStyle.stack) }) {
			ControlField("item") {
				PillGroup("Item", TooltipItem.entries, item, { it.name.lowercase().replace('_', ' ') }, { "item/${it.id}" }) { item = it }
			}

			Div({ classes(DocControlsStyle.grid) }) {
				TextField("item-tooltip-name", "name", name, item.title, 30) { name = it }
				TextField("item-tooltip-lore", "lore", lore, "none", 40) { lore = it }
			}

			ControlField("rarity") {
				PillGroup("Rarity", TooltipRarity.entries, rarity, { it.name.lowercase() }) { rarity = it }
			}
			ControlField("enchantment glint") {
				PillGroup("Enchantment glint", listOf(false, true), glint, { if (it) "on" else "off" }) { glint = it }
			}
		}

		Div({
			classes(DocControlsStyle.panel, DocMockupsStyle.itemBody)
			style {
				gap(gui(12))
				overflowX(Overflow.Auto)
				padding(1.5.cssRem)
			}
		}) {
			McStack(item.id, enchanted = glint)
			McTooltip {
				if (name.isBlank()) McLine(item.title, rarity.color) else McLine(name, rarity.color, italic = true)
				if (lore.isNotBlank()) McLine(lore, McColor.DARK_PURPLE, italic = true)
			}
		}

		HighlightedCode(KOTLIN_ID, kotlin, "kotlin")
	}
}
