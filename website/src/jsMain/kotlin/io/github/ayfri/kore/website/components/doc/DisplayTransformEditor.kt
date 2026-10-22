package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.utils.Details
import io.github.ayfri.kore.website.utils.Summary
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import kotlin.math.pow
import kotlin.math.round

private const val DISPLAY_COMMAND_ID = "display-command"
private const val DISPLAY_KOTLIN_ID = "display-kotlin"

private fun rounded(value: Double, decimals: Int) = 10.0.pow(decimals).let { round(value * it) / it + 0.0 }

private fun DisplayVec3.rounded(decimals: Int) = DisplayVec3(rounded(x, decimals), rounded(y, decimals), rounded(z, decimals))

/** A float as the game's NBT writes it, always with a decimal point: `1.0f`, `0.7071f`. */
private fun Double.nbtFloat(decimals: Int) = rounded(this, decimals).toString().let { if ('.' in it) it else "$it.0" } + "f"

/** A float literal as Kotlin writes it, without a useless `.0`: `2f`, `0.5f`. */
private fun Double.kotlinFloat(decimals: Int) = rounded(this, decimals).toString().removeSuffix(".0") + "f"

private fun String.indented() = lines().joinToString("\n") { if (it.isEmpty()) it else "\t$it" }

private fun List<Double>.nbtList(decimals: Int) = joinToString(",", "[", "]") { it.nbtFloat(decimals) }

/** The four parts of a display `transformation`, the rotations in degrees as the sliders edit them. */
private data class DisplayParts(
	val left: DisplayVec3 = DisplayVec3.ZERO,
	val right: DisplayVec3 = DisplayVec3.ZERO,
	val scale: DisplayVec3 = DisplayVec3.ONE,
	val translation: DisplayVec3 = DisplayVec3.ZERO,
) {
	/** The radians `eulerAngles` is called with, as the Kotlin tab prints them, so the preview and the code describe the same rotation. */
	val leftRadians get() = left.radians()
	val rightRadians get() = right.radians()

	val isDefault get() = leftRadians == DisplayVec3.ZERO && rightRadians == DisplayVec3.ZERO && scale == DisplayVec3.ONE && translation == DisplayVec3.ZERO

	private val leftQuat get() = leftRadians.let { DisplayQuat.eulerXYZ(it.x, it.y, it.z) }
	private val rightQuat get() = rightRadians.let { DisplayQuat.eulerXYZ(it.x, it.y, it.z) }

	/** What the game applies around the pivot: `rightRotation`, then `scale`, then `leftRotation`. */
	val aroundPivot get() = leftQuat.toMatrix() * DisplayMat4.scaling(scale) * rightQuat.toMatrix()

	/** `translation * leftRotation * scale * rightRotation`, in Minecraft units. */
	val matrix get() = DisplayMat4.translation(translation) * aroundPivot

	private fun DisplayVec3.radians() = DisplayVec3(rounded(x.degreesToRadians(), 4), rounded(y.degreesToRadians(), 4), rounded(z.degreesToRadians(), 4))

	/** The `transformation` of the `summon` command, all four parts once any differs from the default. */
	fun nbt() = "transformation:{" +
		"left_rotation:${leftQuat.components.nbtList(4)}," +
		"right_rotation:${rightQuat.components.nbtList(4)}," +
		"scale:${listOf(scale.x, scale.y, scale.z).nbtList(3)}," +
		"translation:${listOf(translation.x, translation.y, translation.z).nbtList(3)}}"

	/** The Kotlin `transformation { }` block with the non-default parts only, in alphabetical order. */
	fun kotlinBlock(): String {
		fun rotation(name: String, radians: DisplayVec3) =
			"$name {\n\teulerAngles(${radians.x.kotlinFloat(4)}, ${radians.y.kotlinFloat(4)}, ${radians.z.kotlinFloat(4)})\n}"

		val blocks = listOfNotNull(
			rotation("leftRotation", leftRadians).takeIf { leftRadians != DisplayVec3.ZERO },
			rotation("rightRotation", rightRadians).takeIf { rightRadians != DisplayVec3.ZERO },
			"scale = Vec3f(${scale.x.kotlinFloat(3)}, ${scale.y.kotlinFloat(3)}, ${scale.z.kotlinFloat(3)})".takeIf { scale != DisplayVec3.ONE },
			DisplayAxis.entries.filter { translation[it] != 0.0 }
				.joinToString("\n", "translation {\n", "\n}") { "\t${it.name.lowercase()} = ${translation[it].kotlinFloat(3)}" }
				.takeIf { translation != DisplayVec3.ZERO },
		)
		return blocks.joinToString("\n\n", "transformation {\n", "\n}") { it.indented() }
	}
}

private fun displayKotlin(kind: DisplayKind, billboard: DisplayBillboard, parts: DisplayParts) = listOfNotNull(
	kind.content,
	parts.kotlinBlock().takeUnless { parts.isDefault },
	"billboardMode = BillboardMode.${billboard.name}".takeIf { billboard != DisplayBillboard.FIXED },
).joinToString("\n\n", "val display = ${kind.function} {\n", "\n}\n\nsummon(display.entityType, vec3(), display.toNbt())") { it.indented() }

private fun displayCommand(kind: DisplayKind, billboard: DisplayBillboard, parts: DisplayParts) = listOfNotNull(
	"billboard:\"${billboard.name.lowercase()}\"".takeIf { billboard != DisplayBillboard.FIXED },
	parts.nbt().takeUnless { parts.isDefault },
	kind.nbt,
).joinToString(",", "summon minecraft:${kind.entity} ~ ~ ~ {", "}")

private fun DisplayVec3.reading() = listOf(x, y, z).joinToString(", ") { rounded(it, 2).toString() }

/**
 * Edits the `transformation` of a block, item or text display on a 3D preview you can turn, and writes the equivalent Kotlin and `summon`
 * command, with rotations rounded to 4 decimals. Each type pivots differently, so switching the type keeps the values and shows what changes.
 */
@Composable
fun DisplayTransformEditor() {
	Style(DisplayEditorStyle)

	var billboard by remember { mutableStateOf(DisplayBillboard.FIXED) }
	var camera by remember { mutableStateOf(DisplayCamera()) }
	var kind by remember { mutableStateOf(DisplayKind.BLOCK) }
	var parts by remember { mutableStateOf(DisplayParts()) }
	var uniform by remember { mutableStateOf(true) }

	val billboardMatrix = billboard.matrix(camera)
	val center = parts.matrix.transform(kind.center).reading()
	val kotlin = displayKotlin(kind, billboard, parts)
	val command = displayCommand(kind, billboard, parts)

	Div({
		id("display-transform-editor")
		classes(DisplayEditorStyle.editor)
	}) {
		Div({ classes(DisplayEditorStyle.toolbar) }) {
			DisplayField("Display type") {
				PillGroup("Display type", DisplayKind.entries, kind, { it.entity }) { kind = it }
			}
			DisplayField("Billboard") {
				PillGroup("Billboard", DisplayBillboard.entries, billboard, { it.name.lowercase() }) { billboard = it }
				Div({ classes(DisplayEditorStyle.effect) }) { Text(billboard.effect) }
			}
		}

		DisplayScene(
			kind = kind,
			model = billboardMatrix * parts.matrix,
			camera = camera,
			label = "A ${kind.entity} showing a ${kind.subject}, its center at $center blocks from the entity, billboard ${billboard.name.lowercase()}. " +
				"Drag or use the arrow keys to turn the camera.",
			floorCells = 6,
			ghost = billboardMatrix,
			onCamera = { camera = it },
		)

		Div({ classes(DisplayEditorStyle.actions) }) {
			Div({ classes(DisplayEditorStyle.reading) }) { Text("The model's center sits at $center blocks from the entity.") }
			Div({ classes(DocControlsStyle.group) }) {
				if (kind != DisplayKind.ITEM) Pill("Center on the entity") {
					parts = parts.copy(translation = (-parts.aroundPivot.linear(kind.center)).rounded(3))
				}
				Pill("Reset") { parts = DisplayParts() }
			}
		}

		Div({ classes(DocControlsStyle.panel, DisplayEditorStyle.controls) }) {
			Div({ classes(DisplayEditorStyle.groups) }) {
				VectorGroup("translation", "display-translation", parts.translation, -3.0, 3.0, 0.05) { axis, value ->
					parts = parts.copy(translation = parts.translation.with(axis, value))
				}
				VectorGroup("leftRotation (degrees)", "display-left", parts.left, -180.0, 180.0, 1.0) { axis, value ->
					parts = parts.copy(left = parts.left.with(axis, value))
				}
				VectorGroup(
					"scale", "display-scale", parts.scale, 0.1, 3.0, 0.05,
					header = {
						Pill("Uniform", uniform) {
							uniform = !uniform
							if (uniform) parts = parts.copy(scale = DisplayVec3(parts.scale.x, parts.scale.x, parts.scale.x))
						}
					},
				) { axis, value ->
					parts = parts.copy(scale = if (uniform) DisplayVec3(value, value, value) else parts.scale.with(axis, value))
				}
			}

			Details({ classes(DisplayEditorStyle.details) }) {
				Summary({ classes(DisplayEditorStyle.summary) }) { Text("rightRotation, applied before scale") }
				Div({ classes(DisplayEditorStyle.hint) }) { Text("Differs from leftRotation only when the scale isn't uniform.") }
				Div({ classes(DisplayEditorStyle.groups) }) {
					VectorGroup("rightRotation (degrees)", "display-right", parts.right, -180.0, 180.0, 1.0) { axis, value ->
						parts = parts.copy(right = parts.right.with(axis, value))
					}
				}
			}
		}

		Tabs(
			listOf(
				Tab("Kotlin") { Div({ id(DISPLAY_KOTLIN_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } } },
				Tab("Command") { Div({ id(DISPLAY_COMMAND_ID) }) { key(command) { CodeBlock(command, "mcfunction") } } },
			),
		)
		Div({ classes(DisplayEditorStyle.hint) }) {
			Text("Both tabs round rotations to 4 decimals, so the command is equivalent to Kore's output, not identical to it.")
		}
		highlightCodeIn(DISPLAY_KOTLIN_ID, kotlin)
		highlightCodeIn(DISPLAY_COMMAND_ID, command)
	}
}

@Composable
private fun DisplayField(label: String, content: @Composable () -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Span({ classes(DocControlsStyle.controlLabel) }) { Text(label) }
		content()
	}
}

/** Three sliders, one per axis of [value], under the mono [title] with an optional [header] control on its right. */
@Composable
private fun VectorGroup(
	title: String,
	idPrefix: String,
	value: DisplayVec3,
	min: Double,
	max: Double,
	step: Double,
	header: @Composable () -> Unit = {},
	onChange: (DisplayAxis, Double) -> Unit,
) {
	Div({
		classes(DisplayEditorStyle.group)
		attr("aria-label", title)
		attr("role", "group")
	}) {
		Div({ classes(DisplayEditorStyle.groupHeader) }) {
			Span({ classes(DisplayEditorStyle.groupTitle) }) { Text(title) }
			header()
		}
		DisplayAxis.entries.forEach { axis ->
			val name = axis.name.lowercase()
			SliderField("$idPrefix-$name", name, value[axis], min, max, step) { onChange(axis, rounded(it.coerceIn(min, max), 3)) }
		}
	}
}

private object DisplayEditorStyle : StyleSheet() {
	val actions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem, 1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
	}

	val details by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.7.cssRem)
	}

	val editor by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
		width(100.percent)
	}

	val effect by style {
		color(LandingVars.Muted.value())
		fontSize(0.85.cssRem)
	}

	val group by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.7.cssRem)
		minWidth(0.px)
	}

	val groupHeader by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		minHeight(1.9.cssRem)
	}

	val groups by style {
		display(DisplayStyle.Grid)
		gap(0.9.cssRem, 1.5.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val groupTitle by style {
		color(LandingVars.Text.value())
		fontSize(0.8.cssRem)
		monoFont()
	}

	val hint by style {
		color(LandingVars.Muted.value())
		fontSize(0.85.cssRem)
	}

	val reading by style {
		color(LandingVars.Text.value())
		fontSize(0.95.cssRem)
		minWidth(0.px)
	}

	val summary by style {
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		fontSize(0.8.cssRem)
		monoFont()
	}

	val toolbar by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
	}
}
