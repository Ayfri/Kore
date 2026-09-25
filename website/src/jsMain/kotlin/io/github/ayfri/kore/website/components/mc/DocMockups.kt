package io.github.ayfri.kore.website.components.mc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.mcTexture
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

/*
 * In-game renders of the doc examples, embedded in markdown with `{{{ .components.mc.XxxMockup }}}`.
 * Each one mirrors the code block right above it, so keep both in sync.
 */

private enum class SceneKind {
	/** A dialog screen, spanning the whole scene between its header and footer separators. */
	DIALOG,

	/** The in-game HUD, children being placed at the screen edges like the game does. */
	HUD,

	/** A menu centered over the blurred world. */
	SCREEN,
}

/** A game screen showing the world from the vanilla title [panorama] face, with a [caption] under it. */
@Composable
private fun McScene(caption: String, kind: SceneKind, panorama: Int = 0, height: Int? = null, content: @Composable () -> Unit) {
	Div({ classes(DocMockupsStyle.figure) }) {
		Div({
			classes(McUiStyle.world, DocMockupsStyle.scene)
			when (kind) {
				SceneKind.DIALOG -> classes(McUiStyle.menuBackground, DocMockupsStyle.dialog)
				SceneKind.HUD -> classes(DocMockupsStyle.hud)
				SceneKind.SCREEN -> classes(McUiStyle.menuBackground, DocMockupsStyle.screen)
			}
			style {
				property("background-image", mcPanorama(panorama))
				height?.let { height(gui(it)) }
			}
		}) { content() }
		P({ classes(DocMockupsStyle.caption) }) { Text(caption) }
	}
}

/** A dialog screen: [title] in the header, [body] in the middle, then the [footer] buttons. */
@Composable
private fun McDialog(
	title: String,
	titleColor: String = McColor.WHITE,
	footer: (@Composable () -> Unit)? = null,
	body: @Composable () -> Unit,
) {
	Div({ classes(DocMockupsStyle.dialogHeader) }) { McLine(title, titleColor) }
	Div({ classes(DocMockupsStyle.dialogBody) }) { body() }
	footer?.let { Div({ classes(DocMockupsStyle.dialogFooter) }) { it() } }
}

@Composable
private fun McButtonGrid(columns: Int, labels: List<String>) {
	Div({
		classes(DocMockupsStyle.buttonGrid)
		style { gridTemplateColumns("repeat($columns, minmax(0, ${gui(150)}))") }
	}) {
		labels.forEach { McButton(it) }
	}
}

@Composable
private fun McTextField(label: String, value: String) {
	Div({ classes(DocMockupsStyle.control) }) {
		McLine(label)
		Div({ classes(DocMockupsStyle.textField) }) { McLine(value, "#e0e0e0") }
	}
}

/** The handle moves over the slider width minus its own, as the game does. */
@Composable
private fun McSlider(label: String, fraction: Double) {
	Div({ classes(DocMockupsStyle.slider) }) {
		Span({
			classes(DocMockupsStyle.sliderHandle)
			style { property("left", "calc($fraction * (100% - ${gui(6)}) - ${gui(1)})") }
		})
		McLine(label)
	}
}

@Composable
private fun McCheckbox(label: String, checked: Boolean) {
	Div({ classes(DocMockupsStyle.checkboxRow) }) {
		Span({
			classes(DocMockupsStyle.checkbox)
			style { property("background-image", mcTexture("gui/sprites/widget/checkbox${if (checked) "_selected" else ""}")) }
		})
		McLine(label, "#e0e0e0")
	}
}

/** The tooltip of an unenchanted diamond sword. */
@Composable
private fun DiamondSwordTooltip(vararg extraClasses: String, enchantments: List<String> = emptyList()) =
	McTooltip(*extraClasses) {
		McLine("Diamond Sword", if (enchantments.isEmpty()) McColor.WHITE else McColor.AQUA)
		enchantments.forEach { McLine(it, McColor.GRAY) }
		McLine("")
		McLine("When in Main Hand:", McColor.GRAY)
		McLine(" 7 Attack Damage", McColor.DARK_GREEN)
		McLine(" 1.6 Attack Speed", McColor.DARK_GREEN)
	}

@Composable
fun ConfirmationDialogMockup() = McScene("In game: confirmDialog, the game stays paused while it is open", SceneKind.DIALOG) {
	McDialog("Delete World?", footer = {
		McButton("Delete", width = 150)
		McButton("Cancel", width = 150)
	}) {
		McParagraph("Are you sure you want to delete this world? This action cannot be undone.")
	}
}

@Composable
fun NoticeDialogMockup() = McScene("In game: noticeDialog, hover the sword to see its tooltip", SceneKind.DIALOG, panorama = 1) {
	var hovered by remember { mutableStateOf(false) }
	McDialog("Achievement Unlocked!", footer = { McButton("Awesome!", width = 150) }) {
		Div({ classes(DocMockupsStyle.itemBody) }) {
			Div({
				classes(DocMockupsStyle.hoverable)
				onMouseEnter { hovered = true }
				onMouseLeave { hovered = false }
			}) {
				McStack("diamond_sword")
				if (hovered) DiamondSwordTooltip(DocMockupsStyle.itemBodyTooltip)
			}
			McLine("Your first diamond tool!")
		}
		McParagraph("You've crafted your first diamond sword!")
	}
}

@Composable
fun MultiActionDialogMockup() = McScene("In game: menuDialog, inputs first, then the actions in 3 columns", SceneKind.DIALOG, panorama = 2) {
	McDialog("Server Menu") {
		McTextField("Your Name", "Steve")
		McSlider("Difficulty: 5", 4.0 / 9)
		McCheckbox("Enable PVP", checked = false)
		McButtonGrid(3, listOf("Start Game", "Settings", "Quit"))
	}
}

@Composable
fun KitsMenuMockup() = McScene("In game: kits.open(), \"Help\" opens its page and \"Done\" closes the menu", SceneKind.DIALOG, panorama = 3) {
	McDialog("Choose your kit", McColor.GOLD, footer = { McButton("Done", width = 200) }) {
		McParagraph("Your inventory is replaced by the kit.")
		McButtonGrid(2, listOf("Warrior", "Archer", "Help"))
	}
}

private data class BossBarState(val call: String, val color: McBossBarColor, val value: Int)

@Composable
fun BossBarMockup() {
	val states = listOf(
		BossBarState("registerBossBar", McBossBarColor.RED, 50),
		BossBarState("setValue(150)", McBossBarColor.RED, 150),
		BossBarState("setColor(BLUE)", McBossBarColor.BLUE, 100),
	)
	var state by remember { mutableStateOf(states.first()) }
	McScene("In game: the NOTCHED_10 bar at value ${state.value} of 200, click a call to apply it", SceneKind.HUD, panorama = 3, height = 70) {
		Div({ classes(DocMockupsStyle.hudTop) }) {
			McBossBar("Phase 2", state.color, state.value / 200.0, notches = 10)
		}
		Div({ classes(DocMockupsStyle.hudControls) }) {
			states.forEach { McButton(it.call, enabled = it != state) { state = it } }
		}
	}
}

@Composable
fun SidebarMockup() = McScene("In game: the lobby sidebar after lobby.refresh(), with sample scores", SceneKind.HUD, panorama = 1, height = 100) {
	Div({ classes(DocMockupsStyle.hudRight) }) {
		McSidebar(
			"Sky Wars", McColor.GOLD, listOf(
				McSidebarLine("Map: Floating Isles"),
				McSidebarLine(""),
				McSidebarLine("Players", value = "8"),
				McSidebarLine("Kills", McColor.RED, "12"),
				McSidebarLine(""),
				McSidebarLine("play.example.net", McColor.YELLOW),
			)
		)
	}
}

private data class Trade(val wants: String, val wantsCount: Int, val additional: String?, val gives: String, val enchanted: Boolean)

/** Offers use the `MerchantScreen` offsets, shifted by the button's 3 px border. */
@Composable
fun VillagerTradesMockup() {
	val trades = listOf(
		Trade("wheat", 20, null, "emerald", enchanted = false),
		Trade("emerald", 30, null, "diamond_sword", enchanted = true),
		Trade("emerald", 5, "book", "enchanted_book", enchanted = true),
	)
	var selected by remember { mutableStateOf(trades.first()) }
	McScene("In game: the three trades above in a merchant's offer list, click one to select it", SceneKind.SCREEN) {
		McContainer("villager", width = 276, textureWidth = 512, rows = 0 until 166) {
			McLabel("Trades", 53, 6, centered = true)
			McLabel("Wandering Trader", 187, 6, centered = true)
			McLabel("Inventory", 107, 72)
			trades.forEachIndexed { index, trade ->
				McAt(5, 18 + index * 20) {
					Button({
						classes(McUiStyle.button, DocMockupsStyle.offer)
						onClick { selected = trade }
					}) {
						McAt(2, -1) { McStack(trade.wants, trade.wantsCount) }
						trade.additional?.let { McAt(32, -1) { McStack(it) } }
						McAt(52, 2, DocMockupsStyle.tradeArrow)
						McAt(65, -1) { McStack(trade.gives, enchanted = trade.enchanted) }
					}
				}
			}
			McAt(94, 18, DocMockupsStyle.scroller)
			McSlot(136, 37, selected.wants, selected.wantsCount)
			selected.additional?.let { McSlot(162, 37, it) }
			McSlot(220, 37, selected.gives, enchanted = selected.enchanted)
		}
	}
}

/** The mouse rests on the first hotbar slot, where giveItem puts the sword, the tooltip being pushed up by the screen's bottom. */
@Composable
private fun HoveredSwordInventory(enchantments: List<String>) =
	McContainer("crafting_table", DocMockupsStyle.tooltipRoom, rows = McContainerInventory) {
		McLabel("Inventory", 8, 72)
		McAt(4, 138, DocMockupsStyle.slotHighlightBack)
		McSlot(8, 142, "diamond_sword", enchanted = true)
		McAt(4, 138, DocMockupsStyle.slotHighlightFront)
		McAt(28, 66) { DiamondSwordTooltip(enchantments = enchantments) }
	}

@Composable
fun ItemTooltipMockup() = McScene("In game: hovering the sword given by player.giveItem(sword)", SceneKind.SCREEN, panorama = 2) {
	HoveredSwordInventory(listOf("Sharpness V", "Unbreaking III"))
}

/** Custom enchantments show their `description` in gray, followed by the level numeral as maxLevel is above 1. */
@Composable
fun EnchantmentTooltipMockup() {
	val levels = listOf("I", "II", "III")
	var level by remember { mutableStateOf(levels.first()) }
	McScene("In game: a diamond sword enchanted with fire_aspect_plus, pick a level up to maxLevel", SceneKind.SCREEN, panorama = 4) {
		HoveredSwordInventory(listOf("Fire Aspect+ $level"))
		Div({ classes(DocMockupsStyle.screenControls) }) {
			levels.forEach { McButton("Level $it", enabled = it != level) { level = it } }
		}
	}
}

/** Each roll draws one entry by weight, then the chest puts every stack in a random empty slot. */
private fun rollCustomChest(): Map<Int, String> {
	val pool = listOf("diamond" to 1, "gold_ingot" to 5, "iron_ingot" to 10).flatMap { (item, weight) -> List(weight) { item } }
	return (0 until 27).shuffled().take(3).associateWith { pool.random() }
}

@Composable
fun LootChestMockup() {
	var loot by remember { mutableStateOf(rollCustomChest()) }
	McScene("In game: a chest filled from custom_chest, 3 rolls weighted 1:5:10, reroll to fill it again", SceneKind.SCREEN, panorama = 5) {
		McContainer("generic_54", height = 222, rows = 0 until 71) {
			McLabel("Chest", 8, 6)
			loot.forEach { (slot, item) -> McSlot(8 + slot % 9 * 18, 18 + slot / 9 * 18, item) }
		}
		Div({ classes(DocMockupsStyle.screenControls) }) {
			McButton("Reroll", width = 100) { loot = rollCustomChest() }
		}
	}
}

@Composable
fun MarkdownRendererMockup() = McScene("In game: the four examples above sent to the chat", SceneKind.HUD, panorama = 4, height = 70) {
	McChat(DocMockupsStyle.hudChat) {
		McChatLine(McSpan("bold and ", bold = true), McSpan("italic", bold = true, italic = true), McSpan(" text", bold = true))
		McChatLine(McSpan("Visit "), McSpan("Kore", McColor.GREEN, underlined = true))
		McChatLine(McSpan("red text", "#ff0000"), McSpan(" normal text"))
		McChatLine(McSpan("Welcome to Kore", McColor.GOLD, bold = true))
	}
}

/** The team prefix and color go inside the chat brackets, as they are part of the player's display name. */
@Composable
fun TeamChatMockup() = McScene("In game: the chat after player.joinTeam(red), next to a player without team", SceneKind.HUD, panorama = 5, height = 50) {
	McChat(DocMockupsStyle.hudChat) {
		McChatLine(McSpan("<"), McSpan("RED ", McColor.DARK_RED, bold = true), McSpan("Steve", McColor.DARK_RED), McSpan("> ready when you are"))
		McChatLine(McSpan("<Alex> gl hf"))
	}
}

@Composable
fun AdvancementToastsMockup() = McScene("In game: the toast of each frame, top right of the screen", SceneKind.HUD, panorama = 3, height = 110) {
	Div({ classes(DocMockupsStyle.hudTopRight) }) {
		McToast("diamond", "Advancement Made!", "My First Advancement")
		McToast("golden_apple", "Goal Reached!", "Golden Achievement")
		McToast("diamond_sword", "Challenge Complete!", "Monsters Hunted", challenge = true)
	}
}

@Composable
fun ShapedRecipeMockup() = McScene("In game: my_pickaxe in a crafting table", SceneKind.SCREEN) {
	McCraftingTable(listOf("DDD", " S ", " S "), mapOf('D' to "diamond", 'S' to "stick"), "diamond_pickaxe")
}

/** The arrow fills in the default 200 ticks cooking time, while the coal flame burns down. */
@Composable
fun SmeltingRecipeMockup() = McScene("In game: iron_ingot smelting in a furnace, the arrow fills in cookingTime (10 seconds)", SceneKind.SCREEN, panorama = 1) {
	McContainer("furnace") {
		McRecipeBookButton(20)
		McLabel("Furnace", 88, 6, centered = true)
		McSlot(56, 17, "raw_iron", 12)
		McAt(56, 36, DocMockupsStyle.furnaceFlame)
		McSlot(56, 53, "coal", 7)
		McAt(79, 34, DocMockupsStyle.furnaceArrow)
		McSlot(116, 35, "iron_ingot", 4)
	}
}

@Composable
fun SmithingRecipeMockup() = McScene("In game: the netherite_sword upgrade in a smithing table", SceneKind.SCREEN, panorama = 2) {
	McContainer("smithing") {
		McLabel("Upgrade Gear", 44, 15)
		McSlot(8, 48, "netherite_upgrade_smithing_template")
		McSlot(26, 48, "diamond_sword")
		McSlot(44, 48, "netherite_ingot")
		McSlot(98, 48, "netherite_sword")
	}
}

/** Vanilla draws the action bar 72 px above the bottom, see [McTitle] for the title. */
@Composable
fun TitleMockup() = McScene("In game: showTitle and showActionBar, after giveItem and replaceItem filled the hotbar", SceneKind.HUD, panorama = 2, height = 210) {
	McTitle("Title", subtitle = "Subtitle")
	Div({
		classes(DocMockupsStyle.hudCentered)
		style { bottom(gui(72 - 9)) }
	}) { McLine("Action bar text") }
	Div({ classes(DocMockupsStyle.hudBottom) }) { McHotbar(listOf("netherite_sword", "diamond")) }
}

@Composable
fun InventoryManagerMockup() = McScene("In game: taking the star out of the first hotbar slot shows the warning, and the star comes back", SceneKind.HUD, height = 100) {
	Div({
		classes(DocMockupsStyle.hudCentered)
		style { bottom(gui(72 - 9)) }
	}) { McLine("Stop taking me!", McColor.RED) }
	Div({ classes(DocMockupsStyle.hudBottom) }) { McHotbar(listOf("nether_star"), enchanted = setOf("nether_star")) }
}

object DocMockupsStyle : StyleSheet() {
	val furnaceFlameBurn by keyframes {
		from { property("clip-path", "inset(0 0 0 0)") }
		to { property("clip-path", "inset(93% 0 0 0)") }
	}

	val furnaceArrowFill by keyframes {
		from { property("clip-path", "inset(0 100% 0 0)") }
		to { property("clip-path", "inset(0 0 0 0)") }
	}

	val figure by style {
		margin(1.5.cssRem, 0.px)
	}

	val scene by style {
		borderRadius(GlobalStyle.roundingButton)
		overflowX(Overflow.Auto)
	}

	val screen by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		justifyContent(JustifyContent.Center)
		padding(gui(16), gui(8))
	}

	val dialog by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val hud by style {
		minWidth(gui(200))
	}

	val caption by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.85.cssRem)
		marginTop(0.5.cssRem)
		textAlign(TextAlign.Center)
	}

	/** The header and footer are 33 px tall, with the vanilla separators on their inner edge. */
	val dialogHeader by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		height(gui(33))
		justifyContent(JustifyContent.Center)
		property("background", "${mcTexture("gui/inworld_header_separator")} 0 100% / ${gui(32)} ${gui(2)} repeat-x")
		property("image-rendering", "pixelated")
	}

	val dialogBody by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(gui(10))
		padding(gui(12), gui(4))
	}

	val dialogFooter by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(gui(8))
		height(gui(33))
		justifyContent(JustifyContent.Center)
		property("background", "${mcTexture("gui/inworld_footer_separator")} 0 0 / ${gui(32)} ${gui(2)} repeat-x")
		property("image-rendering", "pixelated")
	}

	val buttonGrid by style {
		display(DisplayStyle.Grid)
		gap(gui(2))
		justifyContent(JustifyContent.Center)
		width(100.percent)

		"button" style { width(100.percent) }
	}

	val itemBody by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(gui(4))
	}

	val hoverable by style {
		position(Position.Relative)
	}

	val itemBodyTooltip by style {
		left(gui(20))
		position(Position.Absolute)
		top(gui(-12))
		zIndex(1)
	}

	val control by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(gui(2))
		maxWidth(100.percent)
		width(gui(200))
	}

	/** Text starts 4 px from the left and 6 px from the top of the field, inside its 1 px border. */
	val textField by style {
		border(gui(1), LineStyle.Solid, Color.transparent)
		height(gui(20))
		padding(gui(5), gui(3), 0.px)
		property("border-image", "${mcTexture("gui/sprites/widget/text_field")} 1 fill")
		property("box-sizing", "border-box")
		property("image-rendering", "pixelated")
	}

	val slider by style {
		border(gui(1), LineStyle.Solid, Color.transparent)
		height(gui(20))
		maxWidth(100.percent)
		paddingTop(gui(5))
		position(Position.Relative)
		textAlign(TextAlign.Center)
		width(gui(200))
		property("border-image", "${mcTexture("gui/sprites/widget/slider")} 1 fill")
		property("box-sizing", "border-box")
		property("image-rendering", "pixelated")

		"div" style { position(Position.Relative) }
	}

	val sliderHandle by style {
		height(gui(20))
		position(Position.Absolute)
		top(gui(-1))
		width(gui(8))
		property("background", "${mcTexture("gui/sprites/widget/slider_handle")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	val checkboxRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(gui(4))
		maxWidth(100.percent)
		width(gui(200))
	}

	/** Checkboxes are the font's line height plus 8, 17 px. */
	val checkbox by style {
		flexShrink(0)
		height(gui(17))
		width(gui(17))
		property("background-size", "100% 100%")
		property("image-rendering", "pixelated")
	}

	val hudTop by style {
		position(Position.Absolute)
		top(gui(3))
		property("left", PIXEL_HALF)
		property("transform", pixelTranslate("-50%"))
	}

	val hudTopRight by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		position(Position.Absolute)
		right(0.px)
		top(0.px)
	}

	val hudRight by style {
		position(Position.Absolute)
		right(gui(1))
		property("top", PIXEL_HALF)
		property("transform", pixelTranslate("0px", "-50%"))
	}

	val hudCentered by style {
		position(Position.Absolute)
		property("left", PIXEL_HALF)
		property("transform", pixelTranslate("-50%"))
	}

	val hudBottom by style {
		bottom(0.px)
		position(Position.Absolute)
		property("left", PIXEL_HALF)
		property("transform", pixelTranslate("-50%"))
	}

	val hudChat by style {
		bottom(gui(8))
		left(0.px)
		position(Position.Absolute)
	}

	val screenControls by style {
		display(DisplayStyle.Flex)
		gap(gui(4))
		justifyContent(JustifyContent.Center)
		marginTop(gui(8))
	}

	val hudControls by style {
		bottom(gui(6))
		display(DisplayStyle.Flex)
		gap(gui(4))
		justifyContent(JustifyContent.Center)
		left(0.px)
		position(Position.Absolute)
		right(0.px)
	}

	/** Offer buttons are 88x20, their items placed relative to the padding box. */
	val offer by style {
		display(DisplayStyle.Block)
		padding(0.px)
		position(Position.Relative)
		width(gui(88))
	}

	val tradeArrow by style {
		height(gui(9))
		width(gui(10))
		property("background", "${mcTexture("gui/sprites/container/villager/trade_arrow")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	val scroller by style {
		height(gui(27))
		width(gui(6))
		property("background", "${mcTexture("gui/sprites/container/villager/scroller_disabled")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	/** Leaves room above the inventory for the tooltip pushed up by the screen's bottom. */
	val tooltipRoom by style {
		marginTop(gui(8))
	}

	val slotHighlightBack by style {
		height(gui(24))
		width(gui(24))
		property("background", "${mcTexture("gui/sprites/container/slot_highlight_back")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	val slotHighlightFront by style {
		height(gui(24))
		width(gui(24))
		property("background", "${mcTexture("gui/sprites/container/slot_highlight_front")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
		property("pointer-events", "none")
	}

	val furnaceFlame by style {
		animation(furnaceFlameBurn) {
			duration(80.s)
			iterationCount(null)
			timingFunction(AnimationTimingFunction.steps(13))
		}
		height(gui(14))
		width(gui(14))
		property("background", "${mcTexture("gui/sprites/container/furnace/lit_progress")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	val furnaceArrow by style {
		animation(furnaceArrowFill) {
			duration(10.s)
			iterationCount(null)
			timingFunction(AnimationTimingFunction.steps(24))
		}
		height(gui(16))
		width(gui(24))
		property("background", "${mcTexture("gui/sprites/container/furnace/burn_progress")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}
}
