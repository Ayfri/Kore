package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.components.common.mcTexture
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.attributes.step
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import kotlin.math.max
import kotlin.math.min

private const val COLUMN_REM = 22.0
private const val HEIGHT_JSON_ID = "world-height-json"
private const val HEIGHT_KOTLIN_ID = "world-height-kotlin"
private const val LABEL_GAP_REM = 0.15
private const val LINE_REM = 1.1
private const val MAX_Y = 2031
private const val MIN_Y = -2032
private const val TICK_REM = 1.0

private val LAVA = Color("#ff8a1f")
private val WATER = Color("#3f76e4")

/** A block texture drawn from world height [from] up to [to], both edges in world coordinates (a block `y` spans `y`..`y + 1`). */
private class Layer(val texture: String, val from: Double, val to: Double)

/** What an anchor resolves against: the intersection of the dimension type and its noise settings, with the sea level of the latter. */
private enum class Dimension(val label: String, val minY: Int, val height: Int, val seaLevel: Int, val seaColor: CSSColorValue, val note: String) {
	OVERWORLD("Overworld", -64, 384, 63, WATER, ""),
	NETHER(
		"Nether", 0, 128, 32, LAVA,
		" The dimension is 256 blocks tall, but in vanilla anchors resolve against the 128 blocks of its noise settings, so `belowTop(0)` is 127.",
	),
	END(
		"End", 0, 128, 0, WATER,
		" The dimension is 256 blocks tall, but in vanilla anchors resolve against the 128 blocks of its noise settings, so `belowTop(0)` is 127.",
	);

	val top get() = minY + height - 1

	val hasBedrock get() = this != END

	val layers
		get() = when (this) {
			OVERWORLD -> listOf(Layer("deepslate", minY.toDouble(), 0.0), Layer("stone", 0.0, seaLevel + 0.5))
			NETHER -> listOf(Layer("netherrack", minY.toDouble(), top + 1.0))
			END -> listOf(Layer("end_stone", minY.toDouble(), top + 1.0))
		}

	val summary get() = "Bottom Y $minY, $height blocks tall, top Y $top, sea level $seaLevel.$note"

	/** Distance from the top of the column, in rem, of the world height [edge] (a block `y` has its center at `y + 0.5`). */
	fun topRem(edge: Double) = ((top + 1 - edge) / height).coerceIn(0.0, 1.0) * COLUMN_REM

	fun contains(y: Int) = y in minY..top
}

private enum class AnchorKind(val function: String, val key: String, val default: Int) {
	ABSOLUTE("absolute", "absolute", 64),
	ABOVE_BOTTOM("aboveBottom", "above_bottom", 8),
	BELOW_TOP("belowTop", "below_top", 10),
	RELATIVE_TO_SEA_LEVEL("relativeToSeaLevel", "relative_to_sea_level", 10);

	fun resolve(world: Dimension, offset: Int) = when (this) {
		ABSOLUTE -> offset
		ABOVE_BOTTOM -> world.minY + offset
		BELOW_TOP -> world.top - offset
		RELATIVE_TO_SEA_LEVEL -> world.seaLevel + offset
	}
}

private data class Anchor(val kind: AnchorKind, val offset: Int) {
	val call get() = "${kind.function}($offset)"

	val json get() = "{\n\t\t\"${kind.key}\": $offset\n\t}"
}

/** A real vanilla height range: where it is [file] and [field], and the [dimension] and anchors it uses. */
private class HeightPreset(
	val label: String,
	val file: String,
	val field: String,
	val note: String,
	val dimension: Dimension,
	val low: Anchor,
	val high: Anchor,
)

private val HEIGHT_PRESETS = listOf(
	HeightPreset(
		label = "Cave carver",
		file = "worldgen/carver/cave.json",
		field = "y",
		note = "height of the cave carver.",
		dimension = Dimension.OVERWORLD,
		low = Anchor(AnchorKind.ABOVE_BOTTOM, 8),
		high = Anchor(AnchorKind.ABSOLUTE, 180),
	),
	HeightPreset(
		label = "Canyon carver",
		file = "worldgen/carver/canyon.json",
		field = "y",
		note = "height of the canyon carver.",
		dimension = Dimension.OVERWORLD,
		low = Anchor(AnchorKind.ABSOLUTE, 10),
		high = Anchor(AnchorKind.ABSOLUTE, 67),
	),
	HeightPreset(
		label = "Nether gold ore",
		file = "worldgen/placed_feature/ore_gold_nether.json",
		field = "height_range",
		note = "height of the nether gold ore veins, 10 blocks from the floor and the ceiling.",
		dimension = Dimension.NETHER,
		low = Anchor(AnchorKind.ABOVE_BOTTOM, 10),
		high = Anchor(AnchorKind.BELOW_TOP, 10),
	),
	HeightPreset(
		label = "Ancient debris",
		file = "worldgen/placed_feature/ore_debris_small.json",
		field = "height_range",
		note = "height of the small ancient debris veins, 8 blocks from the floor and the ceiling.",
		dimension = Dimension.NETHER,
		low = Anchor(AnchorKind.ABOVE_BOTTOM, 8),
		high = Anchor(AnchorKind.BELOW_TOP, 8),
	),
)

private enum class Role { ANCHOR, IGNORED, RANGE }

/** A horizontal line over the column at the resolved [y], pinned to an edge with an arrow when the world doesn't contain it. */
private class Marker(val name: String, val y: Int, val role: Role, world: Dimension) {
	val outside = !world.contains(y)

	val arrow = if (y > world.top) "↑ " else if (outside) "↓ " else ""

	val center = world.topRem(y + 0.5)

	val lines = if (outside) 3 else 2

	val text get() = "$name Y $y"
}

private fun markers(world: Dimension, anchors: Map<AnchorKind, Int>, low: Anchor, high: Anchor): List<Marker> {
	val lowY = low.kind.resolve(world, low.offset)
	val highY = high.kind.resolve(world, high.offset)
	return AnchorKind.entries.map { Marker(Anchor(it, anchors.getValue(it)).call, it.resolve(world, anchors.getValue(it)), Role.ANCHOR, world) } +
		Marker("min", lowY, Role.RANGE, world) +
		Marker("max", highY, if (lowY > highY) Role.IGNORED else Role.RANGE, world)
}

/** Top offsets (rem) of labels wanting to be centered on [centers], pushed apart to never overlap and kept inside `0..COLUMN_REM`. */
private fun spread(centers: List<Double>, heights: List<Double>): List<Double> {
	val order = centers.indices.sortedBy { centers[it] }
	val tops = DoubleArray(centers.size)
	var floor = 0.0
	order.forEach {
		tops[it] = max(centers[it] - heights[it] / 2, floor)
		floor = tops[it] + heights[it] + LABEL_GAP_REM
	}
	var ceiling = COLUMN_REM
	order.asReversed().forEach {
		tops[it] = min(tops[it], ceiling - heights[it])
		ceiling = tops[it] - LABEL_GAP_REM
	}
	return tops.toList()
}

private fun Dimension.ticks(): List<Pair<Int, String>> {
	val names = mutableMapOf<Int, MutableList<String>>()
	names.getOrPut(top) { mutableListOf() } += "top"
	names.getOrPut(seaLevel) { mutableListOf() } += "sea level"
	names.getOrPut(minY) { mutableListOf() } += "bottom"
	if (minY < 0 && top > 0) names.getOrPut(0) { mutableListOf() }
	return names.toList().map { (y, labels) -> y to (labels.joinToString(", ").ifEmpty { "Y" } + " $y") }
}

private fun reading(world: Dimension, low: Anchor, high: Anchor): String {
	val lowY = low.kind.resolve(world, low.offset)
	val highY = high.kind.resolve(world, high.offset)
	if (lowY > highY) return "min above max: the game always uses min (Y $lowY) and logs a warning."
	val count = highY - lowY + 1
	val spread = if (count == 1) "Features always land at Y $lowY, the only possible height."
	else "Features land between Y $lowY and Y $highY, $count possible heights, each as likely."
	val reach = if (world.contains(lowY) && world.contains(highY)) "" else " The range reaches outside the world (Y ${world.minY} to ${world.top}), and the game doesn't clamp it."
	return spread + reach
}

/** Resolves vertical anchors and `uniform` height ranges against a drawn Overworld, Nether or End column, with the matching Kotlin call and JSON. */
@Composable
fun WorldHeightDiagram() {
	Style(WorldHeightStyle)

	var world by remember { mutableStateOf(Dimension.OVERWORLD) }
	var anchors by remember { mutableStateOf(AnchorKind.entries.associateWith { it.default }) }
	var low by remember { mutableStateOf(Anchor(AnchorKind.ABOVE_BOTTOM, 8)) }
	var high by remember { mutableStateOf(Anchor(AnchorKind.ABSOLUTE, 180)) }
	var preset by remember { mutableStateOf<HeightPreset?>(null) }

	val marks = markers(world, anchors, low, high)
	val reading = reading(world, low, high)
	val warning = marks.any { it.role == Role.IGNORED }
	val kotlin = "uniformHeightProvider(${low.call}, ${high.call})"
	val json = "{\n\t\"type\": \"minecraft:uniform\",\n\t\"min_inclusive\": ${low.json},\n\t\"max_inclusive\": ${high.json}\n}"
	val label = "${world.label}, from Y ${world.minY} to ${world.top}, sea level ${world.seaLevel}. " +
		marks.joinToString(". ") { "${it.text}${if (it.outside) ", outside the world, not clamped" else ""}" } + ". $reading"

	Div({ classes(WorldHeightStyle.diagram) }) {
		HeightPresetRow(preset, preset?.let { it.dimension != world || it.low != low || it.high != high } == true) { chosen ->
			if (chosen == preset) {
				preset = null
				return@HeightPresetRow
			}
			world = chosen.dimension
			low = chosen.low
			high = chosen.high
			preset = chosen
		}

		HeightPillGroup("Dimension", Dimension.entries, world, Dimension::label) { world = it }

		Div({ classes(WorldHeightStyle.body) }) {
			WorldColumn(world, marks, label)

			Div({ classes(WorldHeightStyle.controls) }) {
				Div({ classes(WorldHeightStyle.section) }) {
					Span({ classes(WorldHeightStyle.sectionTitle) }) { Text("Vertical anchors") }
					AnchorKind.entries.forEach { kind ->
						val offset = anchors.getValue(kind)
						val marker = marks[kind.ordinal]
						Div({ classes(WorldHeightStyle.anchorRow) }) {
							Label("world-height-anchor-${kind.key}", { classes(WorldHeightStyle.controlLabel) }) { Text(kind.function) }
							OffsetInput("world-height-anchor-${kind.key}", offset) { anchors = anchors + (kind to it) }
							Resolved(marker)
						}
					}
				}

				Div({ classes(WorldHeightStyle.section) }) {
					Span({ classes(WorldHeightStyle.sectionTitle) }) { Text("Height range, uniformHeightProvider") }
					RangeEnd("minInclusive", low, marks[AnchorKind.entries.size]) { low = it }
					RangeEnd("maxInclusive", high, marks[AnchorKind.entries.size + 1]) { high = it }
					Div({
						classes(WorldHeightStyle.reading)
						if (warning) classes(WorldHeightStyle.warning)
					}) { Text(reading) }
				}
			}
		}

		Div({ classes(WorldHeightStyle.caption) }) { Text(world.summary.replace("`", "") + " The bands are stylized, real terrain varies.") }

		Tabs(
			listOf(
				Tab("Kotlin") { Div({ id(HEIGHT_KOTLIN_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } } },
				Tab("JSON") { Div({ id(HEIGHT_JSON_ID) }) { key(json) { CodeBlock(json, "json") } } },
			),
		)
		highlightCodeIn(HEIGHT_KOTLIN_ID, kotlin)
		highlightCodeIn(HEIGHT_JSON_ID, json)
	}
}

@Composable
private fun <T : Any> HeightPillGroup(label: String, options: List<T>, selected: T?, text: (T) -> String, onSelect: (T) -> Unit) {
	Div({
		classes(WorldHeightStyle.group)
		attr("aria-label", label)
		attr("role", "group")
	}) {
		options.forEach { option ->
			Button({
				classes(WorldHeightStyle.pill)
				if (option == selected) classes(WorldHeightStyle.pillActive)
				attr("aria-pressed", (option == selected).toString())
				attr("type", "button")
				onClick { onSelect(option) }
			}) { Text(text(option)) }
		}
	}
}

@Composable
private fun HeightPresetRow(selected: HeightPreset?, edited: Boolean, onSelect: (HeightPreset) -> Unit) {
	Div({ classes(WorldHeightStyle.presets) }) {
		Span({ classes(WorldHeightStyle.sectionTitle) }) { Text("Try a vanilla range") }
		HeightPillGroup("Vanilla presets", HEIGHT_PRESETS, selected, HeightPreset::label, onSelect)
		selected?.let {
			Div({ classes(WorldHeightStyle.caption) }) {
				Text("from ")
				Span({ classes(WorldHeightStyle.captionFile) }) { Text(it.file) }
				Text(", ")
				Span({ classes(WorldHeightStyle.captionFile) }) { Text(it.field) }
				Text(": ${it.note}${if (edited) " (edited)" else ""}")
			}
		}
	}
}

@Composable
private fun OffsetInput(inputId: String, offset: Int, onChange: (Int) -> Unit) {
	NumberInput(offset, MIN_Y, MAX_Y) {
		id(inputId)
		classes(WorldHeightStyle.number)
		step(1)
		onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let { onChange(it.toInt().coerceIn(MIN_Y, MAX_Y)) } }
	}
}

@Composable
private fun Resolved(marker: Marker) {
	Span({
		classes(WorldHeightStyle.resolved)
		if (marker.outside) classes(WorldHeightStyle.warning)
	}) { Text("= Y ${marker.y}${if (marker.outside) ", outside the world, not clamped" else ""}") }
}

@Composable
private fun RangeEnd(name: String, anchor: Anchor, marker: Marker, onChange: (Anchor) -> Unit) {
	val inputId = "world-height-$name"

	Div({ classes(WorldHeightStyle.rangeEnd) }) {
		Label(inputId, { classes(WorldHeightStyle.controlLabel) }) { Text(name) }
		HeightPillGroup("$name anchor type", AnchorKind.entries, anchor.kind, AnchorKind::function) { onChange(anchor.copy(kind = it)) }
		Div({ classes(WorldHeightStyle.anchorRow) }) {
			OffsetInput(inputId, anchor.offset) { onChange(anchor.copy(offset = it)) }
			Resolved(marker)
		}
	}
}

@Composable
private fun WorldColumn(world: Dimension, marks: List<Marker>, label: String) {
	val ticks = world.ticks()
	val tickTops = spread(ticks.map { world.topRem(it.first + 0.5) }, ticks.map { TICK_REM })
	val markTops = spread(marks.map { it.center }, marks.map { it.lines * LINE_REM })
	val low = marks[AnchorKind.entries.size]
	val high = marks[AnchorKind.entries.size + 1]

	Div({
		classes(WorldHeightStyle.world)
		attr("aria-label", label)
		attr("role", "img")
	}) {
		Div({ classes(WorldHeightStyle.ticks) }) {
			ticks.forEachIndexed { index, (_, text) ->
				Div({
					classes(WorldHeightStyle.tick)
					style { top(tickTops[index].cssRem) }
				}) {
					Span { Text(text) }
					Span({ classes(WorldHeightStyle.tickMark) })
				}
			}
		}

		Div({ classes(WorldHeightStyle.column) }) {
			world.layers.forEach { layer ->
				Div({
					classes(WorldHeightStyle.layer)
					style {
						backgroundImage(mcTexture("block/${layer.texture}"))
						height((world.topRem(layer.from) - world.topRem(layer.to)).cssRem)
						top(world.topRem(layer.to).cssRem)
					}
				})
			}
			if (world.hasBedrock) Div({
				classes(WorldHeightStyle.layer, WorldHeightStyle.bedrock)
				style { backgroundImage(mcTexture("block/bedrock")) }
			})
			Div({
				classes(WorldHeightStyle.seaLine)
				style {
					borderTop(2.px, LineStyle.Dashed, world.seaColor)
					top(world.topRem(world.seaLevel + 0.5).cssRem)
				}
			})
			if (low.role == Role.RANGE && low.y <= high.y) Div({
				classes(WorldHeightStyle.band)
				style {
					height((world.topRem(low.y.toDouble()) - world.topRem(high.y + 1.0)).cssRem)
					top(world.topRem(high.y + 1.0).cssRem)
				}
			})
			marks.forEach { marker ->
				Div({
					classes(WorldHeightStyle.line)
					when (marker.role) {
						Role.ANCHOR -> Unit
						Role.IGNORED -> classes(WorldHeightStyle.lineIgnored)
						Role.RANGE -> classes(WorldHeightStyle.lineRange)
					}
					style { top(marker.center.cssRem) }
				})
			}
		}

		Div({ classes(WorldHeightStyle.labels) }) {
			marks.forEachIndexed { index, marker ->
				Div({
					classes(WorldHeightStyle.chip)
					when (marker.role) {
						Role.ANCHOR -> Unit
						Role.IGNORED -> classes(WorldHeightStyle.chipIgnored)
						Role.RANGE -> classes(WorldHeightStyle.chipRange)
					}
					style { top(markTops[index].cssRem) }
				}) {
					Div { Text(marker.arrow + marker.name) }
					Div { Text("Y ${marker.y}" + if (marker.role == Role.IGNORED) " (ignored)" else "") }
					if (marker.outside) Div({ classes(WorldHeightStyle.chipNote) }) { Text("outside, not clamped") }
				}
			}
		}
	}
}

private object WorldHeightStyle : StyleSheet() {
	val anchorRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.4.cssRem, 0.7.cssRem)
		minWidth(0.px)
	}

	val band by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.35))
		left(0.px)
		position(Position.Absolute)
		right(0.px)
	}

	val bedrock by style {
		bottom(0.px)
		height(0.75.cssRem)
	}

	val body by style {
		alignItems(AlignItems.FlexStart)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.5.cssRem)
		minWidth(0.px)

		smMax(self) {
			flexDirection(FlexDirection.Column)
		}
	}

	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.8.cssRem)
	}

	val captionFile by style {
		color(LandingVars.Text.value())
		monoFont()
	}

	val chip by style {
		color(LandingVars.Text.value())
		fontSize(0.72.cssRem)
		left(0.px)
		lineHeight(LINE_REM.cssRem)
		monoFont()
		overflow(Overflow.Hidden)
		paddingLeft(0.6.cssRem)
		position(Position.Absolute)
		right(0.px)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val chipIgnored by style {
		color(LandingVars.Muted.value())
	}

	val chipNote by style {
		color(LandingVars.Gold.value())
	}

	val chipRange by style {
		color(LandingVars.Accent.value())
	}

	val column by style {
		backgroundColor(LandingVars.Surface.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		boxSizing(BoxSizing.ContentBox)
		flexShrink(0)
		height(COLUMN_REM.cssRem)
		position(Position.Relative)
		width(5.cssRem)

		smMax(self) {
			width(4.cssRem)
		}
	}

	val controlLabel by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		minWidth(9.cssRem)
		monoFont()
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexBasis(20.cssRem)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		flexShrink(1)
		gap(1.2.cssRem)
		minWidth(0.px)
	}

	val diagram by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
		width(100.percent)
	}

	val group by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.35.cssRem)
	}

	val labels by style {
		flexGrow(1)
		flexShrink(1)
		height(COLUMN_REM.cssRem)
		minWidth(0.px)
		position(Position.Relative)
	}

	val layer by style {
		backgroundSize(BackgroundSize.of(2.cssRem, 2.cssRem))
		imageRendering(ImageRendering.Pixelated)
		left(0.px)
		opacity(0.6)
		position(Position.Absolute)
		right(0.px)
	}

	val line by style {
		backgroundColor(LandingVars.Text.value())
		borderBottom(1.px, LineStyle.Solid, Color("#000000aa"))
		borderTop(1.px, LineStyle.Solid, Color("#000000aa"))
		height(2.px)
		left((-0.4).cssRem)
		marginTop((-2).px)
		position(Position.Absolute)
		right((-0.4).cssRem)
	}

	val lineIgnored by style {
		backgroundColor(LandingVars.Muted.value())
	}

	val lineRange by style {
		backgroundColor(LandingVars.Accent.value())
	}

	val number by style {
		backgroundColor(LandingVars.Surface.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.4.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(LandingVars.Text.value())
		fontSize(0.85.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.5.cssRem)
		width(5.5.cssRem)
	}

	val pill by style {
		backgroundColor(LandingVars.Card.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(999.px)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		fontSize(0.8.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.8.cssRem)
	}

	val pillActive by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.18))
		border(1.px, LineStyle.Solid, LandingVars.Accent.value())
		color(LandingVars.Text.value())
	}

	val presets by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
	}

	val rangeEnd by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.4.cssRem)
	}

	val reading by style {
		color(LandingVars.Text.value())
		fontSize(0.95.cssRem)
	}

	val resolved by style {
		color(LandingVars.Text.value())
		fontSize(0.8.cssRem)
		minWidth(0.px)
		monoFont()
	}

	val seaLine by style {
		left(0.px)
		position(Position.Absolute)
		right(0.px)
	}

	val section by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.7.cssRem)
	}

	val sectionTitle by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
	}

	val tick by style {
		alignItems(AlignItems.Center)
		color(LandingVars.Muted.value())
		display(DisplayStyle.Flex)
		fontSize(0.72.cssRem)
		gap(0.3.cssRem)
		height(TICK_REM.cssRem)
		justifyContent(JustifyContent.FlexEnd)
		monoFont()
		position(Position.Absolute)
		right(0.px)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val tickMark by style {
		backgroundColor(LandingVars.Muted.value())
		height(1.px)
		width(0.4.cssRem)
	}

	val ticks by style {
		flexShrink(0)
		height(COLUMN_REM.cssRem)
		position(Position.Relative)
		width(6.5.cssRem)

		smMax(self) {
			width(6.cssRem)
		}
	}

	val warning by style {
		color(LandingVars.Gold.value())
	}

	val world by style {
		backgroundColor(LandingVars.Pane.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.8.cssRem)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexBasis(26.cssRem)
		flexGrow(0)
		flexShrink(1)
		gap(0.4.cssRem)
		minWidth(0.px)
		padding(1.cssRem)

		smMax(self) {
			flexBasis(FlexBasis.Auto)
			width(100.percent)
		}
	}
}
