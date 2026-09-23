package io.github.ayfri.kore.website.components.mc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.components.common.mcTexture
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoElement.after
import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoElement.before
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement

/** Minecraft's `§` color codes, plus the gray of container labels. */
object McColor {
	const val AQUA = "#55ffff"
	const val BLUE = "#5555ff"
	const val DARK_GRAY = "#555555"
	const val DARK_GREEN = "#00aa00"
	const val DARK_PURPLE = "#aa00aa"
	const val GOLD = "#ffaa00"
	const val GRAY = "#aaaaaa"
	const val GREEN = "#55ff55"
	const val LABEL = "#404040"
	const val LIGHT_PURPLE = "#ff55ff"
	const val RED = "#ff5555"
	const val WHITE = "#ffffff"
	const val YELLOW = "#ffff55"
}

/** [pixels] GUI pixels in CSS pixels, at the game's default GUI scale of 2, so vanilla coordinates can be used as is. */
fun gui(pixels: Number) = (pixels.toDouble() * 2).px

private fun itemTexture(item: String) = mcTexture("item/$item")

/** A `translate` rounded to whole pixels, so pixel art never lands between two screen pixels. */
fun pixelTranslate(x: String, y: String = "0px") = "translate(round($x, 1px), round($y, 1px))"

/** Half of the parent's size rounded to whole pixels, for `left` and `top`, see [pixelTranslate]. */
const val PIXEL_HALF = "round(50%, 1px)"

/** The game draws text shadows in the text color at a quarter of its brightness: `(color & 0xFCFCFC) >> 2`. */
private fun shadowOf(color: String) =
	"#" + color.removePrefix("#").chunked(2).joinToString("") { (it.toInt(16) / 4).toString(16).padStart(2, '0') }

/**
 * One line of in-game text, 9 GUI pixels tall like the game's line height, [scale] being 4 for titles and 2 for subtitles.
 * Chat, tooltips and buttons draw a hard [shadow], containers labels, toasts and the sidebar don't.
 * Italics shear the glyphs by 1 px over their height and the underline sits under the descender row, like the game.
 */
@Composable
fun McLine(
	text: String,
	color: String = McColor.WHITE,
	italic: Boolean = false,
	underlined: Boolean = false,
	shadow: Boolean = true,
	scale: Int = 1,
) {
	val width = McFont.width(text)
	val pixels = McFont.path(text) + if (underlined) "M0 8h${width}v1h-${width}z" else ""
	val svg = buildString {
		append("""<svg width="${(width + 1) * 2 * scale}" height="${18 * scale}" viewBox="0 0 ${width + 1} 9" shape-rendering="crispEdges">""")
		append(if (italic) """<g transform="translate(1 0) skewX(-7)">""" else "<g>")
		if (shadow) append("""<path fill="${shadowOf(color)}" transform="translate(1 1)" d="$pixels"/>""")
		append("""<path fill="$color" d="$pixels"/></g></svg>""")
	}
	Div({
		classes(McUiStyle.line)
		attr("aria-label", text)
		attr("role", "img")
	}) {
		DisposableEffect(svg) {
			scopeElement.innerHTML = svg
			onDispose {}
		}
	}
}

/** Wrapped in-game text, like dialog `plainMessage` bodies, [width] being the wrapping width in GUI pixels. */
@Composable
fun McParagraph(text: String, width: Int = 200) {
	Div({ classes(McUiStyle.paragraph) }) {
		McFont.wrap(text, width).forEach { McLine(it) }
	}
}

/** The item tooltip, drawn with the vanilla `tooltip/background` and `tooltip/frame` sprites. */
@Composable
fun McTooltip(vararg extraClasses: String, content: @Composable () -> Unit) {
	Div({ classes(McUiStyle.tooltip, *extraClasses) }) { content() }
}

/** A button drawn with the vanilla 9-slice `widget/button` sprite, [width] in GUI pixels (null to fit the label). */
@Composable
fun McButton(label: String, enabled: Boolean = true, width: Int? = null, onClick: () -> Unit = {}) {
	Button({
		classes(McUiStyle.button)
		if (!enabled) disabled()
		width?.let { style { width(gui(it)) } }
		onClick { onClick() }
	}) { McLine(label, if (enabled) McColor.WHITE else "#a0a0a0") }
}

/** An item texture, [item] being its id without namespace, e.g. `diamond_sword`. */
@Composable
fun McItem(item: String, vararg extraClasses: String) {
	Span({
		classes(McUiStyle.item, *extraClasses)
		attr("aria-label", item.replace('_', ' '))
		attr("role", "img")
		style { property("background-image", itemTexture(item)) }
	})
}

/** An item as drawn in GUIs, with its stack [count] and the scrolling enchantment glint. */
@Composable
fun McStack(item: String, count: Int? = null, enchanted: Boolean = false) {
	Span({ classes(McUiStyle.stack) }) {
		McItem(item)
		if (enchanted) Span({
			classes(McUiStyle.glint)
			style { property("mask-image", itemTexture(item)) }
		})
		count?.takeIf { it > 1 }?.let { Div({ classes(McUiStyle.count) }) { McLine(it.toString()) } }
	}
}

/** Places [content] at ([x], [y]) GUI pixels from the top left of its screen, like the game's draw coordinates. */
@Composable
fun McAt(x: Int, y: Int, vararg extraClasses: String, content: @Composable () -> Unit = {}) {
	Div({
		classes(McUiStyle.at, *extraClasses)
		style {
			left(gui(x))
			top(gui(y))
		}
	}) { content() }
}

/** A stack in the slot whose item corner is at ([x], [y]), the coordinates used by vanilla menus. */
@Composable
fun McSlot(x: Int, y: Int, item: String, count: Int? = null, enchanted: Boolean = false) =
	McAt(x, y) { McStack(item, count, enchanted) }

/** A container label, gray without shadow, centered on [x] when [centered]. */
@Composable
fun McLabel(text: String, x: Int, y: Int, centered: Boolean = false) =
	McAt(x, y, *listOfNotNull(McUiStyle.centered.takeIf { centered }).toTypedArray()) {
		McLine(text, McColor.LABEL, shadow = false)
	}

/** The green recipe book button, at the fixed height every recipe screen puts it. */
@Composable
fun McRecipeBookButton(x: Int) = McAt(x, 34, McUiStyle.recipeBook)

/** Rows of 176 px wide containers above the player inventory label: the title and the crafting area. */
val McContainerTop = 0 until 76

/** Rows of 176 px wide containers holding the player inventory, its label included. */
val McContainerInventory = 72 until 166

/** Rows of a container's top and bottom borders. */
private const val CONTAINER_EDGE = 7

/**
 * A container screen on its `gui/container/[texture]` background, showing only the texture [rows] closed by the frame edges.
 * Children are placed with [McAt] and [McSlot] in texture coordinates, whatever the rows shown.
 */
@Composable
fun McContainer(
	texture: String,
	vararg extraClasses: String,
	width: Int = 176,
	height: Int = 166,
	textureWidth: Int = 256,
	rows: IntRange = McContainerTop,
	content: @Composable () -> Unit,
) {
	val topEdge = if (rows.first > 0) CONTAINER_EDGE else 0
	val bottomEdge = if (rows.last < height - 1) CONTAINER_EDGE else 0
	Div({
		classes(McUiStyle.container, *extraClasses)
		if (topEdge > 0) classes(McUiStyle.topEdge)
		if (bottomEdge > 0) classes(McUiStyle.bottomEdge)
		style {
			height(gui(topEdge + rows.count() + bottomEdge))
			width(gui(width))
			property("--bottom-edge-y", gui(CONTAINER_EDGE - height))
			property("background-image", mcTexture("gui/container/$texture"))
			property("background-position", "0 ${gui(topEdge - rows.first)}")
			property("background-size", "${gui(textureWidth)} auto")
		}
	}) {
		Div({
			classes(McUiStyle.containerContent)
			style { top(gui(topEdge - rows.first)) }
		}) { content() }
	}
}

/** A crafting table GUI, [pattern] rows mapping [keys] characters to item ids. */
@Composable
fun McCraftingTable(pattern: List<String>, keys: Map<Char, String>, result: String, resultCount: Int? = null) =
	McContainer("crafting_table") {
		McRecipeBookButton(5)
		McLabel("Crafting", 29, 6)
		pattern.forEachIndexed { row, line ->
			line.forEachIndexed { column, key -> keys[key]?.let { McSlot(30 + column * 18, 17 + row * 18, it) } }
		}
		McSlot(124, 35, result, resultCount)
	}

/** The advancement toast, drawn on the vanilla `toast/advancement` sprite, with the pink header of [challenge] frames. */
@Composable
fun McToast(icon: String, header: String, title: String, vararg extraClasses: String, challenge: Boolean = false) {
	Div({ classes(McUiStyle.toast, *extraClasses) }) {
		McAt(8, 8) { McItem(icon) }
		McAt(30, 7) { McLine(header, if (challenge) "#ff88ff" else "#ffff00", shadow = false) }
		McAt(30, 18) { McLine(title, shadow = false) }
	}
}

/** Boss bar colors, named after their `gui/sprites/boss_bar/<color>_*` sprites. */
enum class McBossBarColor { BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW }

/** A boss bar at [progress] (0 to 1), [notches] being 0 (progress style) or 6, 10, 12 or 20. */
@Composable
fun McBossBar(title: String, color: McBossBarColor, progress: Double, notches: Int = 0) {
	val name = color.name.lowercase()
	fun layers(part: String) = listOfNotNull(
		notches.takeIf { it > 0 }?.let { mcTexture("gui/sprites/boss_bar/notched_${it}_$part") },
		mcTexture("gui/sprites/boss_bar/${name}_$part"),
	).joinToString()

	Div({ classes(McUiStyle.bossBar) }) {
		McLine(title)
		Div({
			classes(McUiStyle.bossBarTrack)
			style { property("background-image", layers("background")) }
		}) {
			Div({
				classes(McUiStyle.bossBarProgress)
				style {
					width((progress.coerceIn(0.0, 1.0) * 100).percent)
					property("background-image", layers("progress"))
				}
			})
		}
	}
}

/** One sidebar row, [value] being the right-aligned text. */
data class McSidebarLine(val text: String, val color: String = McColor.WHITE, val value: String? = null)

/** The scoreboard sidebar shown on the right of the screen, without text shadow like in game. */
@Composable
fun McSidebar(title: String, titleColor: String, lines: List<McSidebarLine>) {
	Div({ classes(McUiStyle.sidebar) }) {
		Div({ classes(McUiStyle.sidebarTitle) }) { McLine(title, titleColor, shadow = false) }
		Div({ classes(McUiStyle.sidebarBody) }) {
			lines.forEach { line ->
				Div({ classes(McUiStyle.sidebarRow) }) {
					McLine(line.text, line.color, shadow = false)
					line.value?.let { McLine(it, shadow = false) }
				}
			}
		}
	}
}

/** The bottom of the survival HUD: full health and hunger, an empty experience bar and the hotbar holding [items]. */
@Composable
fun McHotbar(
	items: List<String?>,
	selected: Int = 0,
	enchanted: Set<String> = emptySet(),
	health: Int = 20,
	cooldowns: Map<Int, Double> = emptyMap(),
) {
	Div({ classes(McUiStyle.hotbar) }) {
		repeat(10) {
			val heart = when {
				health >= it * 2 + 2 -> McUiStyle.heartFull
				health == it * 2 + 1 -> McUiStyle.heartHalf
				else -> McUiStyle.heartEmpty
			}
			McAt(it * 8, 0, McUiStyle.heart, heart)
			McAt(182 - 9 - it * 8, 0, McUiStyle.food)
		}
		McAt(0, 10, McUiStyle.experienceBar)
		McAt(0, 17, McUiStyle.hotbarBackground)
		McAt(selected * 20 - 1, 16, McUiStyle.hotbarSelection)
		items.forEachIndexed { slot, item -> item?.let { McSlot(3 + slot * 20, 20, it, enchanted = it in enchanted) } }
		cooldowns.forEach { (slot, remaining) ->
			McAt(3 + slot * 20, 20, McUiStyle.cooldown) {
				Div({
					classes(McUiStyle.cooldownFill)
					style { height((remaining.coerceIn(0.0, 1.0) * 100).percent) }
				})
			}
		}
	}
}

/** A title screen panorama [face] as the world behind a scene, from 0 to 5. */
fun mcPanorama(face: Int) = mcTexture("gui/title/background/panorama_$face")

/** The chat, its [McChatLine]s sharing the width of the longest one like in game. */
@Composable
fun McChat(vararg extraClasses: String, content: @Composable () -> Unit) {
	Div({ classes(McUiStyle.chat, *extraClasses) }) { content() }
}

/** A chat line on the game's translucent background, kept on this wrapper as the text filter would make it opaque. */
@Composable
fun McChatLine(text: String, color: String = McColor.WHITE, underlined: Boolean = false, attrs: AttrsScope<HTMLDivElement>.() -> Unit = {}) {
	Div({
		classes(McUiStyle.chatLine)
		attrs()
	}) { McLine(text, color, underlined = underlined) }
}

object McUiStyle : StyleSheet() {
	val glintScroll by keyframes {
		from { property("background-position", "0 0") }
		to { property("background-position", "${gui(-64)} ${gui(128)}") }
	}

	/** No font size, so the line box is exactly the SVG's height and parents can still center it with `text-align`. */
	val line by style {
		fontSize(0.px)
		lineHeight(0.number)

		"svg" style {
			display(DisplayStyle.InlineBlock)
			overflow(Overflow.Visible)
		}
	}

	val paragraph by style {
		textAlign(TextAlign.Center)
	}

	/** Lines are 10 px apart with 2 more under the first one, and the sprites overflow the text by 12 px on each side. */
	val tooltip by style {
		padding(gui(4))
		position(Position.Relative)
		property("isolation", "isolate")
		property("width", "max-content")

		child(self, universal) style { marginBottom(gui(1)) }
		child(self, selector(":first-child:not(:only-child)")) style { marginBottom(gui(3)) }
		child(self, selector(":only-child")) style { marginBottom(gui(-1)) }

		(self + before) style {
			border(gui(9), LineStyle.Solid, Color.transparent)
			position(Position.Absolute)
			zIndex(-1)
			property("border-image", "${mcTexture("gui/sprites/tooltip/background")} 9 fill")
			property("content", "''")
			property("image-rendering", "pixelated")
			property("inset", gui(-8))
		}

		(self + after) style {
			border(gui(10), LineStyle.Solid, Color.transparent)
			position(Position.Absolute)
			zIndex(-1)
			property("border-image", "${mcTexture("gui/sprites/tooltip/frame")} 10 fill")
			property("content", "''")
			property("image-rendering", "pixelated")
			property("inset", gui(-8))
		}
	}

	/**
	 * Labels start 6 px under the top edge, as the game centers 8 px of text in a 20 px button.
	 * Flex start overrides the browser's own vertical centering of button content, which would push the label 2 px lower.
	 */
	val button by style {
		alignItems(AlignItems.FlexStart)
		backgroundColor(Color.transparent)
		border(gui(3), LineStyle.Solid, Color.transparent)
		cursor(Cursor.Pointer)
		display(DisplayStyle.LegacyInlineFlex)
		height(gui(20))
		justifyContent(JustifyContent.Center)
		padding(gui(3), gui(4), 0.px)
		property("border-image", "${mcTexture("gui/sprites/widget/button")} 3 fill")
		property("box-sizing", "border-box")
		property("image-rendering", "pixelated")

		hover(self) style {
			property("border-image-source", mcTexture("gui/sprites/widget/button_highlighted"))
		}

		(self + disabled) style {
			cursor(Cursor.Default)
			property("border-image-source", mcTexture("gui/sprites/widget/button_disabled"))
		}
	}

	/** A background instead of an `<img>`, so the doc's image margins and rounded corners never reach it. */
	val item by style {
		display(DisplayStyle.Block)
		height(gui(16))
		width(gui(16))
		property("background-size", "100% 100%")
		property("image-rendering", "pixelated")
	}

	val stack by style {
		display(DisplayStyle.Block)
		height(gui(16))
		position(Position.Relative)
		width(gui(16))
	}

	val glint by style {
		animation(glintScroll) {
			duration(8.s)
			iterationCount(null)
			timingFunction(AnimationTimingFunction.Linear)
		}
		position(Position.Absolute)
		property("background", "${mcTexture("misc/enchanted_glint_item")} 0 0 / ${gui(64)}")
		property("image-rendering", "pixelated")
		property("inset", 0.px)
		property("mask-size", "100% 100%")
		property("mix-blend-mode", "plus-lighter")
		property("opacity", 0.45)
	}

	/** The game draws counts right-aligned 1 px past the item, their baseline on the item's last row, the shadow 1 px further. */
	val count by style {
		position(Position.Absolute)
		right(gui(-2))
		top(gui(9))
		property("pointer-events", "none")
	}

	val at by style {
		position(Position.Absolute)
	}

	val centered by style {
		property("transform", pixelTranslate("-50%"))
	}

	val recipeBook by style {
		height(gui(18))
		width(gui(20))
		property("background", "${mcTexture("gui/sprites/recipe_book/button")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	val container by style {
		flexShrink(0)
		position(Position.Relative)
		property("background-repeat", "no-repeat")
		property("image-rendering", "pixelated")
	}

	val containerContent by style {
		left(0.px)
		position(Position.Absolute)
		width(100.percent)
	}

	val topEdge by style {
		(self + before) style {
			height(gui(CONTAINER_EDGE))
			left(0.px)
			position(Position.Absolute)
			top(0.px)
			width(100.percent)
			property("background", "inherit")
			property("background-position", "0 0")
			property("content", "''")
		}
	}

	val bottomEdge by style {
		(self + after) style {
			bottom(0.px)
			height(gui(CONTAINER_EDGE))
			left(0.px)
			position(Position.Absolute)
			width(100.percent)
			property("background", "inherit")
			property("background-position", "0 var(--bottom-edge-y)")
			property("content", "''")
		}
	}

	val toast by style {
		flexShrink(0)
		height(gui(32))
		position(Position.Relative)
		width(gui(160))
		property("background", "${mcTexture("gui/sprites/toast/advancement")} 0 0 / 100% 100%")
		property("image-rendering", "pixelated")
	}

	val bossBar by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val bossBarTrack by style {
		height(gui(5))
		width(gui(182))
		property("background-size", "100% 100%")
		property("image-rendering", "pixelated")
	}

	/** Every layer is sized to the full track, so a narrower element crops the progress instead of squashing it. */
	val bossBarProgress by style {
		height(100.percent)
		transition(0.4.s, "width")
		property("background-size", "${gui(182)} 100%")
		property("image-rendering", "pixelated")
	}

	val sidebar by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		property("width", "max-content")
	}

	/** The game fills the title row at 40% opacity and the score rows at 30%, 1 px above each text. */
	val sidebarTitle by style {
		backgroundColor(rgba(0, 0, 0, 0.4))
		height(gui(8))
		paddingLeft(gui(2))
		paddingTop(gui(1))
		textAlign(TextAlign.Center)
	}

	val sidebarBody by style {
		backgroundColor(rgba(0, 0, 0, 0.3))
		paddingLeft(gui(2))
		paddingTop(gui(1))
	}

	/** Values sit at least the width of `": "` away from their line. */
	val sidebarRow by style {
		display(DisplayStyle.Flex)
		gap(gui(6))
		justifyContent(JustifyContent.SpaceBetween)
	}

	val hotbar by style {
		flexShrink(0)
		height(gui(39))
		position(Position.Relative)
		width(gui(182))
	}

	val heart by style {
		height(gui(9))
		width(gui(9))
		property("background-size", "100%")
		property("image-rendering", "pixelated")
	}

	val heartFull by style {
		property("background-image", "${mcTexture("gui/sprites/hud/heart/full")}, ${mcTexture("gui/sprites/hud/heart/container")}")
	}

	val heartHalf by style {
		property("background-image", "${mcTexture("gui/sprites/hud/heart/half")}, ${mcTexture("gui/sprites/hud/heart/container")}")
	}

	val heartEmpty by style {
		property("background-image", mcTexture("gui/sprites/hud/heart/container"))
	}

	val cooldown by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		height(gui(16))
		justifyContent(JustifyContent.FlexEnd)
		width(gui(16))
	}

	/** The game whitens the bottom of the item for the remaining cooldown fraction, rounded to whole pixels. */
	val cooldownFill by style {
		backgroundColor(rgba(255, 255, 255, 0.5))
		transition(0.05.s, "height")
	}

	val chat by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		property("width", "max-content")
	}

	val chatLine by style {
		backgroundColor(rgba(0, 0, 0, 0.5))
		paddingLeft(gui(2))
		paddingRight(gui(4))
	}

	/** Open menus blur the world and darken it with the tiled `inworld_menu_background`. */
	val menuBackground by style {
		(self + before) style {
			position(Position.Absolute)
			zIndex(-1)
			property("backdrop-filter", "blur(${gui(3)})")
			property("background", "${mcTexture("gui/inworld_menu_background")} 0 0 / ${gui(32)}")
			property("content", "''")
			property("inset", 0.px)
		}
	}

	/** A scene showing the world, from the panorama set as its `background-image`. */
	val world by style {
		position(Position.Relative)
		property("background-position", "center")
		property("background-size", "cover")
		property("isolation", "isolate")
	}

	val food by style {
		height(gui(9))
		width(gui(9))
		property(
			"background",
			"${mcTexture("gui/sprites/hud/food_full")} 0 0 / 100%, ${mcTexture("gui/sprites/hud/food_empty")} 0 0 / 100%"
		)
		property("image-rendering", "pixelated")
	}

	val experienceBar by style {
		height(gui(5))
		width(gui(182))
		property("background", "${mcTexture("gui/sprites/hud/experience_bar_background")} 0 0 / 100%")
		property("image-rendering", "pixelated")
	}

	val hotbarBackground by style {
		height(gui(22))
		width(gui(182))
		property("background", "${mcTexture("gui/sprites/hud/hotbar")} 0 0 / 100%")
		property("image-rendering", "pixelated")
	}

	val hotbarSelection by style {
		height(gui(23))
		width(gui(24))
		property("background", "${mcTexture("gui/sprites/hud/hotbar_selection")} 0 0 / 100%")
		property("image-rendering", "pixelated")
	}
}
