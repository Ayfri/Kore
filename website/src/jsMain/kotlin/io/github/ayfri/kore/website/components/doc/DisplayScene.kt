@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.LinearGradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.mcTexture
import io.github.ayfri.kore.website.components.mc.McColor
import io.github.ayfri.kore.website.components.mc.McFont
import io.github.ayfri.kore.website.components.mc.McItemModel
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.perspective
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

internal const val DISPLAY_TEXT = "Hello"

private const val AXIS_LENGTH = 1.5
private const val BLOCK_PX = 128
private const val COMPACT_ZOOM = 0.8
private const val DOT_SCREEN_PX = 8.0
private const val DOT_SCREEN_Z = 400.0
private const val DRAG_DEGREES_PER_PIXEL = 0.5
private const val ITEM_TEXELS = 16
private const val KEY_DEGREES = 10.0
private const val LABEL_LIFT_PX = 16
private const val LINE_PX = 3
private const val MAX_PITCH = 80.0
private const val PERSPECTIVE_PX = 900.0
private const val PX = 1.0 / BLOCK_PX
private const val RIBBON_PX = 5
private const val SEAM_PX = 1.5
private const val SMALL_ZOOM = 0.6
private const val TEXT_PX_PER_BLOCK = 40

/** Faces are drawn at 8 px per texture pixel, which the zoom shrinks to 5 screen pixels: the compositor then only ever minifies, so edges stay sharp. */
private const val ZOOM = 80.0 / BLOCK_PX

private val GRASS_TINT = Color("#91bd59")
private val TEXT_BACKGROUND = rgba(0, 0, 0, 0.25)

private fun round6(value: Double) = round(value * 1_000_000) / 1_000_000 + 0.0

internal fun Double.degreesToRadians() = this * PI / 180

/** A point or direction in Minecraft units, X east, Y up and Z south. */
internal data class DisplayVec3(val x: Double, val y: Double, val z: Double) {
	operator fun get(axis: DisplayAxis) = when (axis) {
		DisplayAxis.X -> x
		DisplayAxis.Y -> y
		DisplayAxis.Z -> z
	}

	operator fun plus(other: DisplayVec3) = DisplayVec3(x + other.x, y + other.y, z + other.z)

	operator fun minus(other: DisplayVec3) = DisplayVec3(x - other.x, y - other.y, z - other.z)

	operator fun times(factor: Double) = DisplayVec3(x * factor, y * factor, z * factor)

	operator fun unaryMinus() = DisplayVec3(-x, -y, -z)

	fun cross(other: DisplayVec3) = DisplayVec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)

	fun with(axis: DisplayAxis, value: Double) = when (axis) {
		DisplayAxis.X -> copy(x = value)
		DisplayAxis.Y -> copy(y = value)
		DisplayAxis.Z -> copy(z = value)
	}

	companion object {
		val ONE get() = DisplayVec3(1.0, 1.0, 1.0)
		val ZERO get() = DisplayVec3(0.0, 0.0, 0.0)
	}
}

/** The three axes with the colors of the F3 screen. */
internal enum class DisplayAxis(val color: String, val unit: DisplayVec3) {
	X(McColor.RED, DisplayVec3(1.0, 0.0, 0.0)),
	Y(McColor.GREEN, DisplayVec3(0.0, 1.0, 0.0)),
	Z(McColor.BLUE, DisplayVec3(0.0, 0.0, 1.0)),
}

internal class DisplayQuat(private val x: Double, private val y: Double, private val z: Double, private val w: Double) {
	val components get() = listOf(x, y, z, w)

	fun toMatrix() = DisplayMat4(
		doubleArrayOf(
			1 - 2 * (y * y + z * z), 2 * (x * y - z * w), 2 * (x * z + y * w), 0.0,
			2 * (x * y + z * w), 1 - 2 * (x * x + z * z), 2 * (y * z - x * w), 0.0,
			2 * (x * z - y * w), 2 * (y * z + x * w), 1 - 2 * (x * x + y * y), 0.0,
			0.0, 0.0, 0.0, 1.0,
		)
	)

	companion object {
		/** JOML's `Quaternionf().rotateXYZ(rx, ry, rz)`, [rx] first and [rz] last in the product, angles in radians. */
		fun eulerXYZ(rx: Double, ry: Double, rz: Double): DisplayQuat {
			val sx = sin(rx / 2)
			val cx = cos(rx / 2)
			val sy = sin(ry / 2)
			val cy = cos(ry / 2)
			val sz = sin(rz / 2)
			val cz = cos(rz / 2)
			return DisplayQuat(
				x = sx * cy * cz + cx * sy * sz,
				y = cx * sy * cz - sx * cy * sz,
				z = cx * cy * sz + sx * sy * cz,
				w = cx * cy * cz - sx * sy * sz,
			)
		}
	}
}

/** A row-major 4x4 transform of column vectors. */
internal class DisplayMat4(private val m: DoubleArray) {
	operator fun times(other: DisplayMat4): DisplayMat4 {
		val result = DoubleArray(16)
		for (row in 0..3) for (column in 0..3) {
			var sum = 0.0
			for (k in 0..3) sum += m[row * 4 + k] * other.m[k * 4 + column]
			result[row * 4 + column] = sum
		}
		return DisplayMat4(result)
	}

	/** The 16 numbers of CSS `matrix3d()`, the translation column being scaled by [px] since the faces are drawn in pixels. */
	fun columnMajor(px: Double) = buildList {
		for (column in 0..3) for (row in 0..3) add(round6(m[row * 4 + column] * if (column == 3 && row < 3) px else 1.0))
	}

	/** Turns [v] without moving it, like a direction. */
	fun linear(v: DisplayVec3) = DisplayVec3(
		m[0] * v.x + m[1] * v.y + m[2] * v.z,
		m[4] * v.x + m[5] * v.y + m[6] * v.z,
		m[8] * v.x + m[9] * v.y + m[10] * v.z,
	)

	fun transform(v: DisplayVec3) = linear(v) + DisplayVec3(m[3], m[7], m[11])

	companion object {
		val IDENTITY get() = scaling(DisplayVec3.ONE)

		fun ofColumns(u: DisplayVec3, v: DisplayVec3, n: DisplayVec3, o: DisplayVec3) = DisplayMat4(
			doubleArrayOf(
				u.x, v.x, n.x, o.x,
				u.y, v.y, n.y, o.y,
				u.z, v.z, n.z, o.z,
				0.0, 0.0, 0.0, 1.0,
			)
		)

		fun rotX(angle: Double) = DisplayMat4(
			doubleArrayOf(
				1.0, 0.0, 0.0, 0.0,
				0.0, cos(angle), -sin(angle), 0.0,
				0.0, sin(angle), cos(angle), 0.0,
				0.0, 0.0, 0.0, 1.0,
			)
		)

		fun rotY(angle: Double) = DisplayMat4(
			doubleArrayOf(
				cos(angle), 0.0, sin(angle), 0.0,
				0.0, 1.0, 0.0, 0.0,
				-sin(angle), 0.0, cos(angle), 0.0,
				0.0, 0.0, 0.0, 1.0,
			)
		)

		fun scaling(s: DisplayVec3) = DisplayMat4(
			doubleArrayOf(
				s.x, 0.0, 0.0, 0.0,
				0.0, s.y, 0.0, 0.0,
				0.0, 0.0, s.z, 0.0,
				0.0, 0.0, 0.0, 1.0,
			)
		)

		fun translation(t: DisplayVec3) = DisplayMat4(
			doubleArrayOf(
				1.0, 0.0, 0.0, t.x,
				0.0, 1.0, 0.0, t.y,
				0.0, 0.0, 1.0, t.z,
				0.0, 0.0, 0.0, 1.0,
			)
		)
	}
}

/** The camera orbiting the scene, in CSS degrees: [yaw] turns the world around the vertical axis, [pitch] looks from above when positive. */
internal data class DisplayCamera(val yaw: Double = -30.0, val pitch: Double = 25.0) {
	fun turned(yawDegrees: Double, pitchDegrees: Double) = DisplayCamera(yaw + yawDegrees, (pitch + pitchDegrees).coerceIn(-MAX_PITCH, MAX_PITCH))
}

/** How a display faces the camera, in the game's order. */
internal enum class DisplayBillboard(val effect: String) {
	FIXED("Keeps the entity's own rotation: turn the camera, the model stays put."),
	VERTICAL("Turns around the vertical axis to face the camera, like a name tag."),
	HORIZONTAL("Tilts around the horizontal axis only to face the camera."),
	CENTER("Faces the camera on both axes, swinging the whole transformed model around the entity position.");

	/** The turn applied before the transformation, undoing the camera's own rotation on the axes this mode follows. */
	fun matrix(camera: DisplayCamera): DisplayMat4 {
		val yaw = DisplayMat4.rotY(-camera.yaw.degreesToRadians())
		val pitch = DisplayMat4.rotX(-camera.pitch.degreesToRadians())
		return when (this) {
			FIXED -> DisplayMat4.IDENTITY
			VERTICAL -> yaw
			HORIZONTAL -> pitch
			CENTER -> yaw * pitch
		}
	}
}

/** One flat rectangle of [width] x [height] blocks: [u] is its texture's right, [v] its down, [n] its outward normal and [o] its top-left corner. */
internal class DisplayFace(
	val o: DisplayVec3,
	val u: DisplayVec3,
	val v: DisplayVec3,
	val n: DisplayVec3,
	val width: Double = 1.0,
	val height: Double = 1.0,
	val texture: String? = null,
	val tinted: Boolean = false,
)

private fun blockFace(o: DisplayVec3, u: DisplayVec3, v: DisplayVec3, n: DisplayVec3, texture: String, tinted: Boolean = false) =
	DisplayFace(o, u, v, n, texture = texture, tinted = tinted)

private fun vec(x: Int, y: Int, z: Int) = DisplayVec3(x.toDouble(), y.toDouble(), z.toDouble())

private val BLOCK_FACES = listOf(
	blockFace(vec(0, 1, 1), vec(1, 0, 0), vec(0, -1, 0), vec(0, 0, 1), "block/grass_block_side"),
	blockFace(vec(1, 1, 0), vec(-1, 0, 0), vec(0, -1, 0), vec(0, 0, -1), "block/grass_block_side"),
	blockFace(vec(1, 1, 1), vec(0, 0, -1), vec(0, -1, 0), vec(1, 0, 0), "block/grass_block_side"),
	blockFace(vec(0, 1, 0), vec(0, 0, 1), vec(0, -1, 0), vec(-1, 0, 0), "block/grass_block_side"),
	blockFace(vec(0, 1, 0), vec(1, 0, 0), vec(0, 0, 1), vec(0, 1, 0), "block/grass_block_top", tinted = true),
	blockFace(vec(0, 0, 1), vec(1, 0, 0), vec(0, 0, -1), vec(0, -1, 0), "block/dirt"),
)

private val ITEM_FACES = listOf(
	DisplayFace(DisplayVec3(-0.5, 0.5, 0.0), vec(1, 0, 0), vec(0, -1, 0), vec(0, 0, 1), texture = "item/diamond_sword"),
)

private const val TEXT_HEIGHT = 10.0 / TEXT_PX_PER_BLOCK

private val TEXT_FACES
	get() = (McFont.width(DISPLAY_TEXT) + 2).toDouble().let { pixels ->
		val width = pixels / TEXT_PX_PER_BLOCK
		listOf(DisplayFace(DisplayVec3(-width / 2, TEXT_HEIGHT, 0.0), vec(1, 0, 0), vec(0, -1, 0), vec(0, 0, 1), width, TEXT_HEIGHT))
	}

/** The three display types, each with its geometry, pivot and the Kotlin and NBT that summon it. */
internal enum class DisplayKind(val entity: String, val function: String, val pivot: String, val content: String, val nbt: String) {
	BLOCK("block_display", "blockDisplay", "bottom north-west corner", "blockState(Blocks.GRASS_BLOCK)", "block_state:\"minecraft:grass_block\""),
	ITEM("item_display", "itemDisplay", "center", "item(Items.DIAMOND_SWORD)", "item:{id:\"minecraft:diamond_sword\",count:1s}"),
	TEXT("text_display", "textDisplay", "bottom center", "text(\"$DISPLAY_TEXT\")", "text:\"$DISPLAY_TEXT\"");

	/** The center of the default model, relative to the pivot and the entity. */
	val center
		get() = when (this) {
			BLOCK -> DisplayVec3(0.5, 0.5, 0.5)
			ITEM -> DisplayVec3.ZERO
			TEXT -> DisplayVec3(0.0, TEXT_HEIGHT / 2, 0.0)
		}

	val faces
		get() = when (this) {
			BLOCK -> BLOCK_FACES
			ITEM -> ITEM_FACES
			TEXT -> TEXT_FACES
		}

	val subject
		get() = when (this) {
			BLOCK -> "grass block"
			ITEM -> "diamond sword"
			TEXT -> "\"$DISPLAY_TEXT\" text"
		}
}

private fun TransformBuilder.matrix3d(matrix: DisplayMat4) {
	val c = matrix.columnMajor(BLOCK_PX.toDouble())
	matrix3d(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], c[9], c[10], c[11], c[12], c[13], c[14], c[15])
}

private fun TransformBuilder.matrix3d(face: DisplayFace) = matrix3d(DisplayMat4.ofColumns(face.u, face.v, face.n, face.o))

private fun ribbon(axis: DisplayVec3, side: DisplayVec3, from: Double, to: Double, lift: Double = 0.0) = DisplayFace(
	o = axis * from - side * (RIBBON_PX * PX / 2) + DisplayVec3(0.0, lift, 0.0),
	u = axis,
	v = side,
	n = axis.cross(side),
	width = to - from,
	height = RIBBON_PX * PX,
)

/**
 * A CSS 3D scene with no canvas or WebGL: the entity at the origin on a floor grid, the world axes in the F3 colors, the display [kind]
 * drawn with [model] (a dashed outline of the untransformed one when [ghost] is set) and a dot on its pivot.
 * Units are Minecraft's (X east, Y up, Z south). It is static unless [onCamera] is given, then dragging turns the camera and the arrow keys
 * step it by 10 degrees. A [compact] scene is smaller, for diagrams.
 */
@Composable
internal fun DisplayScene(
	kind: DisplayKind,
	model: DisplayMat4,
	camera: DisplayCamera,
	label: String,
	floorCells: Int,
	ghost: DisplayMat4?,
	compact: Boolean = false,
	onCamera: ((DisplayCamera) -> Unit)? = null,
) {
	Style(DisplaySceneStyle)

	val cameraState = rememberUpdatedState(camera)
	val onCameraState = rememberUpdatedState(onCamera)
	val pivotView = (
		DisplayMat4.rotX(-camera.pitch.degreesToRadians()) * DisplayMat4.rotY(camera.yaw.degreesToRadians()) *
			DisplayMat4.scaling(DisplayVec3(1.0, -1.0, 1.0)) * DisplayMat4.translation(DisplayVec3(0.0, -0.5 * BLOCK_PX, 0.0))
		).transform(model.transform(DisplayVec3.ZERO) * BLOCK_PX.toDouble())
	val zoom = if (compact) ZOOM * COMPACT_ZOOM else ZOOM
	val dotScale = (PERSPECTIVE_PX - DOT_SCREEN_Z) / (PERSPECTIVE_PX - zoom * pivotView.z)
	val dotSize = DOT_SCREEN_PX * (PERSPECTIVE_PX - DOT_SCREEN_Z) / PERSPECTIVE_PX / zoom

	Div({
		classes(DisplaySceneStyle.stage)
		if (compact) classes(DisplaySceneStyle.stageCompact)
		attr("aria-label", label)
		if (onCamera == null) {
			attr("role", "img")
		} else {
			classes(DisplaySceneStyle.stageInteractive)
			attr("role", "group")
			attr("tabindex", "0")
			onKeyDown { event ->
				val turn = when (event.key) {
					"ArrowLeft" -> cameraState.value.turned(-KEY_DEGREES, 0.0)
					"ArrowRight" -> cameraState.value.turned(KEY_DEGREES, 0.0)
					"ArrowUp" -> cameraState.value.turned(0.0, KEY_DEGREES)
					"ArrowDown" -> cameraState.value.turned(0.0, -KEY_DEGREES)
					"Home" -> DisplayCamera()
					else -> null
				} ?: return@onKeyDown
				event.preventDefault()
				onCamera(turn)
			}
			ref { stage ->
				var dragging = false
				var start = DisplayCamera()
				var startX = 0
				var startY = 0
				val onDown: (Event) -> Unit = { event ->
					val pointer = event.unsafeCast<MouseEvent>()
					dragging = true
					start = cameraState.value
					startX = pointer.clientX
					startY = pointer.clientY
					stage.asDynamic().setPointerCapture(event.asDynamic().pointerId)
				}
				val onMove: (Event) -> Unit = { event ->
					val pointer = event.unsafeCast<MouseEvent>()
					if (dragging) onCameraState.value?.invoke(
						start.turned((pointer.clientX - startX) * DRAG_DEGREES_PER_PIXEL, (pointer.clientY - startY) * DRAG_DEGREES_PER_PIXEL)
					)
				}
				val onEnd: (Event) -> Unit = { dragging = false }
				stage.addEventListener("pointerdown", onDown)
				stage.addEventListener("pointermove", onMove)
				stage.addEventListener("pointerup", onEnd)
				stage.addEventListener("pointercancel", onEnd)
				onDispose {
					stage.removeEventListener("pointerdown", onDown)
					stage.removeEventListener("pointermove", onMove)
					stage.removeEventListener("pointerup", onEnd)
					stage.removeEventListener("pointercancel", onEnd)
				}
			}
		}
	}) {
		Div({ classes(DisplaySceneStyle.node, if (compact) DisplaySceneStyle.zoomCompact else DisplaySceneStyle.zoom) }) {
			Div({
				classes(DisplaySceneStyle.node)
				style {
					transform {
						rotateX((-camera.pitch).deg)
						rotateY(camera.yaw.deg)
						scale3d(1, -1, 1)
						translate3d(0.px, (-0.5 * BLOCK_PX).px, 0.px)
					}
				}
			}) {
				val half = floorCells / 2.0
				Face(
					DisplayFace(DisplayVec3(-half, -PX, -half), vec(1, 0, 0), vec(0, 0, 1), vec(0, 1, 0), floorCells.toDouble(), floorCells.toDouble()),
					DisplaySceneStyle.floor,
					backVisible = true,
				)

				Face(ribbon(DisplayAxis.X.unit, DisplayAxis.Z.unit, -0.5, 0.5, -PX / 2), DisplaySceneStyle.entityMarker, backVisible = true)
				Face(ribbon(DisplayAxis.Z.unit, DisplayAxis.X.unit, -0.5, 0.5, -PX / 2), DisplaySceneStyle.entityMarker, backVisible = true)

				DisplayAxis.entries.forEach { axis ->
					DisplayAxis.entries.filter { it != axis }.forEach { side ->
						Face(ribbon(axis.unit, side.unit, 0.0, AXIS_LENGTH), backVisible = true, extra = { backgroundColor(Color(axis.color)) })
					}
					Billboarded(axis.unit * (AXIS_LENGTH + 0.12), camera, DisplaySceneStyle.axisLabel, extra = { color(Color(axis.color)) }) {
						Text(axis.name)
					}
				}

				ghost?.let { matrix ->
					Div({
						classes(DisplaySceneStyle.node)
						style { transform { matrix3d(matrix) } }
					}) {
						kind.faces.forEach { Face(it, DisplaySceneStyle.ghostFace, backVisible = true) }
					}
				}

				Div({
					classes(DisplaySceneStyle.node)
					style { transform { matrix3d(model) } }
				}) {
					kind.faces.forEach { ModelFace(kind, it) }
				}

			}

			Div({
				classes(DisplaySceneStyle.pivotDot)
				style {
					height(dotSize.px)
					width(dotSize.px)
					transform {
						translate3d((pivotView.x * dotScale).px, (pivotView.y * dotScale).px, (DOT_SCREEN_Z / zoom).px)
						translate((-50).percent, (-50).percent)
					}
				}
			})
		}
	}
}

@Composable
private fun Face(
	face: DisplayFace,
	vararg classes: String,
	backVisible: Boolean = false,
	overlap: Boolean = false,
	extra: StyleScope.() -> Unit = {},
	content: @Composable ElementScope<HTMLDivElement>.() -> Unit = {},
) {
	Div({
		classes(DisplaySceneStyle.face, *classes)
		style {
			height((face.height * BLOCK_PX).px)
			transform {
				matrix3d(face)
				if (overlap) {
					val w = face.width * BLOCK_PX
					val h = face.height * BLOCK_PX
					translate((w / 2).px, (h / 2).px)
					scale(1 + SEAM_PX / w, 1 + SEAM_PX / h)
					translate((-w / 2).px, (-h / 2).px)
				}
			}
			width((face.width * BLOCK_PX).px)
			if (backVisible) backfaceVisibility(BackfaceVisibility.Visible)
			extra()
		}
	}) { content() }
}

@Composable
private fun ModelFace(kind: DisplayKind, face: DisplayFace) {
	if (kind == DisplayKind.TEXT) {
		val svg = """<svg viewBox="0 0 ${McFont.width(DISPLAY_TEXT) + 2} 10" width="100%" height="100%" shape-rendering="crispEdges"><path transform="translate(1 1)" fill="#fff" d="${McFont.path(DISPLAY_TEXT)}"/></svg>"""
		Face(face, DisplaySceneStyle.textFace, backVisible = true) {
			DisposableEffect(svg) {
				scopeElement.innerHTML = svg
				onDispose {}
			}
		}
		return
	}

	if (kind == DisplayKind.ITEM) {
		Div({
			classes(DisplaySceneStyle.node)
			style { transform { scale3d(1, -1, 1) } }
		}) { McItemModel(face.texture!!, (face.width * BLOCK_PX / ITEM_TEXELS).toInt()) }
		return
	}

	Face(
		face,
		overlap = true,
		extra = {
			face.texture?.let { backgroundImage(mcTexture(it)) }
			if (face.tinted) {
				backgroundBlendMode(BackgroundBlendMode.Multiply)
				backgroundColor(GRASS_TINT)
			}
		},
	)
}

/** A flat element at [position] (in blocks) turned back toward the camera and lifted in front of the model, so nothing hides it. */
@Composable
private fun Billboarded(position: DisplayVec3, camera: DisplayCamera, className: String, extra: StyleScope.() -> Unit = {}, content: @Composable () -> Unit = {}) {
	Div({
		classes(className)
		style {
			transform {
				translate3d((position.x * BLOCK_PX).px, (position.y * BLOCK_PX).px, (position.z * BLOCK_PX).px)
				rotateY((-camera.yaw).deg)
				rotateX((-camera.pitch).deg)
				scale3d(1, -1, 1)
				translateZ(LABEL_LIFT_PX.px)
				translate((-50).percent, (-50).percent)
			}
			extra()
		}
	}) { content() }
}

internal object DisplaySceneStyle : StyleSheet() {
	val axisLabel by style {
		fontSize((12 / ZOOM).px)
		fontWeight(700)
		left(0.px)
		monoFont()
		pointerEvents(PointerEvents.None)
		position(Position.Absolute)
		top(0.px)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
		textAlign(TextAlign.Center)
	}

	val cell by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
		minWidth(0.px)
	}

	val diagram by style {
		display(DisplayStyle.Grid)
		gap(0.9.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val entityMarker by style {
		backgroundColor(LandingVars.Muted.value().alpha(0.8))
	}

	val face by style {
		backfaceVisibility(BackfaceVisibility.Hidden)
		backgroundRepeat(BackgroundRepeat.NoRepeat)
		backgroundSize(BackgroundSize.of(100.percent, 100.percent))
		imageRendering(ImageRendering.Pixelated)
		left(0.px)
		position(Position.Absolute)
		top(0.px)
		transformOrigin(TransformOrigin.of(0.px, 0.px))

		"svg" style {
			display(DisplayStyle.Block)
		}
	}

	/** The grid draws one cell per block, from two 1 px lines. */
	val floor by style {
		val line = LandingVars.Border.value()
		val cell = BackgroundSize.of(BLOCK_PX.px, BLOCK_PX.px)
		background(
			Background.list(
				Background.of(
					BackgroundImage.of(linearGradient(LinearGradient.Direction.ToRight) {
						add(line, 0.px, LINE_PX.px)
						add(Color.transparent, LINE_PX.px)
					}),
					size = cell,
				),
				Background.of(
					BackgroundImage.of(linearGradient(LinearGradient.Direction.ToBottom) {
						add(line, 0.px, LINE_PX.px)
						add(Color.transparent, LINE_PX.px)
					}),
					size = cell,
				),
			)
		)
	}

	val ghostFace by style {
		border(LINE_PX.px, LineStyle.Dashed, LandingVars.Muted.value().alpha(0.7))
		boxSizing(BoxSizing.BorderBox)
	}

	/** Nodes are zero-sized points the 3D transforms of their children turn around. */
	val node by style {
		height(0.px)
		left(0.px)
		position(Position.Absolute)
		top(0.px)
		transformOrigin(TransformOrigin.of(0.px, 0.px))
		transformStyle(TransformStyle.Preserve3d)
		width(0.px)
	}

	val pivotDot by style {
		backgroundColor(LandingVars.Gold.value())
		borderRadius(50.percent)
		left(0.px)
		pointerEvents(PointerEvents.None)
		position(Position.Absolute)
		top(0.px)
	}

	/** Clips the stage only: `overflow` on a 3D ancestor of the faces would flatten them. */
	val stage by style {
		backgroundColor(LandingVars.Surface.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.8.cssRem)
		boxSizing(BoxSizing.BorderBox)
		height(20.cssRem)
		maxWidth(100.percent)
		minWidth(0.px)
		overflow(Overflow.Clip)
		perspective(PERSPECTIVE_PX.px)
		position(Position.Relative)
		userSelect(UserSelect.None)

		smMax(self) {
			height(16.cssRem)
		}
	}

	val stageCompact by style {
		height(13.cssRem)

		smMax(self) {
			height(13.cssRem)
		}
	}

	val stageInteractive by style {
		cursor(Cursor.Grab)
		touchAction(TouchAction.PanY)

		self + focusVisible style {
			outline(Outline.of(2.px, LineStyle.Solid, LandingVars.Accent.value()))
			outlineOffset(2.px)
		}
	}

	val textFace by style {
		backgroundColor(TEXT_BACKGROUND)
	}

	val zoom by style {
		left(50.percent)
		scale(ZOOM, ZOOM, ZOOM)
		top(50.percent)

		smMax(self) {
			scale(ZOOM * SMALL_ZOOM, ZOOM * SMALL_ZOOM, ZOOM * SMALL_ZOOM)
		}
	}

	val zoomCompact by style {
		left(50.percent)
		scale(ZOOM * COMPACT_ZOOM, ZOOM * COMPACT_ZOOM, ZOOM * COMPACT_ZOOM)
		top(50.percent)
	}
}

/** Three scenes side by side, each display type at its entity position with its pivot marked by a dot. */
@Composable
fun DisplayPivotDiagram() {
	Style(DisplaySceneStyle)

	Div({ classes(DisplaySceneStyle.diagram) }) {
		DisplayKind.entries.forEach { kind ->
			Div({ classes(DisplaySceneStyle.cell) }) {
				DisplayScene(
					kind = kind,
					model = DisplayMat4.IDENTITY,
					camera = DisplayCamera(),
					label = "A ${kind.entity} at its entity position, the ${kind.subject} pivoting on its ${kind.pivot}, marked by a dot.",
					floorCells = 2,
					ghost = null,
					compact = true,
				)
				Div({ classes(DisplaySceneStyle.caption) }) { Text("${kind.entity}: ${kind.pivot}") }
			}
		}
	}
}
